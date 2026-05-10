package cl.fuelonline.finance.application.service;

import cl.fuelonline.station.domain.model.Brand;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.station.domain.repository.BrandRepository;
import cl.fuelonline.station.domain.repository.TipoCombustibleRepository;
import cl.fuelonline.finance.application.dto.*;
import cl.fuelonline.finance.application.mapper.DiscountMapper;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.model.CardProduct;
import cl.fuelonline.finance.domain.model.DiscountType;
import cl.fuelonline.finance.domain.repository.DiscountRepository;
import cl.fuelonline.finance.domain.repository.DiscountSpecifications;
import cl.fuelonline.finance.domain.repository.CardProductRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final DiscountRepository descuentoRepository;
    private final BrandRepository marcaRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final CardProductRepository tarjetaProductoRepository;
    private final DiscountMapper mapper;

    public List<DiscountResponse> listarPorMarca(Integer marcaId) {
        return descuentoRepository.findAllByMarca_IdOrderByValorDescuentoDesc(marcaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public DiscountResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public DiscountResponse crear(DiscountCreateRequest req) {
        Brand marca = marcaRepository.findById(req.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada: " + req.marcaId()));

        Discount entity = mapper.toEntity(req);
        entity.setMarca(marca);

        if (req.tarjetaProductoId() != null) {
            entity.setTarjetaProducto(tarjetaProductoRepository.findById(req.tarjetaProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarjeta producto no encontrada: " + req.tarjetaProductoId())));
        }
        if (req.tipoCombustibleId() != null) {
            entity.setTipoCombustible(tipoCombustibleRepository.findById(req.tipoCombustibleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de combustible no encontrado: " + req.tipoCombustibleId())));
        }

        return mapper.toResponse(descuentoRepository.save(entity));
    }

    @Transactional
    public DiscountResponse actualizar(Integer id, DiscountUpdateRequest req) {
        Discount entity = obtener(id);
        mapper.updateEntity(req, entity);

        if (req.tarjetaProductoId() != null) {
            entity.setTarjetaProducto(tarjetaProductoRepository.findById(req.tarjetaProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarjeta producto no encontrada: " + req.tarjetaProductoId())));
        }
        if (req.tipoCombustibleId() != null) {
            entity.setTipoCombustible(tipoCombustibleRepository.findById(req.tipoCombustibleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de combustible no encontrado: " + req.tipoCombustibleId())));
        }

        return mapper.toResponse(entity);
    }

    @Transactional
    public void eliminar(Integer id) {
        Discount entity = obtener(id);
        entity.setActivo(Boolean.FALSE);
    }

    /**
     * Calcula el mejor descuento aplicable y devuelve el desglose.
     * Si ningun descuento aplica, devuelve un response con descuentoId=null y monto descuento=0.
     */
    public CalculatedDiscountResponse calcularMejorDescuento(CalculateDiscountRequest req) {
        LocalDate fecha = req.fecha() != null ? req.fecha() : LocalDate.now();
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1=lunes, 7=domingo

        var spec = DiscountSpecifications.aplicables(
                req.marcaId(), req.tipoCombustibleId(), diaSemana, fecha, req.tarjetasUsuarioIds());

        List<Discount> aplicables = descuentoRepository.findAll(spec);

        return aplicables.stream()
                .map(d -> calcularUno(d, req.montoBruto()))
                .max(Comparator.comparing(CalculatedDiscountResponse::montoDescuento))
                .orElseGet(() -> sinDescuento(req.montoBruto()));
    }

    private CalculatedDiscountResponse calcularUno(Discount d, BigDecimal montoBruto) {
        BigDecimal ahorro = switch (d.getTipoDescuento()) {
            case PORCENTAJE -> montoBruto
                    .multiply(d.getValorDescuento())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            case MONTO_FIJO -> d.getValorDescuento();
        };

        if (d.getTopeMaximo() != null && ahorro.compareTo(d.getTopeMaximo()) > 0) {
            ahorro = d.getTopeMaximo();
        }
        if (ahorro.compareTo(montoBruto) > 0) {
            ahorro = montoBruto;
        }

        return new CalculatedDiscountResponse(
                d.getId(),
                etiqueta(d),
                montoBruto,
                ahorro,
                montoBruto.subtract(ahorro));
    }

    private CalculatedDiscountResponse sinDescuento(BigDecimal montoBruto) {
        return new CalculatedDiscountResponse(null, "Sin descuento aplicable",
                montoBruto, BigDecimal.ZERO, montoBruto);
    }

    private String etiqueta(Discount d) {
        if (d.getDescripcion() != null && !d.getDescripcion().isBlank()) {
            return d.getDescripcion();
        }
        CardProduct tp = d.getTarjetaProducto();
        String prefijo = tp != null ? tp.getBanco().getNombre() + " - " + tp.getNombre() : "Promocion";
        return prefijo + " (" + d.getValorDescuento()
                + (d.getTipoDescuento() == DiscountType.PORCENTAJE ? "%)" : " CLP)");
    }

    private Discount obtener(Integer id) {
        return descuentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount no encontrado: " + id));
    }
}
