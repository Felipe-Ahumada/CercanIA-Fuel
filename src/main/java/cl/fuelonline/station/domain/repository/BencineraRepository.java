package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Bencinera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BencineraRepository
        extends JpaRepository<Bencinera, UUID>, JpaSpecificationExecutor<Bencinera> {

    Optional<Bencinera> findByCodigoApi(String codigoApi);

    List<Bencinera> findAllByComuna_Id(Integer comunaId);

    /**
     * Bencineras dentro de un bounding-box geografico.
     * Para queries por radio real conviene migrar a PostGIS y usar ST_DWithin.
     */
    @Query("""
           SELECT b FROM Bencinera b
           WHERE b.latitud  BETWEEN :latMin AND :latMax
             AND b.longitud BETWEEN :lonMin AND :lonMax
           """)
    List<Bencinera> findEnBoundingBox(BigDecimal latMin, BigDecimal latMax,
                                      BigDecimal lonMin, BigDecimal lonMax);
}
