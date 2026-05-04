import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../core/usecases/usecase.dart';
import '../entities/fuel_station.dart';
import '../repositories/fuel_station_repository.dart';

class GetFuelStations implements UseCase<List<FuelStation>, NoParams> {
  final FuelStationRepository repository;

  GetFuelStations(this.repository);

  @override
  Future<Either<Failure, List<FuelStation>>> call(NoParams params) async {
    return await repository.getFuelStations();
  }
}
