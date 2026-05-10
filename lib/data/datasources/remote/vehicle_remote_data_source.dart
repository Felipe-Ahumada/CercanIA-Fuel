import '../../../domain/entities/vehicle_entity.dart';

abstract class VehicleRemoteDataSource {
  Future<List<VehicleEntity>> getVehicles();
  Future<VehicleEntity> addVehicle(VehicleEntity vehicle);
  Future<void> deleteVehicle(String id);
  Future<void> setActiveVehicle(String id);
}
