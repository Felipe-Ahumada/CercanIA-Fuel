package cl.bencinaenlinea.transaccion.domain.model;

import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.bencinera.domain.model.TipoCombustible;
import cl.bencinaenlinea.finanzas.domain.model.Descuento;
import cl.bencinaenlinea.finanzas.domain.model.TarjetaProducto;
import cl.bencinaenlinea.shared.persistence.BaseAuditEntity;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import cl.bencinaenlinea.usuario.domain.model.Vehiculo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "transaccion",
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
public class Transaccion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_vehiculo"))
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_bencinera"))
    private Bencinera bencinera;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_combustible_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_tx_combustible"))
    private TipoCombustible tipoCombustible;

    /** Null = no se uso tarjeta (efectivo u otro). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_producto_id",
                foreignKey = @ForeignKey(name = "fk_tx_tarjeta_producto"))
    private TarjetaProducto tarjetaProducto;

    /** Null = no se aplico descuento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descuento_id",
                foreignKey = @ForeignKey(name = "fk_tx_descuento"))
    private Descuento descuento;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 3)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal litros;

    @Column(name = "monto_bruto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "monto_descuento", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoDescuento = BigDecimal.ZERO;

    @Column(name = "monto_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(length = 255)
    private String observaciones;
}
