package cl.fuelonline.user.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_brand",
       uniqueConstraints = @UniqueConstraint(name = "uq_vehicle_brand_name", columnNames = "name"))
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
    private String name;
}
