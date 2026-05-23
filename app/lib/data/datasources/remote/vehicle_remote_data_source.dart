import '../../../domain/entities/vehicle_entity.dart';

abstract class VehicleRemoteDataSource {
  Future<List<VehicleBrandEntity>> getBrands();
  Future<List<VehicleModelEntity>> getModelsByBrand(int brandId);
  Future<List<FuelTypeEntity>> getFuelTypes();
  Future<List<VehicleEntity>> getVehicles();
  Future<VehicleEntity> addVehicle({
    required int vehicleModelId,
    required int fuelTypeId,
    required String licensePlate,
    required int year,
    required String brandName,
    required String modelName,
  });
  Future<void> deleteVehicle(String id);
}
