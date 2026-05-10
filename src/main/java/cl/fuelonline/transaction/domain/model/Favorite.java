package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "favorite",
    indexes = @Index(name = "idx_favorite_station", columnList = "station_id")
)
@IdClass(Favorite.PK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                columnDefinition = "VARCHAR(36)",
                foreignKey = @ForeignKey(name = "fk_favorite_user"))
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false,
                columnDefinition = "VARCHAR(36)",
                foreignKey = @ForeignKey(name = "fk_favorite_station"))
    private Station station;

    @Column(length = 80)
    private String alias;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private UUID user;
        private UUID station;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(user, pk.user)
                && Objects.equals(station, pk.station);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, station);
        }
    }
}
