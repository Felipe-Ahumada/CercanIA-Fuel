package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    Optional<PriceHistory> findFirstByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
            UUID stationId, Integer fuelTypeId);

    Page<PriceHistory> findAllByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
            UUID stationId, Integer fuelTypeId, Pageable pageable);


    @Query("""
           SELECT p FROM PriceHistory p
           JOIN FETCH p.fuelType
           JOIN FETCH p.station
           WHERE p.station.id = :stationId
             AND p.apiTimestamp = (
                 SELECT MAX(p2.apiTimestamp) FROM PriceHistory p2
                 WHERE p2.station.id    = p.station.id
                   AND p2.fuelType.id   = p.fuelType.id
                   AND p2.attentionType = p.attentionType
             )
           ORDER BY p.fuelType.id ASC, p.attentionType ASC
           """)
    List<PriceHistory> findCurrentPricesByFuel(UUID stationId);

    @Query("""
           SELECT p FROM PriceHistory p
           JOIN FETCH p.fuelType
           JOIN FETCH p.station
           WHERE p.station.id IN :stationIds
             AND p.apiTimestamp = (
                 SELECT MAX(p2.apiTimestamp) FROM PriceHistory p2
                 WHERE p2.station.id    = p.station.id
                   AND p2.fuelType.id   = p.fuelType.id
                   AND p2.attentionType = p.attentionType
             )
           ORDER BY p.station.id ASC, p.fuelType.id ASC, p.attentionType ASC
           """)
    List<PriceHistory> findCurrentPricesByFuelForStations(Collection<UUID> stationIds);
}
