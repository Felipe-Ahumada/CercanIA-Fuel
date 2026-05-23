package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.VehicleBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, Integer> {
    List<VehicleBrand> findAllByOrderByNameAsc();
}
