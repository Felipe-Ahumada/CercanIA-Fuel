package cl.bencinaenlinea.bencinera.domain.repository;

import cl.bencinaenlinea.bencinera.domain.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    Optional<Marca> findByCodigoApi(String codigoApi);
    boolean existsByCodigoApi(String codigoApi);
}
