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
        @Index(name = "idx_discount_brand",             columnList = "brand_id"),
        @Index(name = "idx_discount_card_product",  columnList = "card_product_id"),
        @Index(name = "idx_discount_fuel",       columnList = "fuel_type_id"),
        @Index(name = "idx_discount_day",               columnList = "day_of_week")
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
    @JoinColumn(name = "brand_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_discount_brand"))
    private Brand brand;

    /** Null = aplica a cualquier medio de pago. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_product_id",
                foreignKey = @ForeignKey(name = "fk_discount_card_product"))
    private CardProduct cardProduct;

    /** Null = aplica a cualquier combustible. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id",
                foreignKey = @ForeignKey(name = "fk_discount_fuel"))
    private FuelType fuelType;

    /** Null = aplica todos los dias. 1 = lunes, 7 = domingo. */
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    @Builder.Default
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(name = "discount_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_cap", precision = 10, scale = 2)
    private BigDecimal maxCap;

    @Column(length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
