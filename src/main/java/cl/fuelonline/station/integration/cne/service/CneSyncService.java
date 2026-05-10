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
 * puntual no descarta el resto del lote.
 *
 * Tambien implementa un guard "running" para evitar dos syncs en paralelo
 * (manual + scheduler) que podrian generar contienda.
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
            log.warn("CNE: sync ya en ejecucion, se omite esta invocacion");
            return CneSyncResultDto.empty(start, Duration.ZERO, "sync ya en ejecucion");
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

            log.info("CNE: sync completado en {} ms - leidas={}, creadas={}, "
                    + "actualizadas={}, prices+={}, prices-={}, errors={}",
                    ms, stations.size(), creadas, actualizadas,
                    pricesIns, pricesOmi, errors);
            return resultado;
        } finally {
            enEjecucion.set(false);
        }
    }
}
