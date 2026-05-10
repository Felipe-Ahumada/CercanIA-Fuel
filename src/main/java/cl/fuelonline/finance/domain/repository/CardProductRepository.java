package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.CardProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardProductRepository extends JpaRepository<CardProduct, Integer> {

    @EntityGraph(attributePaths = "bank")
    List<CardProduct> findAllByBanco_IdOrderByNombreAsc(Integer bankId);

    Optional<CardProduct> findByBanco_IdAndNombreIgnoreCase(Integer bankId, String name);

    boolean existsByBanco_IdAndNombreIgnoreCase(Integer bankId, String name);
}
