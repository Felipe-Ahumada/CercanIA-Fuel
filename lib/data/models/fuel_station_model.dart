import '../../domain/entities/fuel_station.dart';

class FuelStationModel extends FuelStation {
  const FuelStationModel({
    required super.id,
    required super.name,
    required super.brand,
    required super.address,
    required super.latitude,
    required super.longitude,
    required super.isOpen,
  });

  factory FuelStationModel.fromJson(Map<String, dynamic> json) {
    return FuelStationModel(
      id: json['id'],
      name: json['nombre'],
      brand: json['marca_id'].toString(), // Asumiendo que vendrá un ID de marca
      address: json['direccion'],
      latitude: (json['latitud'] as num).toDouble(),
      longitude: (json['longitud'] as num).toDouble(),
      isOpen: !(json['en_mantenimiento'] as bool? ?? false),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nombre': name,
      'marca_id': brand,
      'direccion': address,
      'latitud': latitude,
      'longitud': longitude,
      'en_mantenimiento': !isOpen,
    };
  }
}
