enum Fuel {
  bencina93,
  bencina95,
  bencina97,
  diesel
}

extension FuelExtension on Fuel {
  String get displayName {
    switch (this) {
      case Fuel.bencina93:
        return 'Bencina 93';
      case Fuel.bencina95:
        return 'Bencina 95';
      case Fuel.bencina97:
        return 'Bencina 97';
      case Fuel.diesel:
        return 'Diésel';
    }
  }

  static Fuel fromString(String fuel) {
    switch (fuel.toLowerCase()) {
      case 'bencina93':
        return Fuel.bencina93;
      case 'bencina95':
        return Fuel.bencina95;
      case 'bencina97':
        return Fuel.bencina97;
      case 'diesel':
      default:
        return Fuel.diesel;
    }
  }
}

class VehicleEntity {
  final String id;
  final String marca;
  final String modelo;
  final Fuel tipoCombustible;
  final bool activo;

  VehicleEntity({
    required this.id,
    required this.marca,
    required this.modelo,
    required this.tipoCombustible,
    this.activo = false,
  });

  VehicleEntity copyWith({
    String? id,
    String? marca,
    String? modelo,
    Fuel? tipoCombustible,
    bool? activo,
  }) {
    return VehicleEntity(
      id: id ?? this.id,
      marca: marca ?? this.marca,
      modelo: modelo ?? this.modelo,
      tipoCombustible: tipoCombustible ?? this.tipoCombustible,
      activo: activo ?? this.activo,
    );
  }
}
