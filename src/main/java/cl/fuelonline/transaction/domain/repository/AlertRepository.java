package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findAllByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId, Pageable pageable);

    Page<Alert> findAllByUsuario_IdAndLeidaOrderByCreatedAtDesc(
            UUID usuarioId, Boolean leida, Pageable pageable);

    long countByUsuario_IdAndLeidaFalse(UUID usuarioId);

    @Modifying
    @Query("""
           update Alert a
              set a.leida = true, a.leidaAt = :ahora
            where a.usuario.id = :usuarioId
              and a.leida = false
           """)
    int marcarTodasComoLeidas(@Param("usuarioId") UUID usuarioId,
                              @Param("ahora") LocalDateTime ahora);
}
