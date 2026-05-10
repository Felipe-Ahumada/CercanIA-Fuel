package cl.fuelonline.station.domain.model;

import cl.fuelonline.shared.persistence.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "station",
    uniqueConstraints = @UniqueConstraint(name = "uq_bencinera_codigo_api", columnNames = "codigo_api"),
    indexes = {
        @Index(name = "idx_bencinera_marca",  columnList = "marca_id"),
        @Index(name = "idx_bencinera_comuna", columnList = "comuna_id"),
        @Index(name = "idx_bencinera_latlon", columnList = "latitude, longitude"),
        @Index(name = "idx_bencinera_activo", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class Station extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "VARCHAR(36)", length = 36, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo_api", nullable = false, length = 30)
    private String apiCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bencinera_marca"))
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comuna_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bencinera_comuna"))
    private Commune commune;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 20)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(name = "en_mantenimiento", nullable = false)
    @Builder.Default
    private Boolean inMaintenance = Boolean.FALSE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @Column(name = "sync_at")
    private LocalDateTime syncAt;
}
