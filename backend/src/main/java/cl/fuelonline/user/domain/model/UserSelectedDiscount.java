package cl.fuelonline.user.domain.model;

import cl.fuelonline.finance.domain.model.Discount;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "user_selected_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSelectedDiscount {

    @EmbeddedId
    private UserSelectedDiscountId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usd_user"))
    private User user;

    @MapsId("discountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discount_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usd_discount"))
    private Discount discount;

    public static UserSelectedDiscount of(UUID userId, Discount discount, User user) {
        return UserSelectedDiscount.builder()
                .id(new UserSelectedDiscountId(userId, discount.getId()))
                .user(user)
                .discount(discount)
                .build();
    }
}
