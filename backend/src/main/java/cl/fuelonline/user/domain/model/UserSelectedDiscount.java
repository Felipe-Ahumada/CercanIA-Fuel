package cl.fuelonline.user.domain.model;

import cl.fuelonline.finance.domain.model.Discount;
import jakarta.persistence.*;
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

    @MapsId("discountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discount_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usd_discount"))
    private Discount discount;

    public static UserSelectedDiscount of(java.util.UUID userId, Discount discount) {
        return UserSelectedDiscount.builder()
                .id(new UserSelectedDiscountId(userId, discount.getId()))
                .discount(discount)
                .build();
    }
}
