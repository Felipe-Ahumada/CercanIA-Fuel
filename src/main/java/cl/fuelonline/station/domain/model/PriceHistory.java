package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "price_history",
    indexes = {
        @Index(name = "idx_ph_fuel",   columnList = "fuel_type_id"),
        @Index(name = "idx_ph_api_timestamp", columnList = "api_timestamp"),
        @Index(name = "idx_ph_recent",      columnList = "station_id, fuel_type_id, api_timestamp")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ph_station"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fuel_type_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ph_fuel"))
    private FuelType fuelType;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_unit", nullable = false, length = 10)
    @Builder.Default
    private ChargeUnit chargeUnit = ChargeUnit.LT;

    @Enumerated(EnumType.STRING)
    @Column(name = "attention_type", nullable = false, length = 10)
    @Builder.Default
    private AttentionType attentionType = AttentionType.FULL;

    @Column(name = "api_timestamp", nullable = false)
    private LocalDateTime apiTimestamp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum AttentionType { FULL, SELF }
}
