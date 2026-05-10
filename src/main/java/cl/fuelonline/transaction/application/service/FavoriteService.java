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

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final FavoriteMapper mapper;

    public Page<FavoriteResponse> listByUser(UUID userId, Pageable pageable) {
        return favoriteRepository
                .findAllByUser_IdOrderByCreatedAtDesc(userId, pageable)
                .map(mapper::toResponse);
    }

    public boolean isFavorite(UUID userId, UUID stationId) {
        return favoriteRepository.existsByUser_IdAndStation_Id(userId, stationId);
    }

    @Transactional
    public FavoriteResponse add(FavoriteCreateRequest req) {
        if (favoriteRepository.existsByUser_IdAndStation_Id(req.userId(), req.stationId())) {
            throw new FavoriteAlreadyExistsException("La station ya esta en favorites del user");
        }

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + req.userId()));
        Station station = stationRepository.findById(req.stationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station no encontrada: " + req.stationId()));

        Favorite favorite = Favorite.builder()
                .user(user)
                .station(station)
                .alias(req.alias())
                .build();

        return mapper.toResponse(favoriteRepository.save(favorite));
    }

    @Transactional
    public void remove(UUID userId, UUID stationId) {
        if (!favoriteRepository.existsByUser_IdAndStation_Id(userId, stationId)) {
            throw new ResourceNotFoundException("Favorite no encontrado");
        }
        favoriteRepository.deleteByUser_IdAndStation_Id(userId, stationId);
    }
}
