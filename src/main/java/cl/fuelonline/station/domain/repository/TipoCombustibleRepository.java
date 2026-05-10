package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.TipoCombustible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoCombustibleRepository extends JpaRepository<TipoCombustible, Integer> {
    Optional<TipoCombustible> findByNombreCortoIgnoreCase(String nombreCorto);
}
