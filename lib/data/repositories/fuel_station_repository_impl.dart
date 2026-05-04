import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/fuel_station.dart';
import '../../domain/repositories/fuel_station_repository.dart';
import '../datasources/remote/fuel_station_remote_data_source.dart';

class FuelStationRepositoryImpl implements FuelStationRepository {
  final FuelStationRemoteDataSource remoteDataSource;

  FuelStationRepositoryImpl({required this.remoteDataSource});

  @override
  Future<Either<Failure, List<FuelStation>>> getFuelStations() async {
    try {
      final remoteStations = await remoteDataSource.getFuelStations();
      return Right(remoteStations);
    } catch (e) {
      return Left(ServerFailure('Failed to fetch data from server'));
    }
  }
}
