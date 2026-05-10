package cl.fuelonline.user.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marca_vehiculo",
       uniqueConstraints = @UniqueConstraint(name = "uq_marca_vehiculo_nombre", columnNames = "nombre"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String nombre;
}
