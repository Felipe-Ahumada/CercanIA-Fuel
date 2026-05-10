package cl.fuelonline.finance.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "tarjeta_producto",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_producto_banco_nombre",
        columnNames = {"banco_id", "name"}),
    indexes = @Index(name = "idx_tp_banco", columnList = "banco_id")
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
    @JoinColumn(name = "banco_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tp_banco"))
    private Bank bank;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarjeta", nullable = false, length = 20)
    private CardType cardType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
