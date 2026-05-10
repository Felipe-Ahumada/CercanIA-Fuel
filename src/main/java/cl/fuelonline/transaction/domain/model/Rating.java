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
        name = "uq_rating_user_station",
        columnNames = {"user_id", "station_id"}),
    indexes = {
        @Index(name = "idx_rating_user",   columnList = "user_id"),
        @Index(name = "idx_rating_station", columnList = "station_id"),
        @Index(name = "idx_rating_score",   columnList = "score")
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
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_rating_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_rating_station"))
    private Station station;

    /** 1 a 5 estrellas. */
    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String comment;
}
