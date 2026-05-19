package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.UserBankConvenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserBankConvenioRepository extends JpaRepository<UserBankConvenio, Long> {

    List<UserBankConvenio> findByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserBankConvenio c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
