import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/fuel_station.dart';

abstract class FuelStationRepository {
  Future<Either<Failure, List<FuelStation>>> getFuelStations();
}
