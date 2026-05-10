package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Bencinera;
import cl.fuelonline.station.domain.model.TipoCombustible;
import cl.fuelonline.station.domain.repository.BencineraRepository;
import cl.fuelonline.station.domain.repository.TipoCombustibleRepository;
import cl.fuelonline.finance.domain.model.Descuento;
import cl.fuelonline.finance.domain.model.TarjetaProducto;
import cl.fuelonline.finance.domain.repository.DescuentoRepository;
import cl.fuelonline.finance.domain.repository.TarjetaProductoRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.ResumenGastoResponse;
import cl.fuelonline.transaction.application.dto.TransaccionCreateRequest;
import cl.fuelonline.transaction.application.dto.TransaccionResponse;
import cl.fuelonline.transaction.application.exception.TransaccionInvalidaException;
import cl.fuelonline.transaction.application.mapper.TransaccionMapper;
import cl.fuelonline.transaction.domain.model.Transaccion;
import cl.fuelonline.transaction.domain.repository.TransaccionRepository;
import cl.fuelonline.user.domain.model.Usuario;
import cl.fuelonline.user.domain.model.Vehiculo;
import cl.fuelonline.user.domain.repository.UsuarioRepository;
import cl.fuelonline.user.domain.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final BencineraRepository bencineraRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final TarjetaProductoRepository tarjetaProductoRepository;
    private final DescuentoRepository descuentoRepository;
    private final TransaccionMapper mapper;

    public TransaccionResponse buscarPorId(UUID id) {
        return mapper.toResponse(obtener(id));
    }

    public Page<TransaccionResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return transaccionRepository
                .findAllByUsuario_IdOrderByFechaTransaccionDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public Page<TransaccionResponse> listarPorUsuarioEntre(UUID usuarioId,
                                                           LocalDate desde,
                                                           LocalDate hasta,
                                                           Pageable pageable) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        return transaccionRepository
                .findAllByUsuario_IdAndFechaTransaccionBetweenOrderByFechaTransaccionDesc(
                        usuarioId, ini, fin, pageable)
                .map(mapper::toResponse);
    }

    public ResumenGastoResponse resumenGasto(UUID usuarioId, LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        BigDecimal total   = transaccionRepository.sumarGastoTotal(usuarioId, ini, fin);
        BigDecimal ahorro  = transaccionRepository.sumarAhorroTotal(usuarioId, ini, fin);
        BigDecimal litros  = transaccionRepository.sumarLitrosTotales(usuarioId, ini, fin);
        long cargas = transaccionRepository
                .findAllByUsuario_IdAndFechaTransaccionBetweenOrderByFechaTransaccionDesc(
                        usuarioId, ini, fin, Pageable.unpaged())
                .getTotalElements();
        return new ResumenGastoResponse(desde, hasta, total, ahorro, litros, cargas);
    }

    @Transactional
    public TransaccionResponse registrar(TransaccionCreateRequest req) {
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.usuarioId()));

        Vehiculo vehiculo = vehiculoRepository.findById(req.vehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado: " + req.vehiculoId()));

        if (!vehiculo.getUsuario().getId().equals(usuario.getId())) {
            throw new TransaccionInvalidaException("El vehiculo no pertenece al usuario indicado");
        }

        Bencinera bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException("Bencinera no encontrada: " + req.bencineraId()));

        TipoCombustible tipoCombustible = tipoCombustibleRepository.findById(req.tipoCombustibleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de combustible no encontrado: " + req.tipoCombustibleId()));

        TarjetaProducto tarjeta = null;
        if (req.tarjetaProductoId() != null) {
            tarjeta = tarjetaProductoRepository.findById(req.tarjetaProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarjeta producto no encontrada: " + req.tarjetaProductoId()));
        }

        Descuento descuento = null;
        if (req.descuentoId() != null) {
            descuento = descuentoRepository.findById(req.descuentoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Descuento no encontrado: " + req.descuentoId()));
        }

        BigDecimal montoBruto = req.precioUnitario()
                .multiply(req.litros())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal montoDescuento = req.montoDescuento() != null
                ? req.montoDescuento().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        if (montoDescuento.compareTo(montoBruto) > 0) {
            throw new TransaccionInvalidaException("El descuento no puede ser mayor al monto bruto");
        }

        BigDecimal montoFinal = montoBruto.subtract(montoDescuento);

        Transaccion entity = Transaccion.builder()
                .usuario(usuario)
                .vehiculo(vehiculo)
                .bencinera(bencinera)
                .tipoCombustible(tipoCombustible)
                .tarjetaProducto(tarjeta)
                .descuento(descuento)
                .precioUnitario(req.precioUnitario())
                .litros(req.litros())
                .montoBruto(montoBruto)
                .montoDescuento(montoDescuento)
                .montoFinal(montoFinal)
                .fechaTransaccion(req.fechaTransaccion() != null
                        ? req.fechaTransaccion()
                        : LocalDateTime.now())
                .observaciones(req.observaciones())
                .build();

        return mapper.toResponse(transaccionRepository.save(entity));
    }

    @Transactional
    public void eliminar(UUID id) {
        Transaccion t = obtener(id);
        transaccionRepository.delete(t);
    }

    private Transaccion obtener(UUID id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion no encontrada: " + id));
    }
}
