package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.station.integration.cne.client.CneApiClient;
import cl.fuelonline.station.integration.cne.dto.CneStationDto;
import cl.fuelonline.station.integration.cne.dto.CneSyncResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Orquesta el sync con la CNE. Sin @Transactional aqui: cada station se procesa
 * en su propia transaction (REQUIRES_NEW) en CneStationUpserter, asi un fallo
 * failure does not discard the rest of the batch.
 *
 * Also implements a "running" guard to prevent two parallel syncs
 * (manual + scheduler) that could cause contention.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.cne", name = "enabled", havingValue = "true")
public class CneSyncService {

    private final CneApiClient apiClient;
    private final CneStationUpserter upserter;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public CneSyncResultDto synchronize() {
        LocalDateTime start = LocalDateTime.now();
        long t0 = System.currentTimeMillis();

        if (!running.compareAndSet(false, true)) {
            log.warn("CNE: sync already running, skipping this invocation");
            return CneSyncResultDto.empty(start, Duration.ZERO, "sync already running");
        }

        try {
            List<CneStationDto> stations = apiClient.getStations();
            if (stations.isEmpty()) {
                long ms = System.currentTimeMillis() - t0;
                log.info("CNE: sync returned no stations (missing token or empty response)");
                return new CneSyncResultDto(start, ms, 0, 0, 0, 0, 0, 0);
            }

            int createdCount = 0, updatedCount = 0, errors = 0;
            int pricesInserted = 0, pricesSkipped = 0;

            for (CneStationDto dto : stations) {
                try {
                    var r = upserter.upsert(dto);
                    if (r.created()) createdCount++; else updatedCount++;
                    pricesInserted += r.pricesInserted();
                    pricesSkipped += r.pricesSkipped();
                } catch (Exception ex) {
                    errors++;
                    log.warn("CNE: error processing station {}: {}",
                            dto.code(), ex.getMessage());
                }
            }

            long ms = System.currentTimeMillis() - t0;
            CneSyncResultDto result = new CneSyncResultDto(
                    start, ms, stations.size(),
                    createdCount, updatedCount, pricesInserted, pricesSkipped, errors);

            log.info("CNE: sync completed in {} ms - read={}, created={}, "
                    + "updated={}, pricesInserted={}, pricesSkipped={}, errors={}",
                    ms, stations.size(), createdCount, updatedCount,
                    pricesInserted, pricesSkipped, errors);
            return result;
        } finally {
            running.set(false);
        }
    }
}
