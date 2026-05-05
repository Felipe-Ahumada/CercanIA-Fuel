import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';

class StationModel extends StationEntity {
  StationModel({
    required super.id,
    required super.nombre,
    required super.marca,
    required super.lat,
    required super.lng,
    required super.precios,
    required super.esFavorita,
    super.ultimaSincronizacion,
  });

  factory StationModel.fromJson(Map<String, dynamic> json) {
    Map<Fuel, double> parsePrecios(Map<String, dynamic> preciosJson) {
      final map = <Fuel, double>{};
      preciosJson.forEach((key, value) {
        final fuel = FuelExtension.fromString(key);
        if (value != null) {
           map[fuel] = (value as num).toDouble();
        }
      });
      return map;
    }

    return StationModel(
      id: json['id'] ?? '',
      nombre: json['nombre'] ?? '',
      marca: json['marca'] ?? '',
      lat: (json['lat'] as num?)?.toDouble() ?? 0.0,
      lng: (json['lng'] as num?)?.toDouble() ?? 0.0,
      precios: json['precios'] != null ? parsePrecios(json['precios']) : {},
      esFavorita: json['es_favorita'] ?? false,
      ultimaSincronizacion: json['ultima_sincronizacion'] != null 
          ? DateTime.tryParse(json['ultima_sincronizacion']) 
          : null,
    );
  }
}
