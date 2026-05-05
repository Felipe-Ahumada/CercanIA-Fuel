import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/station_entity.dart';
import '../repositories/station_repository.dart';

class GetNearbyStationsUseCase {
  final StationRepository repository;

  GetNearbyStationsUseCase(this.repository);

  Future<Either<Failure, List<StationEntity>>> call(double lat, double lng, double radiusKm) {
    return repository.getNearbyStations(lat, lng, radiusKm);
  }
}

class GetStationDetailUseCase {
  final StationRepository repository;

  GetStationDetailUseCase(this.repository);

  Future<Either<Failure, StationEntity>> call(String stationId) {
    return repository.getStationDetail(stationId);
  }
}

class ToggleFavoriteUseCase {
  final StationRepository repository;

  ToggleFavoriteUseCase(this.repository);

  Future<Either<Failure, StationEntity>> call(String stationId, bool isFavorite) {
    return repository.toggleFavorite(stationId, isFavorite);
  }
}
