package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "station_payment_method",
       indexes = @Index(name = "idx_smp_method", columnList = "payment_method_id"))
@IdClass(StationPaymentMethod.PK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationPaymentMethod {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false,
                columnDefinition = "VARCHAR(36)",
                foreignKey = @ForeignKey(name = "fk_smp_station"))
    private Station station;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_smp_payment_method"))
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
