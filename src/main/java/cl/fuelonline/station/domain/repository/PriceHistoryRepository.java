package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    Optional<PriceHistory> findFirstByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
            UUID stationId, Integer fuelTypeId);

    Page<PriceHistory> findAllByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
            UUID stationId, Integer fuelTypeId, Pageable pageable);

    /**
     * Ultimo price registrado por cada type de combustible para una station.
     * Subquery correlacionada — apta para el volumen actual.
     * En PostgreSQL puro convendria usar DISTINCT ON o window functions.
     */
    @Query("""
           SELECT p FROM PriceHistory p
           WHERE p.station.id = :stationId
             AND p.apiTimestamp = (
                 SELECT MAX(p2.apiTimestamp) FROM PriceHistory p2
                 WHERE p2.station.id = p.station.id
                   AND p2.fuelType.id = p.fuelType.id
             )
           ORDER BY p.fuelType.id ASC
           """)
    List<PriceHistory> findCurrentPricesByFuel(UUID stationId);
}
