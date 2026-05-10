package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.shared.persistence.BaseAuditEntity;
import cl.fuelonline.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "rating",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_calificacion_usuario_bencinera",
        columnNames = {"usuario_id", "bencinera_id"}),
    indexes = {
        @Index(name = "idx_calif_usuario",   columnList = "usuario_id"),
        @Index(name = "idx_calif_bencinera", columnList = "bencinera_id"),
        @Index(name = "idx_calif_puntaje",   columnList = "score")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_calif_usuario"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_calif_bencinera"))
    private Station station;

    /** 1 a 5 estrellas. */
    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String comment;
}
