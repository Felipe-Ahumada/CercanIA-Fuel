package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "marca",
       uniqueConstraints = @UniqueConstraint(name = "uq_marca_codigo_api", columnNames = "codigo_api"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_api", nullable = false, length = 30)
    private String codigoApi;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;
}
