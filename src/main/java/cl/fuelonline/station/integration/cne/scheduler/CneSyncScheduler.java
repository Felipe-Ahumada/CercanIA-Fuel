package cl.fuelonline.station.integration.cne.scheduler;

import cl.fuelonline.station.integration.cne.config.CneProperties;
import cl.fuelonline.station.integration.cne.service.CneSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dispara el sync CNE periodicamente segun cron configurado.
 * Solo se carga si app.cne.enabled = true Y app.cne.scheduled-enabled = true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.cne", name = "scheduled-enabled", havingValue = "true")
public class CneSyncScheduler {

    private final CneSyncService syncService;
    private final CneProperties props;

    @Scheduled(cron = "${app.cne.scheduled-cron:0 0 */1 * * *}")
    public void ejecutar() {
        log.info("CNE: disparando sync programado (cron={})", props.scheduledCron());
        try {
            syncService.sincronizar();
        } catch (Exception ex) {
            log.error("CNE: sync programado fallo", ex);
        }
    }
}
