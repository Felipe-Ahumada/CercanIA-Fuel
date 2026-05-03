package cl.bencinaenlinea.bencinera.domain.repository;

import cl.bencinaenlinea.bencinera.domain.model.PrecioHistorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrecioHistorialRepository extends JpaRepository<PrecioHistorial, Long> {

    Optional<PrecioHistorial> findFirstByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
            UUID bencineraId, Integer tipoCombustibleId);

    Page<PrecioHistorial> findAllByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
            UUID bencineraId, Integer tipoCombustibleId, Pageable pageable);

    /**
     * Ultimo precio registrado por cada tipo de combustible para una bencinera.
     * Subquery correlacionada — apta para el volumen actual.
     * En PostgreSQL puro convendria usar DISTINCT ON o window functions.
     */
    @Query("""
           SELECT p FROM PrecioHistorial p
           WHERE p.bencinera.id = :bencineraId
             AND p.apiTimestamp = (
                 SELECT MAX(p2.apiTimestamp) FROM PrecioHistorial p2
                 WHERE p2.bencinera.id = p.bencinera.id
                   AND p2.tipoCombustible.id = p.tipoCombustible.id
             )
           ORDER BY p.tipoCombustible.id ASC
           """)
    List<PrecioHistorial> findUltimosPreciosPorCombustible(UUID bencineraId);
}
