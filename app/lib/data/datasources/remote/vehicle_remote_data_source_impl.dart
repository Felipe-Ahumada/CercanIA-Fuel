import 'package:dio/dio.dart';

import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';
import '../../../domain/entities/vehicle_entity.dart';
import 'vehicle_remote_data_source.dart';

class VehicleRemoteDataSourceImpl implements VehicleRemoteDataSource {
  final DioClient dioClient;

  VehicleRemoteDataSourceImpl(this.dioClient);

  @override
  Future<List<VehicleBrandEntity>> getBrands() async {
    try {
      final res = await dioClient.get('/vehiculos/marcas');
      final List data = res.data as List;
      return data
          .map((j) => VehicleBrandEntity(id: j['id'] as int, name: j['name'] as String))
          .toList();
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  @override
  Future<List<VehicleModelEntity>> getModelsByBrand(int brandId) async {
    try {
      final res = await dioClient.get('/vehiculos/marcas/$brandId/modelos');
      final List data = res.data as List;
      return data
          .map((j) => VehicleModelEntity(
                id: j['id'] as int,
                brandId: j['brandId'] as int,
                brandName: j['brandName'] as String,
                name: j['name'] as String,
                vehicleType: j['vehicleType'] as String,
              ))
          .toList();
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  @override
  Future<List<FuelTypeEntity>> getFuelTypes() async {
    try {
      final res = await dioClient.get('/vehiculos/combustibles');
      final List data = res.data as List;
      return data
          .map((j) => FuelTypeEntity(
                id: j['id'] as int,
                name: j['name'] as String,
                shortName: j['shortName'] as String,
              ))
          .toList();
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  @override
  Future<List<VehicleEntity>> getVehicles() async {
    try {
      final res = await dioClient.get('/usuarios/me/vehiculos');
      final List data = res.data as List;
      return data.map((j) => _vehicleFromJson(j as Map<String, dynamic>)).toList();
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  @override
  Future<VehicleEntity> addVehicle({
    required int vehicleModelId,
    required int fuelTypeId,
    required String licensePlate,
    required int year,
    required String brandName,
    required String modelName,
  }) async {
    try {
      final res = await dioClient.post('/usuarios/me/vehiculos', data: {
        'vehicleModelId': vehicleModelId,
        'fuelTypeId': fuelTypeId,
        'licensePlate': licensePlate,
        'year': year,
      });
      return _vehicleFromJson(res.data as Map<String, dynamic>);
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  @override
  Future<void> deleteVehicle(String id) async {
    try {
      await dioClient.delete('/usuarios/me/vehiculos/$id');
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    }
  }

  VehicleEntity _vehicleFromJson(Map<String, dynamic> j) {
    return VehicleEntity(
      id: j['id'] as String,
      vehicleModelId: j['vehicleModelId'] as int,
      brand: j['brandName'] as String,
      model: j['modelName'] as String,
      fuelTypeId: j['fuelTypeId'] as int,
      fuelType: FuelExtension.fromString(j['fuelTypeName'] as String),
      licensePlate: j['licensePlate'] as String,
      year: j['year'] as int,
    );
  }
}
