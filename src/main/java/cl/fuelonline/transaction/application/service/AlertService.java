package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.AlertCreateRequest;
import cl.fuelonline.transaction.application.dto.AlertResponse;
import cl.fuelonline.transaction.application.mapper.AlertMapper;
import cl.fuelonline.transaction.domain.model.Alert;
import cl.fuelonline.transaction.domain.repository.AlertRepository;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertaRepository;
    private final UserRepository usuarioRepository;
    private final StationRepository bencineraRepository;
    private final AlertMapper mapper;

    public Page<AlertResponse> listarPorUsuario(UUID usuarioId, Boolean leida, Pageable pageable) {
        if (leida == null) {
            return alertaRepository
                    .findAllByUsuario_IdOrderByCreatedAtDesc(usuarioId, pageable)
                    .map(mapper::toResponse);
        }
        return alertaRepository
                .findAllByUsuario_IdAndLeidaOrderByCreatedAtDesc(usuarioId, leida, pageable)
                .map(mapper::toResponse);
    }

    public long contarNoLeidas(UUID usuarioId) {
        return alertaRepository.countByUsuario_IdAndLeidaFalse(usuarioId);
    }

    @Transactional
    public AlertResponse crear(AlertCreateRequest req) {
        User usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.usuarioId()));

        Station bencinera = null;
        if (req.bencineraId() != null) {
            bencinera = bencineraRepository.findById(req.bencineraId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Station no encontrada: " + req.bencineraId()));
        }

        Alert alerta = Alert.builder()
                .usuario(usuario)
                .bencinera(bencinera)
                .tipoAlerta(req.tipoAlerta())
                .titulo(req.titulo())
                .mensaje(req.mensaje())
                .leida(Boolean.FALSE)
                .build();

        return mapper.toResponse(alertaRepository.save(alerta));
    }

    @Transactional
    public AlertResponse marcarLeida(Long id) {
        Alert alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert no encontrada: " + id));
        if (Boolean.FALSE.equals(alerta.getLeida())) {
            alerta.marcarLeida();
        }
        return mapper.toResponse(alerta);
    }

    @Transactional
    public int marcarTodasLeidas(UUID usuarioId) {
        return alertaRepository.marcarTodasComoLeidas(usuarioId, LocalDateTime.now());
    }

    @Transactional
    public void eliminar(Long id) {
        if (!alertaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert no encontrada: " + id);
        }
        alertaRepository.deleteById(id);
    }
}
