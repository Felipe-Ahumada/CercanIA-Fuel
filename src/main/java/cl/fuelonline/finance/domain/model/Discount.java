package cl.fuelonline.finance.domain.model;

import cl.fuelonline.station.domain.model.Brand;
import cl.fuelonline.station.domain.model.FuelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "discount",
    indexes = {
        @Index(name = "idx_desc_marca",             columnList = "marca_id"),
        @Index(name = "idx_desc_tarjeta_producto",  columnList = "tarjeta_producto_id"),
        @Index(name = "idx_desc_combustible",       columnList = "tipo_combustible_id"),
        @Index(name = "idx_desc_dia",               columnList = "dia_semana")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_desc_marca"))
    private Brand brand;

    /** Null = aplica a cualquier medio de pago. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_producto_id",
                foreignKey = @ForeignKey(name = "fk_desc_tarjeta_producto"))
    private CardProduct cardProduct;

    /** Null = aplica a cualquier combustible. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_combustible_id",
                foreignKey = @ForeignKey(name = "fk_desc_combustible"))
    private FuelType fuelType;

    /** Null = aplica todos los dias. 1 = lunes, 7 = domingo. */
    @Column(name = "dia_semana")
    private Integer dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    @Builder.Default
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(name = "valor_descuento", nullable = false, precision = 8, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "tope_maximo", precision = 10, scale = 2)
    private BigDecimal maxCap;

    @Column(length = 255)
    private String description;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin")
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
