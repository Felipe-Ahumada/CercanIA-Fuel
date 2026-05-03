package cl.bencinaenlinea.bencinera.domain.repository;

import cl.bencinaenlinea.bencinera.domain.model.TipoCombustible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoCombustibleRepository extends JpaRepository<TipoCombustible, Integer> {
    Optional<TipoCombustible> findByNombreCortoIgnoreCase(String nombreCorto);
}
