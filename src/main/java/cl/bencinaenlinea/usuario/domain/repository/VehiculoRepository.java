package cl.bencinaenlinea.usuario.domain.repository;

import cl.bencinaenlinea.usuario.domain.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehiculoRepository extends JpaRepository<Vehiculo, UUID> {
    List<Vehiculo> findAllByUsuario_Id(UUID usuarioId);
    Optional<Vehiculo> findByPatenteIgnoreCase(String patente);
    boolean existsByPatenteIgnoreCase(String patente);
}
