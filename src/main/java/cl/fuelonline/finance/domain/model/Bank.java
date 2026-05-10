package cl.fuelonline.finance.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "banco",
       uniqueConstraints = @UniqueConstraint(name = "uq_banco_codigo", columnNames = "codigo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;
}
