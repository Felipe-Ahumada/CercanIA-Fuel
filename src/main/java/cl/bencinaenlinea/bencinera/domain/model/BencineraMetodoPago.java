package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "bencinera_metodo_pago",
       indexes = @Index(name = "idx_bmp_metodo", columnList = "metodo_pago_id"))
@IdClass(BencineraMetodoPago.PK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BencineraMetodoPago {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bmp_bencinera"))
    private Bencinera bencinera;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "metodo_pago_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bmp_metodo_pago"))
    private MetodoPago metodoPago;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private UUID bencinera;
        private Integer metodoPago;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(bencinera, pk.bencinera)
                && Objects.equals(metodoPago, pk.metodoPago);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bencinera, metodoPago);
        }
    }
}
