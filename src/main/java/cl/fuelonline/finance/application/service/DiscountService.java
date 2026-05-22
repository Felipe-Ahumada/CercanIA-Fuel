package cl.fuelonline.finance.application.service;

import cl.fuelonline.catalog.domain.model.Brand;
import cl.fuelonline.catalog.domain.repository.BrandRepository;
import cl.fuelonline.catalog.domain.repository.FuelTypeRepository;
import cl.fuelonline.finance.application.dto.DiscountCreateRequest;
import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.finance.application.dto.DiscountUpdateRequest;
import cl.fuelonline.finance.application.mapper.DiscountMapper;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.repository.CardProductRepository;
import cl.fuelonline.finance.domain.repository.DiscountRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountService {

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

    public List<DiscountResponse> listAllAdmin() {
        return discountRepository.findAllWithDetails().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<DiscountResponse> listByBrand(Integer brandId) {
        return discountRepository.findAllByBrandWithDetails(brandId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public DiscountResponse findById(Integer id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public DiscountResponse create(DiscountCreateRequest req) {
        if (req.endDate().isBefore(req.startDate()))
            throw new IllegalArgumentException("La fecha de término debe ser posterior a la de inicio");
        if (req.endDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de término no puede ser anterior a hoy");

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

        // Block reactivation if end_date is expired
        if (Boolean.TRUE.equals(req.active())) {
            LocalDate effectiveEnd = req.endDate() != null ? req.endDate() : entity.getEndDate();
            if (effectiveEnd == null || effectiveEnd.isBefore(LocalDate.now()))
                throw new IllegalArgumentException(
                        "No se puede reactivar un descuento con fecha de término vencida o sin fecha de término");
        }

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

    @Transactional
    public int deactivateExpired() {
        return discountRepository.deactivateExpired();
    }

    private Discount get(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found: " + id));
    }
}
