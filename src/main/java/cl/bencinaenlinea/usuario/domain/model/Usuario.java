package cl.bencinaenlinea.usuario.domain.model;

import cl.bencinaenlinea.shared.persistence.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "usuario",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuario_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_usuario_rut",   columnNames = "rut")
    },
    indexes = @Index(name = "idx_usuario_rol", columnList = "rol_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Usuario extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private Rol rol;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 12)
    private String rut;

    @Column(name = "p_nombre", nullable = false, length = 80)
    private String primerNombre;

    @Column(name = "s_nombre", length = 80)
    private String segundoNombre;

    @Column(name = "p_apellido", nullable = false, length = 80)
    private String primerApellido;

    @Column(name = "s_apellido", nullable = false, length = 80)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vehiculo> vehiculos = new ArrayList<>();

    public void desactivar() { this.activo = Boolean.FALSE; }
    public void reactivar()  { this.activo = Boolean.TRUE;  }
}
