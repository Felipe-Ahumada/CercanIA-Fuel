package cl.fuelonline.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UserSelectedDiscountId implements Serializable {

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "discount_id", nullable = false, updatable = false)
    private Integer discountId;
}
