package cl.bencinaenlinea.transaccion.domain.model;

import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "favorito",
    indexes = @Index(name = "idx_fav_bencinera", columnList = "bencinera_id")
)
@IdClass(Favorito.PK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorito {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fav_usuario"))
    private Usuario usuario;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fav_bencinera"))
    private Bencinera bencinera;

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
        private UUID usuario;
        private UUID bencinera;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(usuario, pk.usuario)
                && Objects.equals(bencinera, pk.bencinera);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuario, bencinera);
        }
    }
}
