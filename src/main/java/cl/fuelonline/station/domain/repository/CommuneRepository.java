package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Commune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommuneRepository extends JpaRepository<Commune, Integer> {
    List<Commune> findAllByRegion_IdOrderByNombreAsc(Integer regionId);
    Optional<Commune> findByCodigo(String codigo);
}
