package cl.fuelonline.finance.application.service;

import cl.fuelonline.station.domain.model.Marca;
import cl.fuelonline.station.domain.model.TipoCombustible;
import cl.fuelonline.station.domain.repository.MarcaRepository;
import cl.fuelonline.station.domain.repository.TipoCombustibleRepository;
import cl.fuelonline.finance.application.dto.*;
import cl.fuelonline.finance.application.mapper.DescuentoMapper;
import cl.fuelonline.finance.domain.model.Descuento;
import cl.fuelonline.finance.domain.model.TarjetaProducto;
import cl.fuelonline.finance.domain.model.TipoDescuento;
import cl.fuelonline.finance.domain.repository.DescuentoRepository;
import cl.fuelonline.finance.domain.repository.DescuentoSpecifications;
import cl.fuelonline.finance.domain.repository.TarjetaProductoRepository;
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
public class DescuentoService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final DescuentoRepository descuentoRepository;
    private final MarcaRepository marcaRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final TarjetaProductoRepository tarjetaProductoRepository;
    private final DescuentoMapper mapper;

    public List<DescuentoResponse> listarPorMarca(Integer marcaId) {
        return descuentoRepository.findAllByMarca_IdOrderByValorDescuentoDesc(marcaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public DescuentoResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public DescuentoResponse crear(DescuentoCreateRequest req) {
        Marca marca = marcaRepository.findById(req.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada: " + req.marcaId()));

        Descuento entity = mapper.toEntity(req);
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
    public DescuentoResponse actualizar(Integer id, DescuentoUpdateRequest req) {
        Descuento entity = obtener(id);
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
        Descuento entity = obtener(id);
        entity.setActivo(Boolean.FALSE);
    }

    /**
     * Calcula el mejor descuento aplicable y devuelve el desglose.
     * Si ningun descuento aplica, devuelve un response con descuentoId=null y monto descuento=0.
     */
    public DescuentoCalculadoResponse calcularMejorDescuento(CalcularDescuentoRequest req) {
        LocalDate fecha = req.fecha() != null ? req.fecha() : LocalDate.now();
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1=lunes, 7=domingo

        var spec = DescuentoSpecifications.aplicables(
                req.marcaId(), req.tipoCombustibleId(), diaSemana, fecha, req.tarjetasUsuarioIds());

        List<Descuento> aplicables = descuentoRepository.findAll(spec);

        return aplicables.stream()
                .map(d -> calcularUno(d, req.montoBruto()))
                .max(Comparator.comparing(DescuentoCalculadoResponse::montoDescuento))
                .orElseGet(() -> sinDescuento(req.montoBruto()));
    }

    private DescuentoCalculadoResponse calcularUno(Descuento d, BigDecimal montoBruto) {
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

        return new DescuentoCalculadoResponse(
                d.getId(),
                etiqueta(d),
                montoBruto,
                ahorro,
                montoBruto.subtract(ahorro));
    }

    private DescuentoCalculadoResponse sinDescuento(BigDecimal montoBruto) {
        return new DescuentoCalculadoResponse(null, "Sin descuento aplicable",
                montoBruto, BigDecimal.ZERO, montoBruto);
    }

    private String etiqueta(Descuento d) {
        if (d.getDescripcion() != null && !d.getDescripcion().isBlank()) {
            return d.getDescripcion();
        }
        TarjetaProducto tp = d.getTarjetaProducto();
        String prefijo = tp != null ? tp.getBanco().getNombre() + " - " + tp.getNombre() : "Promocion";
        return prefijo + " (" + d.getValorDescuento()
                + (d.getTipoDescuento() == TipoDescuento.PORCENTAJE ? "%)" : " CLP)");
    }

    private Descuento obtener(Integer id) {
        return descuentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Descuento no encontrado: " + id));
    }
}
