package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodo_pago",
       uniqueConstraints = @UniqueConstraint(name = "uq_metodo_pago_codigo", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 80)
    private String name;
}
