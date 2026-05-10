package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DiscountRepository
        extends JpaRepository<Discount, Integer>, JpaSpecificationExecutor<Discount> {

    List<Discount> findAllByBrand_IdOrderByDiscountValueDesc(Integer brandId);
}
