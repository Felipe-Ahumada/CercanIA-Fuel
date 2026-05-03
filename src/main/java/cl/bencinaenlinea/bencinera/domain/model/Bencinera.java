package cl.bencinaenlinea.bencinera.domain.model;

import cl.bencinaenlinea.shared.persistence.BaseAuditEntity;
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
    name = "bencinera",
    uniqueConstraints = @UniqueConstraint(name = "uq_bencinera_codigo_api", columnNames = "codigo_api"),
    indexes = {
        @Index(name = "idx_bencinera_marca",  columnList = "marca_id"),
        @Index(name = "idx_bencinera_comuna", columnList = "comuna_id"),
        @Index(name = "idx_bencinera_latlon", columnList = "latitud, longitud"),
        @Index(name = "idx_bencinera_activo", columnList = "activo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Bencinera extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo_api", nullable = false, length = 30)
    private String codigoApi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bencinera_marca"))
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comuna_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bencinera_comuna"))
    private Comuna comuna;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(length = 20)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(name = "en_mantenimiento", nullable = false)
    @Builder.Default
    private Boolean enMantenimiento = Boolean.FALSE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;

    @Column(name = "sync_at")
    private LocalDateTime syncAt;
}
