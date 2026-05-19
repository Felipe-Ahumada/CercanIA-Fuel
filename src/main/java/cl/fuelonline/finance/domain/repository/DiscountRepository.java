package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountRepository
        extends JpaRepository<Discount, Integer>, JpaSpecificationExecutor<Discount> {

    @Query("""
           SELECT d FROM Discount d
           JOIN FETCH d.brand
           LEFT JOIN FETCH d.cardProduct cp
           LEFT JOIN FETCH cp.bank
           LEFT JOIN FETCH d.fuelType
           ORDER BY d.brand.name, d.discountValue DESC
           """)
    List<Discount> findAllActiveWithDetails();

    @Query("""
           SELECT d FROM Discount d
           JOIN FETCH d.brand
           LEFT JOIN FETCH d.cardProduct cp
           LEFT JOIN FETCH cp.bank
           LEFT JOIN FETCH d.fuelType
           WHERE d.brand.id = :brandId
           ORDER BY d.discountValue DESC
           """)
    List<Discount> findAllByBrandWithDetails(Integer brandId);

    List<Discount> findAllByCardProduct_IdInAndActiveTrueOrderByDiscountValueDesc(List<Integer> cardProductIds);
}
