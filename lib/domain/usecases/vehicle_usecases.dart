import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/vehicle_entity.dart';
import '../repositories/vehicle_repository.dart';

class GetVehicleBrandsUseCase {
  final VehicleRepository repository;
  GetVehicleBrandsUseCase(this.repository);
  Future<Either<Failure, List<VehicleBrandEntity>>> call() => repository.getBrands();
}

class GetVehicleModelsByBrandUseCase {
  final VehicleRepository repository;
  GetVehicleModelsByBrandUseCase(this.repository);
  Future<Either<Failure, List<VehicleModelEntity>>> call(int brandId) =>
      repository.getModelsByBrand(brandId);
}

class GetFuelTypesUseCase {
  final VehicleRepository repository;
  GetFuelTypesUseCase(this.repository);
  Future<Either<Failure, List<FuelTypeEntity>>> call() => repository.getFuelTypes();
}

class GetVehiclesUseCase {
  final VehicleRepository repository;
  GetVehiclesUseCase(this.repository);
  Future<Either<Failure, List<VehicleEntity>>> call() => repository.getVehicles();
}

class AddVehicleUseCase {
  final VehicleRepository repository;
  AddVehicleUseCase(this.repository);
  Future<Either<Failure, VehicleEntity>> call({
    required int vehicleModelId,
    required int fuelTypeId,
    required String licensePlate,
    required int year,
    required String brandName,
    required String modelName,
  }) =>
      repository.addVehicle(
        vehicleModelId: vehicleModelId,
        fuelTypeId: fuelTypeId,
        licensePlate: licensePlate,
        year: year,
        brandName: brandName,
        modelName: modelName,
      );
}

class DeleteVehicleUseCase {
  final VehicleRepository repository;
  DeleteVehicleUseCase(this.repository);
  Future<Either<Failure, void>> call(String id) => repository.deleteVehicle(id);
}
