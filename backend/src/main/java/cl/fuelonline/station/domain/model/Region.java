package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "region",
       uniqueConstraints = @UniqueConstraint(name = "uq_region_code", columnNames = "code"))
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
    private String code;

    @Column(nullable = false, length = 80)
    private String name;
}
