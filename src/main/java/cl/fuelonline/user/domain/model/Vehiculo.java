package cl.fuelonline.user.domain.model;

import cl.fuelonline.station.domain.model.TipoCombustible;
import cl.fuelonline.shared.persistence.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
    name = "vehiculo",
    uniqueConstraints = @UniqueConstraint(name = "uq_vehiculo_patente", columnNames = "patente"),
    indexes = {
        @Index(name = "idx_vehiculo_usuario",     columnList = "usuario_id"),
        @Index(name = "idx_vehiculo_modelo",      columnList = "modelo_vehiculo_id"),
        @Index(name = "idx_vehiculo_combustible", columnList = "tipo_combustible_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Vehiculo extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "VARCHAR(36)", length = 36, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehiculo_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_vehiculo_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehiculo_modelo"))
    private ModeloVehiculo modelo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_combustible_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehiculo_combustible"))
    private TipoCombustible tipoCombustible;

    @Column(nullable = false, length = 10)
    private String patente;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;
}
