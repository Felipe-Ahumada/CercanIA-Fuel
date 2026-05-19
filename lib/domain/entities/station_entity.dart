import '../../domain/entities/vehicle_entity.dart';

class StationEntity {
  final String id;
  final String name;
  final int? brandId;
  final String brand;
  final double lat;
  final double lng;
  final Map<Fuel, double> prices;
  final bool inMaintenance;
  final String? address;
  final DateTime? lastSync;

  StationEntity({
    required this.id,
    required this.name,
    this.brandId,
    required this.brand,
    required this.lat,
    required this.lng,
    required this.prices,
    this.inMaintenance = false,
    this.address,
    this.lastSync,
  });

  StationEntity copyWith({
    String? id,
    String? name,
    int? brandId,
    String? brand,
    double? lat,
    double? lng,
    Map<Fuel, double>? prices,
    bool? inMaintenance,
    String? address,
    DateTime? lastSync,
  }) {
    return StationEntity(
      id: id ?? this.id,
      name: name ?? this.name,
      brandId: brandId ?? this.brandId,
      brand: brand ?? this.brand,
      lat: lat ?? this.lat,
      lng: lng ?? this.lng,
      prices: prices ?? this.prices,
      inMaintenance: inMaintenance ?? this.inMaintenance,
      address: address ?? this.address,
      lastSync: lastSync ?? this.lastSync,
    );
  }
}
