package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodo_pago",
       uniqueConstraints = @UniqueConstraint(name = "uq_metodo_pago_codigo", columnNames = "codigo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;
}
