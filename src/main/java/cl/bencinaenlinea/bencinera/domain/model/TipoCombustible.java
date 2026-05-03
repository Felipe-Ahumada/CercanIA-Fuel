package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tipo_combustible")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class TipoCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "nombre_corto", nullable = false, length = 20)
    private String nombreCorto;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_cobro", nullable = false, length = 10)
    @Builder.Default
    private UnidadCobro unidadCobro = UnidadCobro.LT;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;
}
