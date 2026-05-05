import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/station_entity.dart';

abstract class StationRepository {
  Future<Either<Failure, List<StationEntity>>> getNearbyStations(double lat, double lng, double radiusKm);
  Future<Either<Failure, StationEntity>> getStationDetail(String stationId);
  Future<Either<Failure, StationEntity>> toggleFavorite(String stationId, bool isFavorite);
}
