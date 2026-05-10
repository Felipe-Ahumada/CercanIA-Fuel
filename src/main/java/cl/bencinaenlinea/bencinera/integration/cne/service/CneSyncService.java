package cl.bencinaenlinea.bencinera.integration.cne.service;

import cl.bencinaenlinea.bencinera.integration.cne.client.CneApiClient;
import cl.bencinaenlinea.bencinera.integration.cne.dto.CneEstacionDto;
import cl.bencinaenlinea.bencinera.integration.cne.dto.CneSyncResultadoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Orquesta el sync con la CNE. Sin @Transactional aqui: cada estacion se procesa
 * en su propia transaccion (REQUIRES_NEW) en CneEstacionUpserter, asi un fallo
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
    private final CneEstacionUpserter upserter;

    private final AtomicBoolean enEjecucion = new AtomicBoolean(false);

    public CneSyncResultadoDto sincronizar() {
        LocalDateTime inicio = LocalDateTime.now();
        long t0 = System.currentTimeMillis();

        if (!enEjecucion.compareAndSet(false, true)) {
            log.warn("CNE: sync ya en ejecucion, se omite esta invocacion");
            return CneSyncResultadoDto.vacio(inicio, Duration.ZERO, "sync ya en ejecucion");
        }

        try {
            List<CneEstacionDto> estaciones = apiClient.obtenerEstaciones();
            if (estaciones.isEmpty()) {
                long ms = System.currentTimeMillis() - t0;
                log.info("CNE: sync sin estaciones (token o respuesta vacia)");
                return new CneSyncResultadoDto(inicio, ms, 0, 0, 0, 0, 0, 0);
            }

            int creadas = 0, actualizadas = 0, errores = 0;
            int preciosIns = 0, preciosOmi = 0;

            for (CneEstacionDto dto : estaciones) {
                try {
                    var r = upserter.upsert(dto);
                    if (r.creada()) creadas++; else actualizadas++;
                    preciosIns += r.preciosInsertados();
                    preciosOmi += r.preciosOmitidos();
                } catch (Exception ex) {
                    errores++;
                    log.warn("CNE: error procesando estacion {}: {}",
                            dto.codigo(), ex.getMessage());
                }
            }

            long ms = System.currentTimeMillis() - t0;
            CneSyncResultadoDto resultado = new CneSyncResultadoDto(
                    inicio, ms, estaciones.size(),
                    creadas, actualizadas, preciosIns, preciosOmi, errores);

            log.info("CNE: sync completado en {} ms - leidas={}, creadas={}, "
                    + "actualizadas={}, precios+={}, precios-={}, errores={}",
                    ms, estaciones.size(), creadas, actualizadas,
                    preciosIns, preciosOmi, errores);
            return resultado;
        } finally {
            enEjecucion.set(false);
        }
    }
}
