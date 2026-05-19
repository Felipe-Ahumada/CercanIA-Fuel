package cl.fuelonline.catalog.domain.repository;

import cl.fuelonline.catalog.domain.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByApiCode(String apiCode);
    boolean existsByApiCode(String apiCode);
}