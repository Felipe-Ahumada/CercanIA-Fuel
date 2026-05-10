package cl.fuelonline.station.domain.model;

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
public class StationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bencinera_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_horario_bencinera"))
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private DiaSemana dayOfWeek;

    @Column(name = "hora_apertura")
    private LocalTime openingTime;

    @Column(name = "hora_cierre")
    private LocalTime closingTime;

    @Column(name = "es_24_horas", nullable = false)
    @Builder.Default
    private Boolean twentyFourHours = Boolean.FALSE;

    @Column(name = "esta_cerrado", nullable = false)
    @Builder.Default
    private Boolean closed = Boolean.FALSE;

    public enum DiaSemana { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
}
