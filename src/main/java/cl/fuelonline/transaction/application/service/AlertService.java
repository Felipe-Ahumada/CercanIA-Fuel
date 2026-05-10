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

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final AlertMapper mapper;

    public Page<AlertResponse> listByUser(UUID userId, Boolean read, Pageable pageable) {
        if (read == null) {
            return alertRepository
                    .findAllByUser_IdOrderByCreatedAtDesc(userId, pageable)
                    .map(mapper::toResponse);
        }
        return alertRepository
                .findAllByUser_IdAndReadOrderByCreatedAtDesc(userId, read, pageable)
                .map(mapper::toResponse);
    }

    public long countUnread(UUID userId) {
        return alertRepository.countByUser_IdAndReadFalse(userId);
    }

    @Transactional
    public AlertResponse create(AlertCreateRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.userId()));

        Station station = null;
        if (req.stationId() != null) {
            station = stationRepository.findById(req.stationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Station no encontrada: " + req.stationId()));
        }

        Alert alert = Alert.builder()
                .user(user)
                .station(station)
                .alertType(req.alertType())
                .title(req.title())
                .message(req.message())
                .read(Boolean.FALSE)
                .build();

        return mapper.toResponse(alertRepository.save(alert));
    }

    @Transactional
    public AlertResponse markAsRead(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert no encontrada: " + id));
        if (Boolean.FALSE.equals(alert.getRead())) {
            alert.markAsRead();
        }
        return mapper.toResponse(alert);
    }

    @Transactional
    public int markAllAsRead(UUID userId) {
        return alertRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Transactional
    public void delete(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert no encontrada: " + id);
        }
        alertRepository.deleteById(id);
    }
}
