package cl.fuelonline.transaction.domain.repository;

import cl.fuelonline.transaction.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * Monthly breakdown of savings and liters for a user in a date range.
     * Returns Object[] rows: [year(int), month(int 1-12), totalSaved(BigDecimal), totalLiters(BigDecimal)]
     */
    @Query("""
           SELECT FUNCTION('YEAR', t.transactionDate),
                  FUNCTION('MONTH', t.transactionDate),
                  COALESCE(SUM(t.discountAmount), 0),
                  COALESCE(SUM(t.liters), 0)
             FROM Transaction t
            WHERE t.user.id  = :userId
              AND t.transactionDate BETWEEN :desde AND :hasta
            GROUP BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate)
            ORDER BY FUNCTION('YEAR', t.transactionDate) ASC, FUNCTION('MONTH', t.transactionDate) ASC
           """)
    List<Object[]> sumByMonth(@Param("userId") UUID userId,
                              @Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta);
}
