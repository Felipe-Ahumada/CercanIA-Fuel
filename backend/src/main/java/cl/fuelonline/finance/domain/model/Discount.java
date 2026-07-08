package cl.fuelonline.finance.domain.model;

import cl.fuelonline.shared.persistence.BaseCreatableEntity;
import cl.fuelonline.catalog.domain.model.Brand;
import cl.fuelonline.catalog.domain.model.FuelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "discount",
    indexes = {
        @Index(name = "idx_discount_brand",        columnList = "brand_id"),
        @Index(name = "idx_discount_card_product", columnList = "card_product_id"),
        @Index(name = "idx_discount_fuel",         columnList = "fuel_type_id"),
        @Index(name = "idx_discount_day",          columnList = "day_of_week")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Check(constraints = "day_of_week IS NULL OR (day_of_week BETWEEN 1 AND 7)")
public class Discount extends BaseCreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_discount_brand"))
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_product_id",
                foreignKey = @ForeignKey(name = "fk_discount_card_product"))
    private CardProduct cardProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id",
                foreignKey = @ForeignKey(name = "fk_discount_fuel"))
    private FuelType fuelType;

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
}