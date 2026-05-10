package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Bencinera;
import cl.fuelonline.shared.persistence.BaseAuditEntity;
import cl.fuelonline.user.domain.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "calificacion",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_calificacion_usuario_bencinera",
        columnNames = {"usuario_id", "bencinera_id"}),
    indexes = {
        @Index(name = "idx_calif_usuario",   columnList = "usuario_id"),
        @Index(name = "idx_calif_bencinera", columnList = "bencinera_id"),
        @Index(name = "idx_calif_puntaje",   columnList = "puntaje")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_calif_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_calif_bencinera"))
    private Bencinera bencinera;

    /** 1 a 5 estrellas. */
    @Column(nullable = false)
    private Integer puntaje;

    @Column(length = 500)
    private String comentario;
}
