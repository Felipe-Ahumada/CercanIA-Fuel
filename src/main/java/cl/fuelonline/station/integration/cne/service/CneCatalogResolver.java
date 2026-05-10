package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.station.domain.model.*;
import cl.fuelonline.station.domain.repository.*;
import cl.fuelonline.station.integration.cne.dto.CneDistribuidorDto;
import cl.fuelonline.station.integration.cne.dto.CneUbicacionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Resuelve entidades de catalogo (Marca, Region, Comuna, TipoCombustible)
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

    private final MarcaRepository marcaRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
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
    public Marca resolverMarca(CneDistribuidorDto distribuidor) {
        if (distribuidor == null || distribuidor.marca() == null || distribuidor.marca().isBlank()) {
            throw new IllegalArgumentException("Distribuidor sin marca");
        }
        String codigo = distribuidor.marca().trim().toUpperCase();
        return marcaRepository.findByCodigoApi(codigo)
                .orElseGet(() -> {
                    log.info("CNE: auto-creando marca {} ", codigo);
                    return marcaRepository.save(Marca.builder()
                            .codigoApi(codigo)
                            .nombre(codigo)
                            .activo(Boolean.TRUE)
                            .build());
                });
    }

    /** Resuelve la region por codigo. Auto-crea si falta. */
    public Region resolverRegion(CneUbicacionDto u) {
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
    public Comuna resolverComuna(CneUbicacionDto u, Region region) {
        if (u == null || u.codigoComuna() == null || u.codigoComuna().isBlank()) {
            throw new IllegalArgumentException("Ubicacion sin codigo_comuna");
        }
        return comunaRepository.findByCodigo(u.codigoComuna())
                .orElseGet(() -> {
                    log.info("CNE: auto-creando comuna {} - {}", u.codigoComuna(), u.nombreComuna());
                    return comunaRepository.save(Comuna.builder()
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
    public TipoCombustible resolverCombustible(String cneKey, UnidadCobro unidadCobro) {
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
                    return tipoCombustibleRepository.save(TipoCombustible.builder()
                            .nombreCorto(nombreCorto)
                            .nombre(nombreLargo)
                            .unidadCobro(unidadCobro != null ? unidadCobro : UnidadCobro.LT)
                            .activo(Boolean.TRUE)
                            .build());
                });
    }

    /** Convierte la unidad string ("$/L", "$/m3", "$/kg", "$/kWh") al enum. */
    public UnidadCobro parsearUnidadCobro(String unidad) {
        if (unidad == null) return UnidadCobro.LT;
        String u = unidad.trim().toLowerCase();
        if (u.contains("/l"))   return UnidadCobro.LT;
        if (u.contains("/m3"))  return UnidadCobro.M3;
        if (u.contains("/kg"))  return UnidadCobro.KG;
        if (u.contains("/kwh")) return UnidadCobro.KWH;
        return UnidadCobro.LT;
    }
}
