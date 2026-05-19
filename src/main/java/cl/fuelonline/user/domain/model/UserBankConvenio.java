package cl.fuelonline.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
    name = "user_bank_convenio",
    indexes = @Index(name = "idx_user_bank_convenio_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String bank;

    @Column(name = "card_type", nullable = false, length = 50)
    private String cardType;

    @Column(name = "card_product_id")
    private Integer cardProductId;
}
