package cl.fuelonline.user.domain.model;

import cl.fuelonline.catalog.domain.model.FuelType;
import cl.fuelonline.shared.persistence.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
    name = "vehicle",
    indexes = {
        @Index(name = "idx_vehicle_user",     columnList = "user_id"),
        @Index(name = "idx_vehicle_model",      columnList = "vehicle_model_id"),
        @Index(name = "idx_vehicle_fuel", columnList = "fuel_type_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class Vehicle extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "VARCHAR(36)", length = 36, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehicle_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_model_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehicle_model"))
    private VehicleModel model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fuel_type_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_vehicle_fuel"))
    private FuelType fuelType;

    @Column(nullable = false, length = 10, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
