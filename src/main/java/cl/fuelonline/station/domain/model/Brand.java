package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "brand",
       uniqueConstraints = @UniqueConstraint(name = "uq_marca_codigo_api", columnNames = "codigo_api"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_api", nullable = false, length = 30)
    private String apiCode;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
}
