package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {

    @Query("""
           SELECT d FROM Discount d
           JOIN FETCH d.brand
           LEFT JOIN FETCH d.cardProduct cp
           LEFT JOIN FETCH cp.bank
           LEFT JOIN FETCH d.fuelType
           WHERE d.active = true
           ORDER BY d.brand.name, d.discountValue DESC
           """)
    List<Discount> findAllActiveWithDetails();

    @Query("""
           SELECT d FROM Discount d
           JOIN FETCH d.brand
           LEFT JOIN FETCH d.cardProduct cp
           LEFT JOIN FETCH cp.bank
           LEFT JOIN FETCH d.fuelType
           WHERE d.brand.id = :brandId AND d.active = true
           ORDER BY d.discountValue DESC
           """)
    List<Discount> findAllByBrandWithDetails(Integer brandId);

    @Query("""
           SELECT d FROM Discount d
           JOIN FETCH d.brand
           LEFT JOIN FETCH d.cardProduct cp
           LEFT JOIN FETCH cp.bank
           LEFT JOIN FETCH d.fuelType
           ORDER BY d.active DESC, d.brand.name, d.discountValue DESC
           """)
    List<Discount> findAllWithDetails();

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
           value = "UPDATE discount SET active = 0 WHERE active = 1 AND end_date IS NOT NULL AND end_date < CURDATE()")
    int deactivateExpired();
}
