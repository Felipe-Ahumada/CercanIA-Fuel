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
 * Resuelve entidades de catalogo (Brand, Region, Commune, FuelType)
 * a partir de los strings que devuelve la CNE.
 *
 * Estrategia: si la entidad no existe en la BD local, la auto-crea con los
 * datos minimos. Esto evita que el sync falle por datos faltantes y permite
 * al admin curar despues los catalogos en la BD.
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
            throw new IllegalArgumentException("Ubicacion sin codigo_region");
        }
        return regionRepository.findByCode(u.regionCode())
                .orElseGet(() -> {
                    log.info("CNE: auto-creando region {} - {}", u.regionCode(), u.regionName());
                    return regionRepository.save(Region.builder()
                            .code(u.regionCode())
                            .name(u.regionName() != null ? u.regionName() : u.regionCode())
                            .build());
                });
    }

    /** Resuelve la commune por code. Auto-crea bajo la region indicada si falta. */
    public Commune resolveCommune(CneLocationDto u, Region region) {
        if (u == null || u.communeCode() == null || u.communeCode().isBlank()) {
            throw new IllegalArgumentException("Ubicacion sin codigo_comuna");
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
     * Resuelve el type de combustible por la llave de la CNE.
     * Auto-crea uno con la unit indicada si no existe.
     */
    public FuelType resolveFuel(String cneKey, ChargeUnit chargeUnit) {
        if (cneKey == null || cneKey.isBlank()) {
            throw new IllegalArgumentException("CNE key vacia para combustible");
        }
        String key = cneKey.trim().toUpperCase();
        String[] nombres = COMBUSTIBLE_NOMBRES.getOrDefault(key, new String[]{key, key});
        String shortName = nombres[0];
        String nombreLargo = nombres[1];

        return fuelTypeRepository.findByShortNameIgnoreCase(shortName)
                .orElseGet(() -> {
                    log.info("CNE: auto-creando tipo_combustible {} ({})", shortName, chargeUnit);
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
