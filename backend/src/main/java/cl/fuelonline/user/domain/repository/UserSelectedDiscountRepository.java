package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.UserSelectedDiscount;
import cl.fuelonline.user.domain.model.UserSelectedDiscountId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserSelectedDiscountRepository
        extends JpaRepository<UserSelectedDiscount, UserSelectedDiscountId> {

    @EntityGraph(attributePaths = {"discount", "discount.brand", "discount.cardProduct",
                                   "discount.cardProduct.bank", "discount.fuelType"})
    List<UserSelectedDiscount> findById_UserIdOrderByDiscount_Brand_NameAscDiscount_DiscountValueDesc(UUID userId);

    /**
     * Bulk DELETE — ejecuta un único DELETE SQL en vez del patrón find-then-delete.
     * clearAutomatically=true limpia el primer nivel de caché de Hibernate tras el DELETE.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserSelectedDiscount u WHERE u.id.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
