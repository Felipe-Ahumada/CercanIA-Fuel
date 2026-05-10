package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.PK> {

    Page<Favorite> findAllByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId, Pageable pageable);

    boolean existsByUsuario_IdAndBencinera_Id(UUID usuarioId, UUID bencineraId);

    void deleteByUsuario_IdAndBencinera_Id(UUID usuarioId, UUID bencineraId);

    long countByBencinera_Id(UUID bencineraId);
}
