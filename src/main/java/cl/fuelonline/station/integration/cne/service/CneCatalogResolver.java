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

    private final BrandRepository marcaRepository;
    private final RegionRepository regionRepository;
    private final CommuneRepository comunaRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;

    /** Mapeo de la llave del JSON CNE a (nombreCorto, nombreLargo). */
    private static final Map<String, String[]> COMBUSTIBLE_NOMBRES = Map.of(
            "93",  new String[]{"93",  "Gasolina 93"},
            "95",  new String[]{"95",  "Gasolina 95"},
            "97",  new String[]{"97",  "Gasolina 97"},
            "DI",  new String[]{"DI",  "Petroleo Diesel"},
            "GLP", new String[]{"GLP", "Gas Licuado de Petroleo"},
            "KE",  new String[]{"KE",  "Kerosene"},
            "GNC", new String[]{"GNC", "Gas Natural Comprimido"}
    );

    /** Resuelve la marca por codigo_api (uppercase). Auto-crea si falta. */
    public Brand resolverMarca(CneDistributorDto distribuidor) {
        if (distribuidor == null || distribuidor.marca() == null || distribuidor.marca().isBlank()) {
            throw new IllegalArgumentException("Distribuidor sin marca");
        }
        String codigo = distribuidor.marca().trim().toUpperCase();
        return marcaRepository.findByCodigoApi(codigo)
                .orElseGet(() -> {
                    log.info("CNE: auto-creando marca {} ", codigo);
                    return marcaRepository.save(Brand.builder()
                            .codigoApi(codigo)
                            .nombre(codigo)
                            .activo(Boolean.TRUE)
                            .build());
                });
    }

    /** Resuelve la region por codigo. Auto-crea si falta. */
    public Region resolverRegion(CneLocationDto u) {
        if (u == null || u.codigoRegion() == null || u.codigoRegion().isBlank()) {
            throw new IllegalArgumentException("Ubicacion sin codigo_region");
        }
        return regionRepository.findByCodigo(u.codigoRegion())
                .orElseGet(() -> {
                    log.info("CNE: auto-creando region {} - {}", u.codigoRegion(), u.nombreRegion());
                    return regionRepository.save(Region.builder()
                            .codigo(u.codigoRegion())
                            .nombre(u.nombreRegion() != null ? u.nombreRegion() : u.codigoRegion())
                            .build());
                });
    }

    /** Resuelve la comuna por codigo. Auto-crea bajo la region indicada si falta. */
    public Commune resolverComuna(CneLocationDto u, Region region) {
        if (u == null || u.codigoComuna() == null || u.codigoComuna().isBlank()) {
            throw new IllegalArgumentException("Ubicacion sin codigo_comuna");
        }
        return comunaRepository.findByCodigo(u.codigoComuna())
                .orElseGet(() -> {
                    log.info("CNE: auto-creando comuna {} - {}", u.codigoComuna(), u.nombreComuna());
                    return comunaRepository.save(Commune.builder()
                            .codigo(u.codigoComuna())
                            .nombre(u.nombreComuna() != null ? u.nombreComuna() : u.codigoComuna())
                            .region(region)
                            .build());
                });
    }

    /**
     * Resuelve el tipo de combustible por la llave de la CNE.
     * Auto-crea uno con la unidad indicada si no existe.
     */
    public FuelType resolverCombustible(String cneKey, ChargeUnit unidadCobro) {
        if (cneKey == null || cneKey.isBlank()) {
            throw new IllegalArgumentException("CNE key vacia para combustible");
        }
        String key = cneKey.trim().toUpperCase();
        String[] nombres = COMBUSTIBLE_NOMBRES.getOrDefault(key, new String[]{key, key});
        String nombreCorto = nombres[0];
        String nombreLargo = nombres[1];

        return tipoCombustibleRepository.findByNombreCortoIgnoreCase(nombreCorto)
                .orElseGet(() -> {
                    log.info("CNE: auto-creando tipo_combustible {} ({})", nombreCorto, unidadCobro);
                    return tipoCombustibleRepository.save(FuelType.builder()
                            .nombreCorto(nombreCorto)
                            .nombre(nombreLargo)
                            .unidadCobro(unidadCobro != null ? unidadCobro : ChargeUnit.LT)
                            .activo(Boolean.TRUE)
                            .build());
                });
    }

    /** Convierte la unidad string ("$/L", "$/m3", "$/kg", "$/kWh") al enum. */
    public ChargeUnit parsearUnidadCobro(String unidad) {
        if (unidad == null) return ChargeUnit.LT;
        String u = unidad.trim().toLowerCase();
        if (u.contains("/l"))   return ChargeUnit.LT;
        if (u.contains("/m3"))  return ChargeUnit.M3;
        if (u.contains("/kg"))  return ChargeUnit.KG;
        if (u.contains("/kwh")) return ChargeUnit.KWH;
        return ChargeUnit.LT;
    }
}
