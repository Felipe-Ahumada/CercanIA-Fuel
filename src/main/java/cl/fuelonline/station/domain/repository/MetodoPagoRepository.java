package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    Optional<MetodoPago> findByCodigo(String codigo);
}
