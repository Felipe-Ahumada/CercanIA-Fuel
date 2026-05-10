package cl.fuelonline.transaction.domain.model;

import cl.fuelonline.station.domain.model.Bencinera;
import cl.fuelonline.user.domain.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "alerta",
    indexes = {
        @Index(name = "idx_alerta_usuario",     columnList = "usuario_id"),
        @Index(name = "idx_alerta_bencinera",   columnList = "bencinera_id"),
        @Index(name = "idx_alerta_tipo",        columnList = "tipo_alerta"),
        @Index(name = "idx_alerta_leida",       columnList = "leida"),
        @Index(name = "idx_alerta_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_alerta_usuario"))
    private Usuario usuario;

    /** Null = alerta no asociada a una bencinera (ej: SISTEMA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bencinera_id",
                foreignKey = @ForeignKey(name = "fk_alerta_bencinera"))
    private Bencinera bencinera;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    private TipoAlerta tipoAlerta;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    @Builder.Default
    private Boolean leida = Boolean.FALSE;

    @Column(name = "leida_at")
    private LocalDateTime leidaAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void marcarLeida() {
        this.leida = Boolean.TRUE;
        this.leidaAt = LocalDateTime.now();
    }
}
