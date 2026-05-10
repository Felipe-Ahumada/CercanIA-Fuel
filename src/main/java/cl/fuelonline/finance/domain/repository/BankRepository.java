package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    Optional<Bank> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
}
