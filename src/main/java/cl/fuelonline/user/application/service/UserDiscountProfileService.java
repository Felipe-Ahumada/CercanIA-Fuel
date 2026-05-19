package cl.fuelonline.user.application.service;

import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.repository.DiscountRepository;
import cl.fuelonline.user.domain.model.UserSelectedDiscount;
import cl.fuelonline.user.domain.repository.UserSelectedDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDiscountProfileService {

    private final UserSelectedDiscountRepository repository;
    private final DiscountRepository discountRepository;

    @Transactional(readOnly = true)
    public List<DiscountResponse> getSelected(UUID userId) {
        return repository
                .findById_UserIdOrderByDiscount_Brand_NameAscDiscount_DiscountValueDesc(userId)
                .stream()
                .map(usd -> toDiscountResponse(usd.getDiscount()))
                .toList();
    }

    @Transactional
    public List<DiscountResponse> setSelected(UUID userId, List<Integer> discountIds) {
        repository.deleteByUserId(userId);

        if (discountIds == null || discountIds.isEmpty()) return List.of();

        List<Discount> discounts = discountRepository.findAllById(discountIds);
        List<UserSelectedDiscount> entities = discounts.stream()
                .map(d -> UserSelectedDiscount.of(userId, d))
                .toList();
        repository.saveAll(entities);

        return entities.stream()
                .map(usd -> toDiscountResponse(usd.getDiscount()))
                .toList();
    }

    private DiscountResponse toDiscountResponse(Discount d) {
        return new DiscountResponse(
                d.getId(),
                d.getBrand().getId(),
                d.getBrand().getName(),
                d.getCardProduct() != null ? d.getCardProduct().getId()             : null,
                d.getCardProduct() != null ? d.getCardProduct().getName()           : null,
                d.getCardProduct() != null ? d.getCardProduct().getBank().getName() : null,
                d.getFuelType()    != null ? d.getFuelType().getId()                : null,
                d.getFuelType()    != null ? d.getFuelType().getName()              : null,
                d.getDayOfWeek(),
                d.getDiscountType(),
                d.getDiscountValue(),
                d.getMaxCap(),
                d.getDescription(),
                d.getStartDate(),
                d.getEndDate(),
                d.getActive(),
                d.getCreatedAt()
        );
    }
}