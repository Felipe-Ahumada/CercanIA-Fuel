package cl.fuelonline.station.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(
    name = "station_schedule",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_schedule_day",
        columnNames = {"station_id", "day_of_week"})
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
    @JoinColumn(name = "station_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_schedule_station"))
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 12)
    private DayOfWeek dayOfWeek;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "twenty_four_hours", nullable = false)
    @Builder.Default
    private Boolean twentyFourHours = Boolean.FALSE;

    @Column(name = "closed", nullable = false)
    @Builder.Default
    private Boolean closed = Boolean.FALSE;

    public enum DayOfWeek { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
}
