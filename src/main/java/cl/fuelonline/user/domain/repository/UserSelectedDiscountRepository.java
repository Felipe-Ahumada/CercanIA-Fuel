package cl.fuelonline.user.domain.repository;

import cl.fuelonline.user.domain.model.UserSelectedDiscount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserSelectedDiscountRepository extends JpaRepository<UserSelectedDiscount, Long> {

    @EntityGraph(attributePaths = {"discount", "discount.brand", "discount.cardProduct",
                                   "discount.cardProduct.bank", "discount.fuelType"})
    List<UserSelectedDiscount> findByUserIdOrderByDiscount_Brand_NameAscDiscount_DiscountValueDesc(UUID userId);

    /**
     * Bulk DELETE — ejecuta un único DELETE SQL en vez del patrón find-then-delete
     * de los derived queries. Necesario para que el flush ocurra ANTES del saveAll
     * posterior en la misma transacción y evitar violación de la constraint uq_user_discount.
     *
     * clearAutomatically=true limpia el primer nivel de caché de Hibernate tras el DELETE,
     * evitando que las entidades recién borradas sean visibles en selects posteriores.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserSelectedDiscount u WHERE u.userId = :userId")
    void deleteByUserId(UUID userId);
}