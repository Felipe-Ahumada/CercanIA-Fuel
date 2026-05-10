import 'package:dio/dio.dart';

import '../../../core/config/app_config.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';
import '../../../domain/entities/vehicle_entity.dart';
import '../../mock/mock_backend_data.dart';
import 'vehicle_remote_data_source.dart';

class VehicleRemoteDataSourceImpl implements VehicleRemoteDataSource {
  final DioClient dioClient;
  final List<VehicleEntity> _mockVehicles = List.of(MockBackendData.vehicles);

  VehicleRemoteDataSourceImpl(this.dioClient);

  @override
  Future<List<VehicleEntity>> getVehicles() async {
    if (AppConfig.useMockData) {
      return List.unmodifiable(_mockVehicles);
    }

    try {
      final response = await dioClient.get('/vehicles');

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data;
        return data.map((json) => _fromJson(json)).toList();
      } else {
        throw ServerException(message: 'Error al obtener vehículos');
      }
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<VehicleEntity> addVehicle(VehicleEntity vehicle) async {
    if (AppConfig.useMockData) {
      final newVehicle = vehicle.copyWith(
        id: DateTime.now().microsecondsSinceEpoch.toString(),
        activo: _mockVehicles.isEmpty,
      );
      _mockVehicles.add(newVehicle);
      return newVehicle;
    }

    try {
      final response = await dioClient.post(
        '/vehicles',
        data: _toJson(vehicle),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return _fromJson(response.data);
      } else {
        throw ServerException(message: 'Error al agregar vehículo');
      }
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<void> deleteVehicle(String id) async {
    if (AppConfig.useMockData) {
      _mockVehicles.removeWhere((vehicle) => vehicle.id == id);
      return;
    }

    try {
      final response = await dioClient.delete('/vehicles/$id');

      if (response.statusCode != 200 && response.statusCode != 204) {
        throw ServerException(message: 'Error al eliminar vehículo');
      }
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<void> setActiveVehicle(String id) async {
    if (AppConfig.useMockData) {
      for (var index = 0; index < _mockVehicles.length; index++) {
        final vehicle = _mockVehicles[index];
        _mockVehicles[index] = vehicle.copyWith(activo: vehicle.id == id);
      }
      return;
    }

    try {
      final response = await dioClient.put('/vehicles/$id/active');

      if (response.statusCode != 200 && response.statusCode != 204) {
        throw ServerException(message: 'Error al seleccionar vehículo activo');
      }
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  VehicleEntity _fromJson(Map<String, dynamic> json) {
    return VehicleEntity(
      id: json['id'].toString(),
      marca: json['marca'],
      modelo: json['modelo'],
      tipoCombustible:
          FuelExtension.fromString(json['tipoCombustible'] ?? 'diesel'),
      activo: json['activo'] ?? false,
    );
  }

  Map<String, dynamic> _toJson(VehicleEntity vehicle) {
    return {
      'marca': vehicle.marca,
      'modelo': vehicle.modelo,
      'tipoCombustible':
          vehicle.tipoCombustible.name, // o el nombre que el backend espere
      'activo': vehicle.activo,
    };
  }
}
