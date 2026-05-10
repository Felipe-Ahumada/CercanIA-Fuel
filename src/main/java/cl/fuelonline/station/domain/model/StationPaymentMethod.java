package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "bencinera_metodo_pago",
       indexes = @Index(name = "idx_bmp_metodo", columnList = "metodo_pago_id"))
@IdClass(StationPaymentMethod.PK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationPaymentMethod {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bmp_bencinera"))
    private Station station;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "metodo_pago_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_bmp_metodo_pago"))
    private PaymentMethod paymentMethod;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private UUID station;
        private Integer paymentMethod;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(station, pk.station)
                && Objects.equals(paymentMethod, pk.paymentMethod);
        }

        @Override
        public int hashCode() {
            return Objects.hash(station, paymentMethod);
        }
    }
}
