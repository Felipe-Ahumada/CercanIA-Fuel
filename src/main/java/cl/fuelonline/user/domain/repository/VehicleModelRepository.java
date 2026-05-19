package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Integer> {
    @EntityGraph(attributePaths = "brand")
    List<VehicleModel> findByBrand_IdOrderByNameAsc(Integer brandId);
}
