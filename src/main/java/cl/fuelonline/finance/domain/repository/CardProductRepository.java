package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.CardProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardProductRepository extends JpaRepository<CardProduct, Integer> {

    @EntityGraph(attributePaths = "bank")
    List<CardProduct> findAllByBank_IdOrderByNameAsc(Integer bankId);

    Optional<CardProduct> findByBank_IdAndNameIgnoreCase(Integer bankId, String name);

    boolean existsByBank_IdAndNameIgnoreCase(Integer bankId, String name);

    @EntityGraph(attributePaths = "bank")
    @Query("SELECT DISTINCT cp FROM CardProduct cp " +
           "WHERE EXISTS (SELECT d FROM Discount d WHERE d.cardProduct = cp AND d.active = true) " +
           "ORDER BY cp.bank.name, cp.name")
    List<CardProduct> findAllWithActiveDiscounts();
}
