package cl.bencinaenlinea.transaccion.application.service;

import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.bencinera.domain.repository.BencineraRepository;
import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
import cl.bencinaenlinea.transaccion.application.dto.AlertaCreateRequest;
import cl.bencinaenlinea.transaccion.application.dto.AlertaResponse;
import cl.bencinaenlinea.transaccion.application.mapper.AlertaMapper;
import cl.bencinaenlinea.transaccion.domain.model.Alerta;
import cl.bencinaenlinea.transaccion.domain.repository.AlertaRepository;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import cl.bencinaenlinea.usuario.domain.repository.UsuarioRepository;
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
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BencineraRepository bencineraRepository;
    private final AlertaMapper mapper;

    public Page<AlertaResponse> listarPorUsuario(UUID usuarioId, Boolean leida, Pageable pageable) {
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
    public AlertaResponse crear(AlertaCreateRequest req) {
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.usuarioId()));

        Bencinera bencinera = null;
        if (req.bencineraId() != null) {
            bencinera = bencineraRepository.findById(req.bencineraId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bencinera no encontrada: " + req.bencineraId()));
        }

        Alerta alerta = Alerta.builder()
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
    public AlertaResponse marcarLeida(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada: " + id));
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
            throw new ResourceNotFoundException("Alerta no encontrada: " + id);
        }
        alertaRepository.deleteById(id);
    }
}
