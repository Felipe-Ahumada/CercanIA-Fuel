package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comuna",
       indexes = @Index(name = "idx_comuna_region", columnList = "region_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_comuna_region"))
    private Region region;

    @Column(nullable = false, length = 80)
    private String nombre;
}
