package cl.bencinaenlinea.finanzas.domain.repository;

import cl.bencinaenlinea.finanzas.domain.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BancoRepository extends JpaRepository<Banco, Integer> {
    Optional<Banco> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
}
