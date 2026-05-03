package cl.bencinaenlinea.bencinera.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(
    name = "bencinera_horario",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_horario_dia",
        columnNames = {"bencinera_id", "dia_semana"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BencineraHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_horario_bencinera"))
    private Bencinera bencinera;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private DiaSemana diaSemana;

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(name = "es_24_horas", nullable = false)
    @Builder.Default
    private Boolean es24Horas = Boolean.FALSE;

    @Column(name = "esta_cerrado", nullable = false)
    @Builder.Default
    private Boolean estaCerrado = Boolean.FALSE;

    public enum DiaSemana { LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO }
}
