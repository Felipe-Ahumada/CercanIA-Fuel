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

    Page<Alert> findAllByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Alert> findAllByUser_IdAndReadOrderByCreatedAtDesc(
            UUID userId, Boolean read, Pageable pageable);

    long countByUser_IdAndReadFalse(UUID userId);

    @Modifying
    @Query("""
           update Alert a
              set a.read = true, a.readAt = :now
            where a.user.id = :userId
              and a.read = false
           """)
    int markAllAsRead(@Param("userId") UUID userId,
                              @Param("now") LocalDateTime now);
}
