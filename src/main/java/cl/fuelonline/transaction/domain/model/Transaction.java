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
        @Index(name = "idx_tx_usuario",     columnList = "usuario_id"),
        @Index(name = "idx_tx_vehiculo",    columnList = "vehiculo_id"),
        @Index(name = "idx_tx_bencinera",   columnList = "bencinera_id"),
        @Index(name = "idx_tx_combustible", columnList = "tipo_combustible_id"),
        @Index(name = "idx_tx_fecha",       columnList = "fecha_transaccion")
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
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_usuario"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_vehiculo"))
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_bencinera"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_combustible_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_combustible"))
    private FuelType fuelType;

    /** Null = no se uso tarjeta (efectivo u otro). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_producto_id",
                foreignKey = @ForeignKey(name = "fk_tx_tarjeta_producto"))
    private CardProduct cardProduct;

    /** Null = no se aplico discount. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descuento_id",
                foreignKey = @ForeignKey(name = "fk_tx_descuento"))
    private Discount discount;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 3)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal liters;

    @Column(name = "monto_bruto", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "monto_descuento", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "monto_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 255)
    private String notes;
}
