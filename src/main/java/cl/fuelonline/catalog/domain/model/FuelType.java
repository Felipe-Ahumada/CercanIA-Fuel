package cl.fuelonline.catalog.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "fuel_type",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_fuel_type_name",       columnNames = "name"),
        @UniqueConstraint(name = "uq_fuel_type_short_name", columnNames = "short_name")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@BatchSize(size = 50)
@SQLRestriction("active = true")
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(name = "short_name", nullable = false, length = 20)
    private String shortName;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_unit", nullable = false, length = 10)
    @Builder.Default
    private ChargeUnit chargeUnit = ChargeUnit.LT;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
}