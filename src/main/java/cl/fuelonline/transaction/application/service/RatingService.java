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

    private final RatingRepository calificacionRepository;
    private final UserRepository usuarioRepository;
    private final StationRepository bencineraRepository;
    private final RatingMapper mapper;

    public RatingResponse buscarPorId(Long id) {
        return mapper.toResponse(obtener(id));
    }

    public Page<RatingResponse> listarPorBencinera(UUID bencineraId, Pageable pageable) {
        return calificacionRepository
                .findAllByBencinera_IdOrderByCreatedAtDesc(bencineraId, pageable)
                .map(mapper::toResponse);
    }

    public Page<RatingResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return calificacionRepository
                .findAllByUsuario_IdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public RatingSummaryResponse resumen(UUID bencineraId) {
        var p = calificacionRepository.calcularResumen(bencineraId);
        Double promedio = p != null && p.getPromedio() != null
                ? Math.round(p.getPromedio() * 100.0) / 100.0
                : 0.0;
        Long total = p != null && p.getTotal() != null ? p.getTotal() : 0L;
        return new RatingSummaryResponse(bencineraId, promedio, total);
    }

    @Transactional
    public RatingResponse crear(RatingCreateRequest req) {
        if (calificacionRepository
                .findByUsuario_IdAndBencinera_Id(req.usuarioId(), req.bencineraId())
                .isPresent()) {
            throw new RatingAlreadyExistsException(
                    "El usuario ya califico esta bencinera. Use PUT para actualizar.");
        }

        User usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.usuarioId()));
        Station bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station no encontrada: " + req.bencineraId()));

        Rating entity = Rating.builder()
                .usuario(usuario)
                .bencinera(bencinera)
                .puntaje(req.puntaje())
                .comentario(req.comentario())
                .build();

        return mapper.toResponse(calificacionRepository.save(entity));
    }

    @Transactional
    public RatingResponse actualizar(Long id, RatingUpdateRequest req) {
        Rating entity = obtener(id);
        if (req.puntaje() != null)    entity.setPuntaje(req.puntaje());
        if (req.comentario() != null) entity.setComentario(req.comentario());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!calificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rating no encontrada: " + id);
        }
        calificacionRepository.deleteById(id);
    }

    private Rating obtener(Long id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating no encontrada: " + id));
    }
}
