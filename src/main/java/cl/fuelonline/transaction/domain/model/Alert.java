package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "alert",
    indexes = {
        @Index(name = "idx_alerta_usuario",     columnList = "usuario_id"),
        @Index(name = "idx_alerta_bencinera",   columnList = "bencinera_id"),
        @Index(name = "idx_alerta_tipo",        columnList = "tipo_alerta"),
        @Index(name = "idx_alerta_leida",       columnList = "read"),
        @Index(name = "idx_alerta_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_alerta_usuario"))
    private User user;

    /** Null = alert no asociada a una station (ej: SYSTEM). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bencinera_id",
                foreignKey = @ForeignKey(name = "fk_alerta_bencinera"))
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    private AlertType alertType;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private Boolean read = Boolean.FALSE;

    @Column(name = "leida_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.read = Boolean.TRUE;
        this.readAt = LocalDateTime.now();
    }
}
