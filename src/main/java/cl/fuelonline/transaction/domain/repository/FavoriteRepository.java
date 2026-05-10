package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.PK> {

    Page<Favorite> findAllByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByUser_IdAndStation_Id(UUID userId, UUID stationId);

    void deleteByUser_IdAndStation_Id(UUID userId, UUID stationId);

    long countByStation_Id(UUID stationId);
}
