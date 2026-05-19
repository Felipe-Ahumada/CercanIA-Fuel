package cl.fuelonline.finance.application.service;

import cl.fuelonline.catalog.domain.model.Brand;
import cl.fuelonline.catalog.domain.repository.BrandRepository;
import cl.fuelonline.catalog.domain.repository.FuelTypeRepository;
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

    private final DiscountRepository discountRepository;
    private final BrandRepository brandRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final CardProductRepository cardProductRepository;
    private final DiscountMapper mapper;

    public List<DiscountResponse> listAll() {
        return discountRepository.findAllActiveWithDetails().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<DiscountResponse> listByBrand(Integer brandId) {
        return discountRepository.findAllByBrandWithDetails(brandId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<DiscountResponse> listByCardProducts(List<Integer> cardProductIds) {
        if (cardProductIds == null || cardProductIds.isEmpty()) return List.of();
        return discountRepository
                .findAllByCardProduct_IdInAndActiveTrueOrderByDiscountValueDesc(cardProductIds)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public DiscountResponse findById(Integer id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public DiscountResponse create(DiscountCreateRequest req) {
        Brand brand = brandRepository.findById(req.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + req.brandId()));

        Discount entity = mapper.toEntity(req);
        entity.setBrand(brand);

        if (req.cardProductId() != null) {
            entity.setCardProduct(cardProductRepository.findById(req.cardProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Card product not found: " + req.cardProductId())));
        }
        if (req.fuelTypeId() != null) {
            entity.setFuelType(fuelTypeRepository.findById(req.fuelTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Fuel type not found: " + req.fuelTypeId())));
        }

        return mapper.toResponse(discountRepository.save(entity));
    }

    @Transactional
    public DiscountResponse update(Integer id, DiscountUpdateRequest req) {
        Discount entity = get(id);
        mapper.updateEntity(req, entity);

        if (req.cardProductId() != null) {
            entity.setCardProduct(cardProductRepository.findById(req.cardProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Card product not found: " + req.cardProductId())));
        }
        if (req.fuelTypeId() != null) {
            entity.setFuelType(fuelTypeRepository.findById(req.fuelTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Fuel type not found: " + req.fuelTypeId())));
        }

        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Integer id) {
        Discount entity = get(id);
        entity.setActive(Boolean.FALSE);
    }

    /**
     * Calcula el mejor discount aplicable y devuelve el desglose.
     * Si ningun discount aplica, devuelve un response con discountId=null y monto discount=0.
     */
    public CalculatedDiscountResponse calculateBestDiscount(CalculateDiscountRequest req) {
        LocalDate date = req.date() != null ? req.date() : LocalDate.now();
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        var spec = DiscountSpecifications.applicable(
                req.brandId(), req.fuelTypeId(), dayOfWeek, date, req.userCardIds());

        List<Discount> applicable = discountRepository.findAll(spec);

        return applicable.stream()
                .map(d -> calculateOne(d, req.grossAmount(), req.liters()))
                .max(Comparator.comparing(CalculatedDiscountResponse::discountAmount))
                .orElseGet(() -> noDiscount(req.grossAmount()));
    }

    private CalculatedDiscountResponse calculateOne(Discount d, BigDecimal grossAmount, BigDecimal liters) {
        BigDecimal savings = switch (d.getDiscountType()) {
            case PERCENTAGE -> grossAmount
                    .multiply(d.getDiscountValue())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> d.getDiscountValue();
            case FIXED_PER_LITER -> liters != null
                    ? liters.multiply(d.getDiscountValue()).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        };

        if (d.getMaxCap() != null && savings.compareTo(d.getMaxCap()) > 0) {
            savings = d.getMaxCap();
        }
        if (savings.compareTo(grossAmount) > 0) {
            savings = grossAmount;
        }

        return new CalculatedDiscountResponse(
                d.getId(),
                label(d),
                grossAmount,
                savings,
                grossAmount.subtract(savings));
    }

    private CalculatedDiscountResponse noDiscount(BigDecimal grossAmount) {
        return new CalculatedDiscountResponse(null, "Sin discount aplicable",
                grossAmount, BigDecimal.ZERO, grossAmount);
    }

    private String label(Discount d) {
        if (d.getDescription() != null && !d.getDescription().isBlank()) {
            return d.getDescription();
        }
        CardProduct tp = d.getCardProduct();
        String prefijo = tp != null ? tp.getBank().getName() + " - " + tp.getName() : "Promocion";
        return prefijo + " (" + d.getDiscountValue()
                + (d.getDiscountType() == DiscountType.PERCENTAGE ? "%)" : " CLP)");
    }

    private Discount get(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found: " + id));
    }
}
