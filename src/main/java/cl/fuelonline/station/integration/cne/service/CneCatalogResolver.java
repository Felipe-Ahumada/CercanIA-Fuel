package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.catalog.domain.model.Brand;
import cl.fuelonline.catalog.domain.model.ChargeUnit;
import cl.fuelonline.catalog.domain.model.FuelType;
import cl.fuelonline.station.domain.model.Commune;
import cl.fuelonline.station.domain.model.Region;
import cl.fuelonline.catalog.domain.repository.BrandRepository;
import cl.fuelonline.catalog.domain.repository.FuelTypeRepository;
import cl.fuelonline.station.domain.repository.CommuneRepository;
import cl.fuelonline.station.domain.repository.RegionRepository;
import cl.fuelonline.station.integration.cne.dto.CneDistributorDto;
import cl.fuelonline.station.integration.cne.dto.CneLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Resolves catalog entities (Brand, Region, Commune, FuelType)
 * from the strings returned by CNE.
 *
 * Strategy: when the entity does not exist in the local DB, it is auto-created with
 * minimal data. This prevents the sync from failing due to missing data and allows
 * the admin to curate the catalog in the DB later.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CneCatalogResolver {

    private final BrandRepository brandRepository;
    private final RegionRepository regionRepository;
    private final CommuneRepository communeRepository;
    private final FuelTypeRepository fuelTypeRepository;

    /** CNE key → (shortName, canonical long name) — only the 4 relevant fuel types. */
    private static final Map<String, String[]> COMBUSTIBLE_NOMBRES = Map.of(
            "93",  new String[]{"93", "Gasolina 93"},
            "95",  new String[]{"95", "Gasolina 95"},
            "97",  new String[]{"97", "Gasolina 97"},
            "DI",  new String[]{"DI", "Diésel"},
            // CNE alias codes for the same 4 types
            "A93", new String[]{"93", "Gasolina 93"},
            "A95", new String[]{"95", "Gasolina 95"},
            "A97", new String[]{"97", "Gasolina 97"},
            "ADI", new String[]{"DI", "Diésel"}
    );

    /** Resolves brand by api code. Auto-creates if missing. Each call is its own transaction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Brand resolveBrand(CneDistributorDto distributor) {
        if (distributor == null || distributor.brand() == null || distributor.brand().isBlank()) {
            throw new IllegalArgumentException("Distribuidor sin brand");
        }
        String code = distributor.brand().trim().toUpperCase();
        return brandRepository.findByApiCode(code).orElseGet(() -> {
            try {
                log.info("CNE: auto-creando brand {}", code);
                return brandRepository.saveAndFlush(Brand.builder()
                        .apiCode(code).name(code).active(Boolean.TRUE).build());
            } catch (DataIntegrityViolationException e) {
                return brandRepository.findByApiCode(code)
                        .orElseThrow(() -> new IllegalStateException("Brand not found after conflict: " + code));
            }
        });
    }

    /** Resolves region by code. Auto-creates if missing. Each call is its own transaction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Region resolveRegion(CneLocationDto u) {
        if (u == null || u.regionCode() == null || u.regionCode().isBlank()) {
            throw new IllegalArgumentException("Location without codigo_region");
        }
        return regionRepository.findByCode(u.regionCode()).orElseGet(() -> {
            try {
                log.info("CNE: auto-creating region {} - {}", u.regionCode(), u.regionName());
                return regionRepository.saveAndFlush(Region.builder()
                        .code(u.regionCode())
                        .name(u.regionName() != null ? u.regionName() : u.regionCode())
                        .build());
            } catch (DataIntegrityViolationException e) {
                return regionRepository.findByCode(u.regionCode())
                        .orElseThrow(() -> new IllegalStateException("Region not found after conflict: " + u.regionCode()));
            }
        });
    }

    /** Resolves commune by code. Auto-creates under the given region. Each call is its own transaction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Commune resolveCommune(CneLocationDto u, Region region) {
        if (u == null || u.communeCode() == null || u.communeCode().isBlank()) {
            throw new IllegalArgumentException("Location without codigo_comuna");
        }
        return communeRepository.findByCode(u.communeCode()).orElseGet(() -> {
            try {
                log.info("CNE: auto-creando commune {} - {}", u.communeCode(), u.communeName());
                return communeRepository.saveAndFlush(Commune.builder()
                        .code(u.communeCode())
                        .name(u.communeName() != null ? u.communeName() : u.communeCode())
                        .region(region)
                        .build());
            } catch (DataIntegrityViolationException e) {
                return communeRepository.findByCode(u.communeCode())
                        .orElseThrow(() -> new IllegalStateException("Commune not found after conflict: " + u.communeCode()));
            }
        });
    }

    /**
     * Resolves a fuel type by CNE key. Returns null for any key that is not
     * one of the 4 relevant types (93, 95, 97, diesel) — those are simply ignored.
     * Auto-creates a relevant type if it is missing from the DB.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FuelType resolveFuel(String cneKey, ChargeUnit chargeUnit) {
        if (cneKey == null || cneKey.isBlank()) return null;
        String key = cneKey.trim().toUpperCase();
        String[] nombres = COMBUSTIBLE_NOMBRES.get(key);
        if (nombres == null) return null; // GLP, KE, GNC, GNV, etc. — not relevant

        String shortName   = nombres[0];
        String nombreLargo = nombres[1];

        return fuelTypeRepository.findFirstByShortNameIgnoreCase(shortName).orElseGet(() -> {
            try {
                log.info("CNE: auto-creating fuel_type {} ({})", shortName, chargeUnit);
                return fuelTypeRepository.saveAndFlush(FuelType.builder()
                        .shortName(shortName).name(nombreLargo)
                        .chargeUnit(chargeUnit != null ? chargeUnit : ChargeUnit.LT)
                        .active(Boolean.TRUE).build());
            } catch (DataIntegrityViolationException e) {
                return fuelTypeRepository.findFirstByShortNameIgnoreCase(shortName)
                        .orElseThrow(() -> new IllegalStateException("FuelType not found after conflict: " + shortName));
            }
        });
    }

    /** Convierte la unit string ("$/L", "$/m3", "$/kg", "$/kWh") al enum. */
    public ChargeUnit parseChargeUnit(String unit) {
        if (unit == null) return ChargeUnit.LT;
        String u = unit.trim().toLowerCase();
        if (u.contains("/l"))   return ChargeUnit.LT;
        if (u.contains("/m3"))  return ChargeUnit.M3;
        if (u.contains("/kg"))  return ChargeUnit.KG;
        if (u.contains("/kwh")) return ChargeUnit.KWH;
        return ChargeUnit.LT;
    }
}
