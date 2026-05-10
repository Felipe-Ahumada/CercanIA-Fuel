package cl.fuelonline.transaction.application.service;

import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.transaction.application.dto.FavoriteCreateRequest;
import cl.fuelonline.transaction.application.dto.FavoriteResponse;
import cl.fuelonline.transaction.application.exception.FavoriteAlreadyExistsException;
import cl.fuelonline.transaction.application.mapper.FavoriteMapper;
import cl.fuelonline.transaction.domain.model.Favorite;
import cl.fuelonline.transaction.domain.repository.FavoriteRepository;
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
public class FavoriteService {

    private final FavoriteRepository favoritoRepository;
    private final UserRepository usuarioRepository;
    private final StationRepository bencineraRepository;
    private final FavoriteMapper mapper;

    public Page<FavoriteResponse> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return favoritoRepository
                .findAllByUsuario_IdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(mapper::toResponse);
    }

    public boolean esFavorito(UUID usuarioId, UUID bencineraId) {
        return favoritoRepository.existsByUsuario_IdAndBencinera_Id(usuarioId, bencineraId);
    }

    @Transactional
    public FavoriteResponse agregar(FavoriteCreateRequest req) {
        if (favoritoRepository.existsByUsuario_IdAndBencinera_Id(req.usuarioId(), req.bencineraId())) {
            throw new FavoriteAlreadyExistsException("La bencinera ya esta en favoritos del usuario");
        }

        User usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.usuarioId()));
        Station bencinera = bencineraRepository.findById(req.bencineraId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station no encontrada: " + req.bencineraId()));

        Favorite favorito = Favorite.builder()
                .usuario(usuario)
                .bencinera(bencinera)
                .alias(req.alias())
                .build();

        return mapper.toResponse(favoritoRepository.save(favorito));
    }

    @Transactional
    public void quitar(UUID usuarioId, UUID bencineraId) {
        if (!favoritoRepository.existsByUsuario_IdAndBencinera_Id(usuarioId, bencineraId)) {
            throw new ResourceNotFoundException("Favorite no encontrado");
        }
        favoritoRepository.deleteByUsuario_IdAndBencinera_Id(usuarioId, bencineraId);
    }
}
