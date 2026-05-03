package cl.bencinaenlinea.finanzas.domain.repository;

import cl.bencinaenlinea.finanzas.domain.model.TarjetaProducto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarjetaProductoRepository extends JpaRepository<TarjetaProducto, Integer> {

    @EntityGraph(attributePaths = "banco")
    List<TarjetaProducto> findAllByBanco_IdOrderByNombreAsc(Integer bancoId);

    Optional<TarjetaProducto> findByBanco_IdAndNombreIgnoreCase(Integer bancoId, String nombre);

    boolean existsByBanco_IdAndNombreIgnoreCase(Integer bancoId, String nombre);
}
