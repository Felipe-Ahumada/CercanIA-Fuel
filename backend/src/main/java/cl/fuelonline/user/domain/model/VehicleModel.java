package cl.fuelonline.user.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_model",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_vehicle_model_brand_name",
           columnNames = {"vehicle_brand_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_brand_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_model_vehicle_brand"))
    private VehicleBrand brand;

    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    @Builder.Default
    private VehicleType vehicleType = VehicleType.CAR;

    public enum VehicleType { CAR, SUV, PICKUP, MOTORCYCLE, VAN, OTHER }
}
