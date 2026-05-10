package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.CardProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardProductRepository extends JpaRepository<CardProduct, Integer> {

    @EntityGraph(attributePaths = "banco")
    List<CardProduct> findAllByBanco_IdOrderByNombreAsc(Integer bancoId);

    Optional<CardProduct> findByBanco_IdAndNombreIgnoreCase(Integer bancoId, String nombre);

    boolean existsByBanco_IdAndNombreIgnoreCase(Integer bancoId, String nombre);
}
