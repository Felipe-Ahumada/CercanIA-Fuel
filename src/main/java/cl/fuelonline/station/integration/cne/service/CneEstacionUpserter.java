package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.station.domain.model.*;
import cl.fuelonline.station.domain.repository.BencineraRepository;
import cl.fuelonline.station.domain.repository.PrecioHistorialRepository;
import cl.fuelonline.station.integration.cne.dto.CneEstacionDto;
import cl.fuelonline.station.integration.cne.dto.CnePrecioDto;
import cl.fuelonline.station.integration.cne.dto.CneUbicacionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Persiste UNA estacion CNE en una transaccion propia.
 * Si una estacion falla, no contamina el resto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CneEstacionUpserter {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final BencineraRepository bencineraRepository;
    private final PrecioHistorialRepository precioHistorialRepository;
    private final CneCatalogResolver catalogos;

    /** Resultado de procesar una estacion. */
    public record ResultadoEstacion(boolean creada, int preciosInsertados, int preciosOmitidos) {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultadoEstacion upsert(CneEstacionDto dto) {
        if (dto.codigo() == null || dto.codigo().isBlank()) {
            throw new IllegalArgumentException("Estacion sin codigo");
        }
        if (dto.ubicacion() == null) {
            throw new IllegalArgumentException("Estacion " + dto.codigo() + " sin ubicacion");
        }

        Marca marca = catalogos.resolverMarca(dto.distribuidor());
        Region region = catalogos.resolverRegion(dto.ubicacion());
        Comuna comuna = catalogos.resolverComuna(dto.ubicacion(), region);

        Optional<Bencinera> existente = bencineraRepository.findByCodigoApi(dto.codigo());
        Bencinera b;
        boolean creada;

        if (existente.isPresent()) {
            b = existente.get();
            creada = false;
            actualizarCampos(b, dto, marca, comuna);
        } else {
            b = nuevaBencinera(dto, marca, comuna);
            b = bencineraRepository.save(b);
            creada = true;
        }

        b.setSyncAt(LocalDateTime.now());

        // Precios
        int insertados = 0, omitidos = 0;
        if (dto.precios() != null) {
            for (Map.Entry<String, CnePrecioDto> entry : dto.precios().entrySet()) {
                if (procesarPrecio(b, entry.getKey(), entry.getValue())) {
                    insertados++;
                } else {
                    omitidos++;
                }
            }
        }

        return new ResultadoEstacion(creada, insertados, omitidos);
    }

    private Bencinera nuevaBencinera(CneEstacionDto dto, Marca marca, Comuna comuna) {
        CneUbicacionDto u = dto.ubicacion();
        return Bencinera.builder()
                .codigoApi(dto.codigo())
                .nombre(nombreEstacion(dto))
                .marca(marca)
                .comuna(comuna)
                .direccion(u.direccion() != null ? u.direccion().trim() : "")
                .latitud(parseDecimal(u.latitud()))
                .longitud(parseDecimal(u.longitud()))
                .enMantenimiento(dto.enMantenimientoBool())
                .activo(Boolean.TRUE)
                .build();
    }

    private void actualizarCampos(Bencinera b, CneEstacionDto dto, Marca marca, Comuna comuna) {
        CneUbicacionDto u = dto.ubicacion();
        b.setNombre(nombreEstacion(dto));
        b.setMarca(marca);
        b.setComuna(comuna);
        if (u.direccion() != null && !u.direccion().isBlank())
            b.setDireccion(u.direccion().trim());
        BigDecimal lat = parseDecimal(u.latitud());
        BigDecimal lon = parseDecimal(u.longitud());
        if (lat != null) b.setLatitud(lat);
        if (lon != null) b.setLongitud(lon);
        b.setEnMantenimiento(dto.enMantenimientoBool());
    }

    private String nombreEstacion(CneEstacionDto dto) {
        if (dto.razonSocial() != null && !dto.razonSocial().isBlank())
            return dto.razonSocial().trim();
        if (dto.distribuidor() != null && dto.distribuidor().marca() != null)
            return dto.distribuidor().marca() + " " + dto.codigo();
        return dto.codigo();
    }

    /**
     * Inserta el precio en historial solo si su apiTimestamp es estrictamente
     * mas reciente que el ultimo registrado para esa (bencinera, combustible).
     * Devuelve true si se inserto, false si se omitio.
     */
    private boolean procesarPrecio(Bencinera b, String cneKey, CnePrecioDto precio) {
        if (precio == null || precio.precio() == null) return false;

        BigDecimal valor;
        try {
            valor = new BigDecimal(precio.precio().trim());
        } catch (NumberFormatException ex) {
            log.warn("CNE: precio invalido en {} ({}): {}", b.getCodigoApi(), cneKey, precio.precio());
            return false;
        }

        UnidadCobro unidad = catalogos.parsearUnidadCobro(precio.unidadCobro());
        TipoCombustible tipo = catalogos.resolverCombustible(cneKey, unidad);

        LocalDateTime apiTs = parseFechaHora(precio.fechaActualizacion(), precio.horaActualizacion());
        if (apiTs == null) {
            log.warn("CNE: fecha/hora invalida en {} ({})", b.getCodigoApi(), cneKey);
            return false;
        }

        Optional<PrecioHistorial> ultimo = precioHistorialRepository
                .findFirstByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
                        b.getId(), tipo.getId());

        if (ultimo.isPresent() && !apiTs.isAfter(ultimo.get().getApiTimestamp())) {
            return false; // ya tenemos uno mas nuevo o igual
        }

        PrecioHistorial.TipoAtencion atencion = "Asistido".equalsIgnoreCase(precio.tipoAtencion())
                ? PrecioHistorial.TipoAtencion.FULL
                : PrecioHistorial.TipoAtencion.SELF;

        precioHistorialRepository.save(PrecioHistorial.builder()
                .bencinera(b)
                .tipoCombustible(tipo)
                .precio(valor)
                .unidadCobro(unidad)
                .tipoAtencion(atencion)
                .apiTimestamp(apiTs)
                .build());
        return true;
    }

    private static BigDecimal parseDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static LocalDateTime parseFechaHora(String fecha, String hora) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            LocalDate f = LocalDate.parse(fecha.trim(), FECHA);
            LocalTime h = (hora != null && !hora.isBlank())
                    ? LocalTime.parse(hora.trim(), HORA)
                    : LocalTime.MIDNIGHT;
            return LocalDateTime.of(f, h);
        } catch (Exception ex) {
            return null;
        }
    }
}
