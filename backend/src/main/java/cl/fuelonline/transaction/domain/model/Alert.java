package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.shared.persistence.BaseCreatableEntity;
import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "alert",
    indexes = {
        @Index(name = "idx_alert_user",       columnList = "user_id"),
        @Index(name = "idx_alert_station",    columnList = "station_id"),
        @Index(name = "idx_alert_type",       columnList = "alert_type"),
        @Index(name = "idx_alert_read",       columnList = "is_read"),
        @Index(name = "idx_alert_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert extends BaseCreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_alert_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id",
                foreignKey = @ForeignKey(name = "fk_alert_station"))
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean read = Boolean.FALSE;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public void markAsRead() {
        this.read = Boolean.TRUE;
        this.readAt = LocalDateTime.now();
    }
}