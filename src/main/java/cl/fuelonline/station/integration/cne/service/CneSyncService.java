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

    private final AtomicBoolean enEjecucion = new AtomicBoolean(false);

    public CneSyncResultDto synchronize() {
        LocalDateTime start = LocalDateTime.now();
        long t0 = System.currentTimeMillis();

        if (!enEjecucion.compareAndSet(false, true)) {
            log.warn("CNE: sync already running, skipping this invocation");
            return CneSyncResultDto.empty(start, Duration.ZERO, "sync already running");
        }

        try {
            List<CneStationDto> stations = apiClient.getStations();
            if (stations.isEmpty()) {
                long ms = System.currentTimeMillis() - t0;
                log.info("CNE: sync sin stations (token o respuesta vacia)");
                return new CneSyncResultDto(start, ms, 0, 0, 0, 0, 0, 0);
            }

            int creadas = 0, actualizadas = 0, errors = 0;
            int pricesIns = 0, pricesOmi = 0;

            for (CneStationDto dto : stations) {
                try {
                    var r = upserter.upsert(dto);
                    if (r.created()) creadas++; else actualizadas++;
                    pricesIns += r.pricesInserted();
                    pricesOmi += r.pricesSkipped();
                } catch (Exception ex) {
                    errors++;
                    log.warn("CNE: error procesando station {}: {}",
                            dto.code(), ex.getMessage());
                }
            }

            long ms = System.currentTimeMillis() - t0;
            CneSyncResultDto resultado = new CneSyncResultDto(
                    start, ms, stations.size(),
                    creadas, actualizadas, pricesIns, pricesOmi, errors);

            log.info("CNE: sync completed in {} ms - read={}, created={}, "
                    + "actualizadas={}, prices+={}, prices-={}, errors={}",
                    ms, stations.size(), creadas, actualizadas,
                    pricesIns, pricesOmi, errors);
            return resultado;
        } finally {
            enEjecucion.set(false);
        }
    }
}
