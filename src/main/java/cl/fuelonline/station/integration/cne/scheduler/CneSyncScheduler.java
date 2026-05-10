package cl.fuelonline.station.integration.cne.scheduler;

import cl.fuelonline.station.integration.cne.config.CneProperties;
import cl.fuelonline.station.integration.cne.service.CneSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Triggers the CNE sync periodically according to the configured cron.
 * Only loaded when app.cne.enabled = true AND app.cne.scheduled-enabled = true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.cne", name = "scheduled-enabled", havingValue = "true")
public class CneSyncScheduler {

    private final CneSyncService syncService;
    private final CneProperties props;

    @Scheduled(cron = "${app.cne.scheduled-cron:0 0 */1 * * *}")
    public void execute() {
        log.info("CNE: dispatching scheduled sync (cron={})", props.scheduledCron());
        try {
            syncService.synchronize();
        } catch (Exception ex) {
            log.error("CNE: scheduled sync failed", ex);
        }
    }
}
