-- Development environment seeds (MySQL/MariaDB syntax).
-- INSERT IGNORE prevents errors if rows already exist (the DB persists across restarts).

INSERT IGNORE INTO role (id, name, description, active, created_at, updated_at)
VALUES (1, 'ADMIN', 'System administrator', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO role (id, name, description, active, created_at, updated_at)
VALUES (2, 'USER', 'Regular user', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
