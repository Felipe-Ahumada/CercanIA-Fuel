package cl.bencinaenlinea.bencinera.domain.repository;

import cl.bencinaenlinea.bencinera.domain.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComunaRepository extends JpaRepository<Comuna, Integer> {
    List<Comuna> findAllByRegion_IdOrderByNombreAsc(Integer regionId);
    Optional<Comuna> findByCodigo(String codigo);
}
