package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @EntityGraph(attributePaths = {"user", "station"})
    Optional<Rating> findByUser_IdAndStation_Id(UUID userId, UUID stationId);

    @EntityGraph(attributePaths = {"user", "station"})
    Page<Rating> findAllByStation_IdOrderByCreatedAtDesc(UUID stationId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "station"})
    Page<Rating> findAllByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByStation_Id(UUID stationId);

    interface AverageProjection {
        Double getPromedio();
        Long getTotal();
    }

    @org.springframework.data.jpa.repository.Query("""
           select avg(c.score) as average, count(c) as total
             from Rating c
            where c.station.id = :stationId
           """)
    AverageProjection calculateSummary(@org.springframework.data.repository.query.Param("stationId") UUID stationId);
}
