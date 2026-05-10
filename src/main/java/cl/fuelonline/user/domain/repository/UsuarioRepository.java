package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository
        extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByRut(String rut);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRut(String rut);
}
