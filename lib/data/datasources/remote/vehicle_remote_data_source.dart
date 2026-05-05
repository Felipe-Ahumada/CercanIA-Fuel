import 'package:flutter_application_1/domain/entities/vehicle_entity.dart';
import 'package:flutter_application_1/core/network/dio_client.dart';
import 'package:flutter_application_1/core/errors/exceptions.dart';

abstract class VehicleRemoteDataSource {
  Future<List<VehicleEntity>> getVehicles();
  Future<VehicleEntity> addVehicle(VehicleEntity vehicle);
  Future<void> deleteVehicle(String id);
  Future<void> setActiveVehicle(String id);
}
