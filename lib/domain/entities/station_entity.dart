import '../../domain/entities/vehicle_entity.dart';

class StationEntity {
  final String id;
  final String nombre;
  final String marca;
  final double lat;
  final double lng;
  final Map<Fuel, double> precios;
  final bool esFavorita;
  final DateTime? ultimaSincronizacion;

  StationEntity({
    required this.id,
    required this.nombre,
    required this.marca,
    required this.lat,
    required this.lng,
    required this.precios,
    required this.esFavorita,
    this.ultimaSincronizacion,
  });

  StationEntity copyWith({
    String? id,
    String? nombre,
    String? marca,
    double? lat,
    double? lng,
    Map<Fuel, double>? precios,
    bool? esFavorita,
    DateTime? ultimaSincronizacion,
  }) {
    return StationEntity(
      id: id ?? this.id,
      nombre: nombre ?? this.nombre,
      marca: marca ?? this.marca,
      lat: lat ?? this.lat,
      lng: lng ?? this.lng,
      precios: precios ?? this.precios,
      esFavorita: esFavorita ?? this.esFavorita,
      ultimaSincronizacion: ultimaSincronizacion ?? this.ultimaSincronizacion,
    );
  }
}
