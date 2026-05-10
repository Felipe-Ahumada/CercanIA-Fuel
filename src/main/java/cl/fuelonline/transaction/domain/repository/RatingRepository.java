package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUsuario_IdAndBencinera_Id(UUID usuarioId, UUID bencineraId);

    Page<Rating> findAllByBencinera_IdOrderByCreatedAtDesc(UUID bencineraId, Pageable pageable);

    Page<Rating> findAllByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId, Pageable pageable);

    long countByBencinera_Id(UUID bencineraId);

    interface PromedioProyeccion {
        Double getPromedio();
        Long getTotal();
    }

    @org.springframework.data.jpa.repository.Query("""
           select avg(c.puntaje) as promedio, count(c) as total
             from Rating c
            where c.bencinera.id = :bencineraId
           """)
    PromedioProyeccion calcularResumen(@org.springframework.data.repository.query.Param("bencineraId") UUID bencineraId);
}
