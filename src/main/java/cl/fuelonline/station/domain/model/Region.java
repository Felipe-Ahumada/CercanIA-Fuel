package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "region",
       uniqueConstraints = @UniqueConstraint(name = "uq_region_codigo", columnNames = "codigo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 2)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;
}
