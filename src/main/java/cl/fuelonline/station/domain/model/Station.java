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
    uniqueConstraints = @UniqueConstraint(name = "uq_station_api_code", columnNames = "api_code"),
    indexes = {
        @Index(name = "idx_station_brand",  columnList = "brand_id"),
        @Index(name = "idx_station_commune", columnList = "commune_id"),
        @Index(name = "idx_station_latlon", columnList = "latitude, longitude"),
        @Index(name = "idx_station_active", columnList = "active")
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

    @Column(name = "api_code", nullable = false, length = 30)
    private String apiCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_station_brand"))
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commune_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_station_commune"))
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

    @Column(name = "in_maintenance", nullable = false)
    @Builder.Default
    private Boolean inMaintenance = Boolean.FALSE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @Column(name = "sync_at")
    private LocalDateTime syncAt;
}
