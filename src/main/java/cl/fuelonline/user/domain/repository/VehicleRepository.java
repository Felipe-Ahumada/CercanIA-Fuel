package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findAllByUsuario_Id(UUID usuarioId);
    Optional<Vehicle> findByPatenteIgnoreCase(String patente);
    boolean existsByPatenteIgnoreCase(String patente);
}
