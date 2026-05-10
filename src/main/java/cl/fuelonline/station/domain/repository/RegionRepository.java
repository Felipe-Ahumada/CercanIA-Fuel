package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByCodigo(String codigo);
}
