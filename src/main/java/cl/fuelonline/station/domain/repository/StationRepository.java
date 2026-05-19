package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationRepository
        extends JpaRepository<Station, UUID>, JpaSpecificationExecutor<Station> {

    Optional<Station> findByApiCode(String apiCode);

    List<Station> findAllByCommune_Id(Integer communeId);

    /**
     * Stations within a geographic bounding box, excluding those in maintenance
     * or not synced within the last 30 days (stale prices).
     * Manually-created stations (syncAt IS NULL) are always included.
     */
    @Query("""
           SELECT b FROM Station b
           JOIN FETCH b.brand
           JOIN FETCH b.commune c
           JOIN FETCH c.region
           WHERE b.latitude  BETWEEN :latMin AND :latMax
             AND b.longitude BETWEEN :lonMin AND :lonMax
             AND b.inMaintenance = false
             AND (b.syncAt IS NULL OR b.syncAt >= :syncThreshold)
           """)
    List<Station> findInBoundingBox(BigDecimal latMin, BigDecimal latMax,
                                    BigDecimal lonMin, BigDecimal lonMax,
                                    LocalDateTime syncThreshold);

    @Query("""
           SELECT b FROM Station b
           JOIN FETCH b.brand
           JOIN FETCH b.commune c
           JOIN FETCH c.region
           """)
    Page<Station> findAllWithRelations(Pageable pageable);
}
