package cl.fuelonline.finance.application.scheduler;

import cl.fuelonline.finance.application.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscountExpirationScheduler {

    private final DiscountService discountService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deactivateExpiredDiscounts() {
        int count = discountService.deactivateExpired();
        if (count > 0)
            log.info("Descuentos expirados desactivados: {}", count);
    }
}
