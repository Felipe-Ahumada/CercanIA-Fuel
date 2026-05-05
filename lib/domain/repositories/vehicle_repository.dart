import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/vehicle_entity.dart';

abstract class VehicleRepository {
  Future<Either<Failure, List<VehicleEntity>>> getVehicles();
  Future<Either<Failure, VehicleEntity>> addVehicle(VehicleEntity vehicle);
  Future<Either<Failure, void>> deleteVehicle(String id);
  Future<Either<Failure, void>> setActiveVehicle(String id);
}
