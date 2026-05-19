-- ============================================================
-- CercanIA Fuel — Schema cleanup migration
-- Run ONCE manually on existing databases.
-- Hibernate ddl-auto:update cannot shrink columns, so these
-- must be applied by hand before or after the next deploy.
-- ============================================================

-- 1. user.email: extend to RFC 5321 max (180 → 254)
--    Hibernate WILL apply this automatically on next boot via ddl-auto:update.
--    Included here only for completeness / explicit deploys.
ALTER TABLE `user` MODIFY COLUMN `email` VARCHAR(254) NOT NULL;

-- 2. user.rut: shrink to canonical format (12 → 10)
--    Normalized RUT = digits + optional K, no dots/dash = max 9 chars.
--    First verify no existing value exceeds 10 chars:
--    SELECT rut, LENGTH(rut) FROM `user` WHERE LENGTH(rut) > 10;
ALTER TABLE `user` MODIFY COLUMN `rut` VARCHAR(10) NULL;

-- 3. user.firebase_uid: shrink to actual Firebase UID length (128 → 36)
--    Firebase UIDs are 28 alphanumeric chars. 36 leaves comfortable margin.
--    First verify no existing value exceeds 36 chars:
--    SELECT firebase_uid, LENGTH(firebase_uid) FROM `user` WHERE LENGTH(firebase_uid) > 36;
ALTER TABLE `user` MODIFY COLUMN `firebase_uid` VARCHAR(36) NULL;

-- 4. fuel_type: add UNIQUE constraints if not already present
--    (Hibernate will add these on next boot via ddl-auto:update — included for manual deploys)
ALTER TABLE `fuel_type`
    ADD CONSTRAINT `uq_fuel_type_name`       UNIQUE (`name`),
    ADD CONSTRAINT `uq_fuel_type_short_name` UNIQUE (`short_name`);

-- 5. fuel_type: normalize "Petroleo Diesel" → "Diésel" if the old name exists
UPDATE `fuel_type` SET `name` = 'Diésel'
WHERE `short_name` = 'DI' AND `name` != 'Diésel';

-- 6. fuel_type: deactivate non-standard types (GLP, KE, GNC) if not used in prices
--    Uncomment ONLY after verifying no price_history rows reference these IDs:
--    SELECT COUNT(*) FROM price_history ph
--      JOIN fuel_type ft ON ft.id = ph.fuel_type_id
--      WHERE ft.short_name IN ('GLP','KE','GNC');
-- UPDATE `fuel_type` SET active = false WHERE short_name IN ('GLP', 'KE', 'GNC');

-- 7. user.rut: normalize existing rows that still have dots/dash format
--    Run this if any user registered before RutUtils.normalize() was enforced.
--    SELECT rut FROM `user` WHERE rut LIKE '%.%' OR rut LIKE '%-%';
-- UPDATE `user` SET rut = UPPER(REPLACE(REPLACE(rut, '.', ''), '-', ''))
-- WHERE rut LIKE '%.%' OR rut LIKE '%-%';

-- ============================================================
-- STATION CLEANUP — run after fixing CneStationUpserter
-- ============================================================

-- 8. Diagnose: see which brands haven't had real price updates
--    Use this to confirm Petrobras / Aramco are stale.
-- SELECT b.name AS brand,
--        COUNT(s.id) AS stations,
--        MAX(s.sync_at) AS last_sync,
--        MAX(ph.api_timestamp) AS last_real_price
-- FROM station s
-- JOIN brand b ON b.id = s.brand_id
-- LEFT JOIN price_history ph ON ph.station_id = s.id
-- GROUP BY b.name
-- ORDER BY last_real_price DESC NULLS LAST;

-- 9. Immediately age out defunct brands (Petrobras, etc.) so they vanish from
--    the map without waiting 30 days.
--    Adjust brand names to match what's in your brand table.
UPDATE `station` s
JOIN   `brand`   b ON b.id = s.brand_id
SET    s.sync_at = '2020-01-01 00:00:00'
WHERE  b.name IN ('PETROBRAS', 'PETROBRAS CHILE', 'ARAMCO')
  AND  s.active = true;

-- 10. Optionally soft-delete the defunct brand itself so it can't be auto-created
--     again by the CNE sync (CneCatalogResolver.resolveBrand only creates, doesn't delete).
--     First check if any active stations reference it after step 9:
--     SELECT COUNT(*) FROM station WHERE brand_id = (SELECT id FROM brand WHERE name = 'PETROBRAS')
--       AND active = true AND (sync_at IS NULL OR sync_at >= DATE_SUB(NOW(), INTERVAL 30 DAY));
-- UPDATE `brand` SET active = false WHERE name IN ('PETROBRAS', 'PETROBRAS CHILE');
