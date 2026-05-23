// ── Fuel type enum (local display names) ──────────────────────────────────────

enum Fuel { gasoline93, gasoline95, gasoline97, diesel, naturalGas }

extension FuelExtension on Fuel {
  String get displayName {
    switch (this) {
      case Fuel.gasoline93: return 'Gasolina 93';
      case Fuel.gasoline95: return 'Gasolina 95';
      case Fuel.gasoline97: return 'Gasolina 97';
      case Fuel.diesel:    return 'Diésel';
      case Fuel.naturalGas:       return 'Gas Natural';
    }
  }

  static Fuel fromString(String s) {
    final v = s.toLowerCase().replaceAll(' ', '');
    if (v.contains('93')) return Fuel.gasoline93;
    if (v.contains('95')) return Fuel.gasoline95;
    if (v.contains('97')) return Fuel.gasoline97;
    if (v.contains('gnv') || v.contains('gas')) return Fuel.naturalGas;
    return Fuel.diesel;
  }
}

// ── Catalog entities ──────────────────────────────────────────────────────────

class VehicleBrandEntity {
  final int id;
  final String name;
  const VehicleBrandEntity({required this.id, required this.name});
}

class VehicleModelEntity {
  final int id;
  final int brandId;
  final String brandName;
  final String name;
  final String vehicleType;
  const VehicleModelEntity({
    required this.id,
    required this.brandId,
    required this.brandName,
    required this.name,
    required this.vehicleType,
  });
}

class FuelTypeEntity {
  final int id;
  final String name;
  final String shortName;
  const FuelTypeEntity({required this.id, required this.name, required this.shortName});

  Fuel get fuel => FuelExtension.fromString(shortName);
}

// ── Vehicle entity ────────────────────────────────────────────────────────────

class VehicleEntity {
  final String id;
  final int vehicleModelId;
  final String brand;
  final String model;
  final int fuelTypeId;
  final Fuel fuelType;
  final String licensePlate;
  final int year;
  final bool active;

  const VehicleEntity({
    required this.id,
    required this.vehicleModelId,
    required this.brand,
    required this.model,
    required this.fuelTypeId,
    required this.fuelType,
    required this.licensePlate,
    required this.year,
    this.active = false,
  });

  VehicleEntity copyWith({
    String? id,
    int? vehicleModelId,
    String? brand,
    String? model,
    int? fuelTypeId,
    Fuel? fuelType,
    String? licensePlate,
    int? year,
    bool? active,
  }) {
    return VehicleEntity(
      id: id ?? this.id,
      vehicleModelId: vehicleModelId ?? this.vehicleModelId,
      brand: brand ?? this.brand,
      model: model ?? this.model,
      fuelTypeId: fuelTypeId ?? this.fuelTypeId,
      fuelType: fuelType ?? this.fuelType,
      licensePlate: licensePlate ?? this.licensePlate,
      year: year ?? this.year,
      active: active ?? this.active,
    );
  }
}
