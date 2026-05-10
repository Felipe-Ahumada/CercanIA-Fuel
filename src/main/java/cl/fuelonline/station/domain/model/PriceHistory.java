package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "precio_historial",
    indexes = {
        @Index(name = "idx_ph_combustible",   columnList = "tipo_combustible_id"),
        @Index(name = "idx_ph_api_timestamp", columnList = "api_timestamp"),
        @Index(name = "idx_ph_reciente",      columnList = "bencinera_id, tipo_combustible_id, api_timestamp")
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
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ph_bencinera"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_combustible_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ph_combustible"))
    private FuelType fuelType;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_cobro", nullable = false, length = 10)
    @Builder.Default
    private ChargeUnit chargeUnit = ChargeUnit.LT;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atencion", nullable = false, length = 10)
    @Builder.Default
    private TipoAtencion attentionType = TipoAtencion.FULL;

    @Column(name = "api_timestamp", nullable = false)
    private LocalDateTime apiTimestamp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TipoAtencion { FULL, SELF }
}
