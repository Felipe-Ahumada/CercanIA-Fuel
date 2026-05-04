import '../../models/fuel_station_model.dart';

abstract class FuelStationRemoteDataSource {
  Future<List<FuelStationModel>> getFuelStations();
}

class FuelStationRemoteDataSourceImpl implements FuelStationRemoteDataSource {
  @override
  Future<List<FuelStationModel>> getFuelStations() async {
    // Simula un retardo de red
    await Future.delayed(const Duration(seconds: 1));

    // Datos mockeados
    final mockData = [
      {
        "id": "1",
        "codigo_api": "copec_1",
        "marca_id": 1,
        "comuna_id": 1,
        "nombre": "Copec - Av. Kennedy",
        "direccion": "Av. Kennedy 9001, Vitacura",
        "latitud": -33.3916,
        "longitud": -70.5609,
        "telefono": "+56221234567",
        "email": "contacto@copec.cl",
        "en_mantenimiento": false,
        "activo": true,
        "created_at": "2023-10-27T10:00:00Z",
        "updated_at": "2023-10-27T10:00:00Z",
        "sync_at": "2023-10-27T10:00:00Z"
      },
      {
        "id": "2",
        "codigo_api": "shell_2",
        "marca_id": 2,
        "comuna_id": 2,
        "nombre": "Shell - Pajaritos",
        "direccion": "Av. Pajaritos 45, Maipú",
        "latitud": -33.4863,
        "longitud": -70.7765,
        "telefono": "+56229876543",
        "email": "contacto@shell.cl",
        "en_mantenimiento": true,
        "activo": true,
        "created_at": "2023-10-27T11:00:00Z",
        "updated_at": "2023-10-27T11:00:00Z",
        "sync_at": "2023-10-27T11:00:00Z"
      }
    ];

    return mockData.map((json) => FuelStationModel.fromJson(json)).toList();
  }
}
