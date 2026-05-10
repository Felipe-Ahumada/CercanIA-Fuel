package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.RatingCreateRequest;
import cl.fuelonline.transaction.application.dto.RatingResponse;
import cl.fuelonline.transaction.application.dto.RatingSummaryResponse;
import cl.fuelonline.transaction.application.dto.RatingUpdateRequest;
import cl.fuelonline.transaction.application.exception.RatingAlreadyExistsException;
import cl.fuelonline.transaction.application.mapper.RatingMapper;
import cl.fuelonline.transaction.domain.model.Rating;
import cl.fuelonline.transaction.domain.repository.RatingRepository;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final RatingMapper mapper;

    public RatingResponse findById(Long id) {
        return mapper.toResponse(get(id));
    }

    public Page<RatingResponse> listByStation(UUID stationId, Pageable pageable) {
        return ratingRepository
                .findAllByStation_IdOrderByCreatedAtDesc(stationId, pageable)
                .map(mapper::toResponse);
    }

    public Page<RatingResponse> listByUser(UUID userId, Pageable pageable) {
        return ratingRepository
                .findAllByUser_IdOrderByCreatedAtDesc(userId, pageable)
                .map(mapper::toResponse);
    }

    public RatingSummaryResponse summary(UUID stationId) {
        var p = ratingRepository.calculateSummary(stationId);
        Double average = p != null && p.getPromedio() != null
                ? Math.round(p.getPromedio() * 100.0) / 100.0
                : 0.0;
        Long total = p != null && p.getTotal() != null ? p.getTotal() : 0L;
        return new RatingSummaryResponse(stationId, average, total);
    }

    @Transactional
    public RatingResponse create(RatingCreateRequest req) {
        if (ratingRepository
                .findByUser_IdAndStation_Id(req.userId(), req.stationId())
                .isPresent()) {
            throw new RatingAlreadyExistsException(
                    "El user ya califico esta station. Use PUT para update.");
        }

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.userId()));
        Station station = stationRepository.findById(req.stationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station no encontrada: " + req.stationId()));

        Rating entity = Rating.builder()
                .user(user)
                .station(station)
                .score(req.score())
                .comment(req.comment())
                .build();

        return mapper.toResponse(ratingRepository.save(entity));
    }

    @Transactional
    public RatingResponse update(Long id, RatingUpdateRequest req) {
        Rating entity = get(id);
        if (req.score() != null)    entity.setScore(req.score());
        if (req.comment() != null) entity.setComment(req.comment());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!ratingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rating no encontrada: " + id);
        }
        ratingRepository.deleteById(id);
    }

    private Rating get(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating no encontrada: " + id));
    }
}
