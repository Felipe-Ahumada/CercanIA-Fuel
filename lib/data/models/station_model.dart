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
    Map<Fuel, double> parsePreciosMap(Map<String, dynamic> preciosJson) {
      final map = <Fuel, double>{};
      preciosJson.forEach((key, value) {
        final fuel = FuelExtension.fromString(key);
        if (value != null) {
          map[fuel] = (value as num).toDouble();
        }
      });
      return map;
    }

    Map<Fuel, double> parsePreciosList(List<dynamic> preciosJson) {
      final map = <Fuel, double>{};
      DateTime? lastSync;

      for (final item in preciosJson) {
        if (item is! Map<String, dynamic>) {
          continue;
        }

        final fuelValue =
            item['tipoCombustible'] ?? item['tipoCombustibleNombre'];
        final priceValue = item['precio'];

        if (fuelValue != null && priceValue != null) {
          map[FuelExtension.fromString(fuelValue.toString())] =
              (priceValue as num).toDouble();
        }

        final timestamp = item['apiTimestamp'];
        if (timestamp != null) {
          lastSync = DateTime.tryParse(timestamp.toString()) ?? lastSync;
        }
      }

      json['ultima_sincronizacion'] ??= lastSync?.toIso8601String();
      return map;
    }

    Map<Fuel, double> parsePrecios(dynamic preciosJson) {
      if (preciosJson is Map<String, dynamic>) {
        return parsePreciosMap(preciosJson);
      }
      if (preciosJson is List<dynamic>) {
        return parsePreciosList(preciosJson);
      }
      return {};
    }

    final preciosJson = json['precios'] ?? json['preciosActuales'];

    return StationModel(
      id: json['id'] ?? '',
      nombre: json['nombre'] ?? '',
      marca: json['marca'] ??
          json['marcaNombre'] ??
          json['marca']?['nombre'] ??
          '',
      lat: (json['lat'] as num?)?.toDouble() ??
          (json['latitud'] as num?)?.toDouble() ??
          0.0,
      lng: (json['lng'] as num?)?.toDouble() ??
          (json['longitud'] as num?)?.toDouble() ??
          0.0,
      precios: parsePrecios(preciosJson),
      esFavorita: json['es_favorita'] ?? json['favorito'] ?? false,
      ultimaSincronizacion: json['ultima_sincronizacion'] != null
          ? DateTime.tryParse(json['ultima_sincronizacion'])
          : null,
    );
  }
}
