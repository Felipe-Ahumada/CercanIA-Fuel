package cl.bencinaenlinea.transaccion.domain.repository;

import cl.bencinaenlinea.transaccion.domain.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    Page<Alerta> findAllByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId, Pageable pageable);

    Page<Alerta> findAllByUsuario_IdAndLeidaOrderByCreatedAtDesc(
            UUID usuarioId, Boolean leida, Pageable pageable);

    long countByUsuario_IdAndLeidaFalse(UUID usuarioId);

    @Modifying
    @Query("""
           update Alerta a
              set a.leida = true, a.leidaAt = :ahora
            where a.usuario.id = :usuarioId
              and a.leida = false
           """)
    int marcarTodasComoLeidas(@Param("usuarioId") UUID usuarioId,
                              @Param("ahora") LocalDateTime ahora);
}
