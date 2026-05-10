package cl.fuelonline.finance.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "card_product",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_card_product_bank_name",
        columnNames = {"bank_id", "name"}),
    indexes = @Index(name = "idx_card_product_bank", columnList = "bank_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class CardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_product_bank"))
    private Bank bank;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType cardType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
