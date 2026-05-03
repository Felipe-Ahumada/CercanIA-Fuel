package cl.bencinaenlinea.bencinera.domain.repository;

import cl.bencinaenlinea.bencinera.domain.model.BencineraHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BencineraHorarioRepository extends JpaRepository<BencineraHorario, Integer> {
    List<BencineraHorario> findAllByBencinera_IdOrderByDiaSemanaAsc(UUID bencineraId);
}
