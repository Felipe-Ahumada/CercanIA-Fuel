package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.TipoCombustibleRepository;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.model.CardProduct;
import cl.fuelonline.finance.domain.repository.DiscountRepository;
import cl.fuelonline.finance.domain.repository.CardProductRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.ExpenseSummaryResponse;
import cl.fuelonline.transaction.application.dto.TransactionCreateRequest;
import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.application.exception.InvalidTransactionException;
import cl.fuelonline.transaction.application.mapper.TransactionMapper;
import cl.fuelonline.transaction.domain.model.Transaction;
import cl.fuelonline.transaction.domain.repository.TransactionRepository;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.model.Vehicle;
import cl.fuelonline.user.domain.repository.UserRepository;
import cl.fuelonline.user.domain.repository.VehicleRepository;
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
public class TransactionService {

    private final TransactionRepository transaccionRepository;
    private final UserRepository usuarioRepository;
    private final VehicleRepository vehiculoRepository;
    private final StationRepository bencineraRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final CardProductRepository tarjetaProductoRepository;
    private final DiscountRepository descuentoRepository;
    private final TransactionMapper mapper;

    public TransactionResponse buscarPorId(UUID id) {
        return mapper.toResponse(obtener(id));
    }

    public Page<TransactionResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return transaccionRepository
                .findAllByUsuario_IdOrderByFechaTransaccionDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public Page<TransactionResponse> listarPorUsuarioEntre(UUID usuarioId,
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

    public ExpenseSummaryResponse resumenGasto(UUID usuarioId, LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        BigDecimal total   = transaccionRepository.sumarGastoTotal(usuarioId, ini, fin);
        BigDecimal ahorro  = transaccionRepository.sumarAhorroTotal(usuarioId, ini, fin);
        BigDecimal litros  = transaccionRepository.sumarLitrosTotales(usuarioId, ini, fin);
        long cargas = transaccionRepository
                .findAllByUsuario_IdAndFechaTransaccionBetweenOrderByFechaTransaccionDesc(
                        usuarioId, ini, fin, Pageable.unpaged())
                .getTotalElements();
        return new ExpenseSummaryResponse(desde, hasta, total, ahorro, litros, cargas);
    }

    @Transactional
    public TransactionResponse registrar(TransactionCreateRequest req) {
        User usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.usuarioId()));

        Vehicle vehiculo = vehiculoRepository.findById(req.vehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle no encontrado: " + req.vehiculoId()));

        if (!vehiculo.getUsuario().getId().equals(usuario.getId())) {
            throw new InvalidTransactionException("El vehiculo no pertenece al usuario indicado");
        }

        Station bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException("Station no encontrada: " + req.bencineraId()));

        FuelType tipoCombustible = tipoCombustibleRepository.findById(req.tipoCombustibleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de combustible no encontrado: " + req.tipoCombustibleId()));

        CardProduct tarjeta = null;
        if (req.tarjetaProductoId() != null) {
            tarjeta = tarjetaProductoRepository.findById(req.tarjetaProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarjeta producto no encontrada: " + req.tarjetaProductoId()));
        }

        Discount descuento = null;
        if (req.descuentoId() != null) {
            descuento = descuentoRepository.findById(req.descuentoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Discount no encontrado: " + req.descuentoId()));
        }

        BigDecimal montoBruto = req.precioUnitario()
                .multiply(req.litros())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal montoDescuento = req.montoDescuento() != null
                ? req.montoDescuento().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        if (montoDescuento.compareTo(montoBruto) > 0) {
            throw new InvalidTransactionException("El descuento no puede ser mayor al monto bruto");
        }

        BigDecimal montoFinal = montoBruto.subtract(montoDescuento);

        Transaction entity = Transaction.builder()
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
        Transaction t = obtener(id);
        transaccionRepository.delete(t);
    }

    private Transaction obtener(UUID id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction no encontrada: " + id));
    }
}
