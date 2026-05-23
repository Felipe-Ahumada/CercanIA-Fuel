import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/vehicle_entity.dart';

abstract class VehicleRepository {
  Future<Either<Failure, List<VehicleBrandEntity>>> getBrands();
  Future<Either<Failure, List<VehicleModelEntity>>> getModelsByBrand(int brandId);
  Future<Either<Failure, List<FuelTypeEntity>>> getFuelTypes();
  Future<Either<Failure, List<VehicleEntity>>> getVehicles();
  Future<Either<Failure, VehicleEntity>> addVehicle({
    required int vehicleModelId,
    required int fuelTypeId,
    required String licensePlate,
    required int year,
    required String brandName,
    required String modelName,
  });
  Future<Either<Failure, void>> deleteVehicle(String id);
}
