package cl.fuelonline.station.domain.repository;

import cl.fuelonline.station.domain.model.StationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StationScheduleRepository extends JpaRepository<StationSchedule, Integer> {
    List<StationSchedule> findAllByBencinera_IdOrderByDiaSemanaAsc(UUID stationId);
}
