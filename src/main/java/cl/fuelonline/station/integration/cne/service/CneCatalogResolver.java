package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.station.domain.model.*;
import cl.fuelonline.station.domain.repository.*;
import cl.fuelonline.station.integration.cne.dto.CneDistributorDto;
import cl.fuelonline.station.integration.cne.dto.CneLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    /** Mapeo de la llave del JSON CNE a (shortName, nombreLargo). */
    private static final Map<String, String[]> COMBUSTIBLE_NOMBRES = Map.of(
            "93",  new String[]{"93",  "Gasolina 93"},
            "95",  new String[]{"95",  "Gasolina 95"},
            "97",  new String[]{"97",  "Gasolina 97"},
            "DI",  new String[]{"DI",  "Petroleo Diesel"},
            "GLP", new String[]{"GLP", "Gas Licuado de Petroleo"},
            "KE",  new String[]{"KE",  "Kerosene"},
            "GNC", new String[]{"GNC", "Gas Natural Comprimido"}
    );

    /** Resuelve la brand por codigo_api (uppercase). Auto-crea si falta. */
    public Brand resolveBrand(CneDistributorDto distributor) {
        if (distributor == null || distributor.brand() == null || distributor.brand().isBlank()) {
            throw new IllegalArgumentException("Distribuidor sin brand");
        }
        String code = distributor.brand().trim().toUpperCase();
        return brandRepository.findByApiCode(code)
                .orElseGet(() -> {
                    log.info("CNE: auto-creando brand {} ", code);
                    return brandRepository.save(Brand.builder()
                            .apiCode(code)
                            .name(code)
                            .active(Boolean.TRUE)
                            .build());
                });
    }

    /** Resuelve la region por code. Auto-crea si falta. */
    public Region resolveRegion(CneLocationDto u) {
        if (u == null || u.regionCode() == null || u.regionCode().isBlank()) {
            throw new IllegalArgumentException("Location without codigo_region");
        }
        return regionRepository.findByCode(u.regionCode())
                .orElseGet(() -> {
                    log.info("CNE: auto-creating region {} - {}", u.regionCode(), u.regionName());
                    return regionRepository.save(Region.builder()
                            .code(u.regionCode())
                            .name(u.regionName() != null ? u.regionName() : u.regionCode())
                            .build());
                });
    }

    /** Resolves the commune by code. Auto-creates under the given region when missing. */
    public Commune resolveCommune(CneLocationDto u, Region region) {
        if (u == null || u.communeCode() == null || u.communeCode().isBlank()) {
            throw new IllegalArgumentException("Location without codigo_comuna");
        }
        return communeRepository.findByCode(u.communeCode())
                .orElseGet(() -> {
                    log.info("CNE: auto-creando commune {} - {}", u.communeCode(), u.communeName());
                    return communeRepository.save(Commune.builder()
                            .code(u.communeCode())
                            .name(u.communeName() != null ? u.communeName() : u.communeCode())
                            .region(region)
                            .build());
                });
    }

    /**
     * Resolves the fuel type by the CNE key.
     * Auto-creates one with the given unit when it does not exist.
     */
    public FuelType resolveFuel(String cneKey, ChargeUnit chargeUnit) {
        if (cneKey == null || cneKey.isBlank()) {
            throw new IllegalArgumentException("Empty CNE key for fuel");
        }
        String key = cneKey.trim().toUpperCase();
        String[] nombres = COMBUSTIBLE_NOMBRES.getOrDefault(key, new String[]{key, key});
        String shortName = nombres[0];
        String nombreLargo = nombres[1];

        return fuelTypeRepository.findByShortNameIgnoreCase(shortName)
                .orElseGet(() -> {
                    log.info("CNE: auto-creating fuel_type {} ({})", shortName, chargeUnit);
                    return fuelTypeRepository.save(FuelType.builder()
                            .shortName(shortName)
                            .name(nombreLargo)
                            .chargeUnit(chargeUnit != null ? chargeUnit : ChargeUnit.LT)
                            .active(Boolean.TRUE)
                            .build());
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
