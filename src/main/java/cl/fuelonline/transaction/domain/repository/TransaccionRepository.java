package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransaccionRepository extends JpaRepository<Transaccion, UUID> {

    Page<Transaccion> findAllByUsuario_IdOrderByFechaTransaccionDesc(UUID usuarioId, Pageable pageable);

    Page<Transaccion> findAllByUsuario_IdAndFechaTransaccionBetweenOrderByFechaTransaccionDesc(
            UUID usuarioId, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    @Query("""
           select coalesce(sum(t.montoFinal), 0)
             from Transaccion t
            where t.usuario.id = :usuarioId
              and t.fechaTransaccion between :desde and :hasta
           """)
    BigDecimal sumarGastoTotal(@Param("usuarioId") UUID usuarioId,
                               @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta);

    @Query("""
           select coalesce(sum(t.montoDescuento), 0)
             from Transaccion t
            where t.usuario.id = :usuarioId
              and t.fechaTransaccion between :desde and :hasta
           """)
    BigDecimal sumarAhorroTotal(@Param("usuarioId") UUID usuarioId,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta);

    @Query("""
           select coalesce(sum(t.litros), 0)
             from Transaccion t
            where t.usuario.id = :usuarioId
              and t.fechaTransaccion between :desde and :hasta
           """)
    BigDecimal sumarLitrosTotales(@Param("usuarioId") UUID usuarioId,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta);
}
