package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.finance.domain.model.Discount;
import cl.fuelonline.finance.domain.model.CardProduct;
import cl.fuelonline.shared.persistence.BaseAuditEntity;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "transaction",
    indexes = {
        @Index(name = "idx_tx_user",     columnList = "user_id"),
        @Index(name = "idx_tx_vehicle",    columnList = "vehicle_id"),
        @Index(name = "idx_tx_station",   columnList = "station_id"),
        @Index(name = "idx_tx_fuel", columnList = "fuel_type_id"),
        @Index(name = "idx_tx_date",       columnList = "transaction_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "VARCHAR(36)", length = 36, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_vehicle"))
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_station"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fuel_type_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_fuel"))
    private FuelType fuelType;

    /** Null = no se uso tarjeta (efectivo u otro). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_product_id",
                foreignKey = @ForeignKey(name = "fk_tx_card_product"))
    private CardProduct cardProduct;

    /** Null = no se aplico discount. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id",
                foreignKey = @ForeignKey(name = "fk_tx_discount"))
    private Discount discount;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 3)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal liters;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 255)
    private String notes;
}
