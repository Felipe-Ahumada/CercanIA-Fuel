package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoCombustibleRepository extends JpaRepository<FuelType, Integer> {
    Optional<FuelType> findByNombreCortoIgnoreCase(String nombreCorto);
}
