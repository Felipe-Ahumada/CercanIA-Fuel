package cl.fuelonline.user.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modelo_vehiculo",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_modelo_marca_nombre",
           columnNames = {"marca_vehiculo_id", "name"}))
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
    @JoinColumn(name = "marca_vehiculo_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_modelo_marca_vehiculo"))
    private VehicleBrand brand;

    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false, length = 20)
    @Builder.Default
    private TipoVehiculo vehicleType = TipoVehiculo.CAR;

    public enum TipoVehiculo { CAR, SUV, PICKUP, MOTORCYCLE, VAN, OTHER }
}
