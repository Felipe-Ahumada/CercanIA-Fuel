package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commune",
       uniqueConstraints = @UniqueConstraint(name = "uq_comuna_codigo", columnNames = "code"),
       indexes = @Index(name = "idx_comuna_region", columnList = "region_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_comuna_region"))
    private Region region;

    /** Codigo territorial chileno (5 digitos). Ej: "13101" para Santiago. */
    @Column(length = 8)
    private String code;

    @Column(nullable = false, length = 80)
    private String name;
}
