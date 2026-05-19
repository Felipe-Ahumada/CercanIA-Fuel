package cl.fuelonline.user.domain.model;

import cl.fuelonline.finance.domain.model.Discount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
    name = "user_selected_discount",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_discount",
        columnNames = {"user_id", "discount_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSelectedDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discount_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usd_discount"))
    private Discount discount;
}
