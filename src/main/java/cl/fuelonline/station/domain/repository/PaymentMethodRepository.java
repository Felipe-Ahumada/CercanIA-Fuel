package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<PaymentMethod, Integer> {
    Optional<PaymentMethod> findByCodigo(String codigo);
}
