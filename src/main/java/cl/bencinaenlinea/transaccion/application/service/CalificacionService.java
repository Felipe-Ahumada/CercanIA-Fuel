package cl.bencinaenlinea.transaccion.application.service;

import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.bencinera.domain.repository.BencineraRepository;
import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
import cl.bencinaenlinea.transaccion.application.dto.CalificacionCreateRequest;
import cl.bencinaenlinea.transaccion.application.dto.CalificacionResponse;
import cl.bencinaenlinea.transaccion.application.dto.CalificacionResumenResponse;
import cl.bencinaenlinea.transaccion.application.dto.CalificacionUpdateRequest;
import cl.bencinaenlinea.transaccion.application.exception.CalificacionYaExisteException;
import cl.bencinaenlinea.transaccion.application.mapper.CalificacionMapper;
import cl.bencinaenlinea.transaccion.domain.model.Calificacion;
import cl.bencinaenlinea.transaccion.domain.repository.CalificacionRepository;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import cl.bencinaenlinea.usuario.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final BencineraRepository bencineraRepository;
    private final CalificacionMapper mapper;

    public CalificacionResponse buscarPorId(Long id) {
        return mapper.toResponse(obtener(id));
    }

    public Page<CalificacionResponse> listarPorBencinera(UUID bencineraId, Pageable pageable) {
        return calificacionRepository
                .findAllByBencinera_IdOrderByCreatedAtDesc(bencineraId, pageable)
                .map(mapper::toResponse);
    }

    public Page<CalificacionResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return calificacionRepository
                .findAllByUsuario_IdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public CalificacionResumenResponse resumen(UUID bencineraId) {
        var p = calificacionRepository.calcularResumen(bencineraId);
        Double promedio = p != null && p.getPromedio() != null
                ? Math.round(p.getPromedio() * 100.0) / 100.0
                : 0.0;
        Long total = p != null && p.getTotal() != null ? p.getTotal() : 0L;
        return new CalificacionResumenResponse(bencineraId, promedio, total);
    }

    @Transactional
    public CalificacionResponse crear(CalificacionCreateRequest req) {
        if (calificacionRepository
                .findByUsuario_IdAndBencinera_Id(req.usuarioId(), req.bencineraId())
                .isPresent()) {
            throw new CalificacionYaExisteException(
                    "El usuario ya califico esta bencinera. Use PUT para actualizar.");
        }

        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.usuarioId()));
        Bencinera bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bencinera no encontrada: " + req.bencineraId()));

        Calificacion entity = Calificacion.builder()
                .usuario(usuario)
                .bencinera(bencinera)
                .puntaje(req.puntaje())
                .comentario(req.comentario())
                .build();

        return mapper.toResponse(calificacionRepository.save(entity));
    }

    @Transactional
    public CalificacionResponse actualizar(Long id, CalificacionUpdateRequest req) {
        Calificacion entity = obtener(id);
        if (req.puntaje() != null)    entity.setPuntaje(req.puntaje());
        if (req.comentario() != null) entity.setComentario(req.comentario());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!calificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Calificacion no encontrada: " + id);
        }
        calificacionRepository.deleteById(id);
    }

    private Calificacion obtener(Long id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificacion no encontrada: " + id));
    }
}
