package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByCodigoApi(String codigoApi);
    boolean existsByCodigoApi(String codigoApi);
}
