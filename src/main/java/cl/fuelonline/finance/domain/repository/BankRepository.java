package cl.fuelonline.finance.domain.repository;

import cl.fuelonline.finance.domain.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    Optional<Bank> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
}
