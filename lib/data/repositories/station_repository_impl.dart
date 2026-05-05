import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/repositories/station_repository.dart';
import '../datasources/remote/station_remote_data_source.dart';
import 'package:dio/dio.dart';

class StationRepositoryImpl implements StationRepository {
  final StationRemoteDataSource remoteDataSource;

  StationRepositoryImpl({required this.remoteDataSource});

  @override
  Future<Either<Failure, List<StationEntity>>> getNearbyStations(double lat, double lng, double radiusKm) async {
    try {
      final stations = await remoteDataSource.getNearbyStations(lat, lng, radiusKm);
      return Right(stations);
    } on DioException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener estaciones'));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, StationEntity>> getStationDetail(String stationId) async {
    try {
      final station = await remoteDataSource.getStationDetail(stationId);
      return Right(station);
    } on DioException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener detalle de la estación'));
    } catch (e) {
       return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, StationEntity>> toggleFavorite(String stationId, bool isFavorite) async {
    try {
      final station = await remoteDataSource.toggleFavorite(stationId, isFavorite);
      return Right(station);
    } on DioException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al actualizar favorito'));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }
}
