import '../../domain/entities/fuel_station.dart';

class FuelStationModel extends FuelStation {
  const FuelStationModel({
    required String id,
    required String name,
    required String brand,
    required String address,
    required double latitude,
    required double longitude,
    required bool isOpen,
  }) : super(
          id: id,
          name: name,
          brand: brand,
          address: address,
          latitude: latitude,
          longitude: longitude,
          isOpen: isOpen,
        );

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
