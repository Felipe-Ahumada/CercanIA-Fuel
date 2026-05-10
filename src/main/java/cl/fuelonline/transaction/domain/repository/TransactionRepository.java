package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByUser_IdOrderByTransactionDateDesc(UUID userId, Pageable pageable);

    Page<Transaction> findAllByUser_IdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    @Query("""
           select coalesce(sum(t.finalAmount), 0)
             from Transaction t
            where t.user.id = :userId
              and t.transactionDate between :desde and :hasta
           """)
    BigDecimal sumTotalSpent(@Param("userId") UUID userId,
                               @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta);

    @Query("""
           select coalesce(sum(t.discountAmount), 0)
             from Transaction t
            where t.user.id = :userId
              and t.transactionDate between :desde and :hasta
           """)
    BigDecimal sumTotalSaved(@Param("userId") UUID userId,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta);

    @Query("""
           select coalesce(sum(t.liters), 0)
             from Transaction t
            where t.user.id = :userId
              and t.transactionDate between :desde and :hasta
           """)
    BigDecimal sumTotalLiters(@Param("userId") UUID userId,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta);
}
