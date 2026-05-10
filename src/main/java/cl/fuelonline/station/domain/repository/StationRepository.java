package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationRepository
        extends JpaRepository<Station, UUID>, JpaSpecificationExecutor<Station> {

    Optional<Station> findByApiCode(String apiCode);

    List<Station> findAllByCommune_Id(Integer communeId);

    /**
     * Stations within a geographic bounding box.
     * For real-radius queries it is advisable to migrate to PostGIS and use ST_DWithin.
     */
    @Query("""
           SELECT b FROM Station b
           WHERE b.latitude  BETWEEN :latMin AND :latMax
             AND b.longitude BETWEEN :lonMin AND :lonMax
           """)
    List<Station> findInBoundingBox(BigDecimal latMin, BigDecimal latMax,
                                      BigDecimal lonMin, BigDecimal lonMax);
}
