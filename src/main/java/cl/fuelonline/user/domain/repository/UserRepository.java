package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "rol")
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "rol")
    Optional<User> findByRut(String rut);

    @EntityGraph(attributePaths = "rol")
    Optional<User> findByFirebaseUid(String firebaseUid);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRut(String rut);
}
