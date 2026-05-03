package cl.bencinaenlinea.transaccion.application.service;

import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.bencinera.domain.repository.BencineraRepository;
import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
import cl.bencinaenlinea.transaccion.application.dto.FavoritoCreateRequest;
import cl.bencinaenlinea.transaccion.application.dto.FavoritoResponse;
import cl.bencinaenlinea.transaccion.application.exception.FavoritoYaExisteException;
import cl.bencinaenlinea.transaccion.application.mapper.FavoritoMapper;
import cl.bencinaenlinea.transaccion.domain.model.Favorito;
import cl.bencinaenlinea.transaccion.domain.repository.FavoritoRepository;
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
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BencineraRepository bencineraRepository;
    private final FavoritoMapper mapper;

    public Page<FavoritoResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return favoritoRepository
                .findAllByUsuario_IdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public boolean esFavorito(UUID usuarioId, UUID bencineraId) {
        return favoritoRepository.existsByUsuario_IdAndBencinera_Id(usuarioId, bencineraId);
    }

    @Transactional
    public FavoritoResponse agregar(FavoritoCreateRequest req) {
        if (favoritoRepository.existsByUsuario_IdAndBencinera_Id(req.usuarioId(), req.bencineraId())) {
            throw new FavoritoYaExisteException("La bencinera ya esta en favoritos del usuario");
        }

        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.usuarioId()));
        Bencinera bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bencinera no encontrada: " + req.bencineraId()));

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .bencinera(bencinera)
                .alias(req.alias())
                .build();

        return mapper.toResponse(favoritoRepository.save(favorito));
    }

    @Transactional
    public void quitar(UUID usuarioId, UUID bencineraId) {
        if (!favoritoRepository.existsByUsuario_IdAndBencinera_Id(usuarioId, bencineraId)) {
            throw new ResourceNotFoundException("Favorito no encontrado");
        }
        favoritoRepository.deleteByUsuario_IdAndBencinera_Id(usuarioId, bencineraId);
    }
}
