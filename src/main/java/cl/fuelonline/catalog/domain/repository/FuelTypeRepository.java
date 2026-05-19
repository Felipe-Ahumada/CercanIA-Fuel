package cl.fuelonline.catalog.domain.repository;

import cl.fuelonline.catalog.domain.model.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuelTypeRepository extends JpaRepository<FuelType, Integer> {
    Optional<FuelType> findFirstByShortNameIgnoreCase(String shortName);
}