import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';

class StationModel extends StationEntity {
  StationModel({
    required super.id,
    required super.name,
    super.brandId,
    required super.brand,
    required super.lat,
    required super.lng,
    required super.prices,
    super.inMaintenance,
    super.address,
    super.lastSync,
  });

  factory StationModel.fromJson(Map<String, dynamic> json) {
    // ── Brand ────────────────────────────────────────────────────────────────
    // Backend SummaryResponse: "brand" | Backend StationResponse: "brandName"
    final dynamic rawBrand = json['brand'] ?? json['brandName'] ?? json['marca'] ?? json['marcaNombre'];
    final String brand = rawBrand is String
        ? rawBrand
        : (rawBrand is Map ? rawBrand['nombre']?.toString() : null) ?? '';

    // ── Prices ───────────────────────────────────────────────────────────────
    // Backend StationResponse: "prices" → List<CurrentPriceResponse>
    //   { fuelTypeId, fuelTypeName, price, chargeUnit, attentionType, apiTimestamp }
    // Legacy / mock fallbacks: "precios" (map) | "preciosActuales" (list)
    final dynamic rawPrices = json['prices'] ?? json['precios'] ?? json['preciosActuales'];
    final Map<Fuel, double> prices = _parsePrices(rawPrices, json);

    // ── Sync timestamp ───────────────────────────────────────────────────────
    // Backend StationResponse: "syncAt" | mock: "ultima_sincronizacion"
    final String? rawSync = (json['syncAt'] ?? json['ultima_sincronizacion'])?.toString();
    DateTime? lastSync;
    if (rawSync != null) lastSync = DateTime.tryParse(rawSync);

    return StationModel(
      id: json['id']?.toString() ?? '',
      name: (json['name'] ?? json['nombre'] ?? '') as String,
      brandId: json['brandId'] as int?,
      brand: brand,
      lat: (json['latitude'] ?? json['lat'] ?? json['latitud'] as num?)?.toDouble() ?? 0.0,
      lng: (json['longitude'] ?? json['lng'] ?? json['longitud'] as num?)?.toDouble() ?? 0.0,
      prices: prices,
      inMaintenance: json['inMaintenance'] ?? json['enMantenimiento'] ?? false,
      address: (json['address'] ?? json['direccion']) as String?,
      lastSync: lastSync,
    );
  }

  static Map<Fuel, double> _parsePrices(dynamic rawPrices, Map<String, dynamic> json) {
    if (rawPrices == null) return {};

    // List format: backend CurrentPriceResponse or legacy mock list
    if (rawPrices is List) {
      final map = <Fuel, double>{};
      for (final item in rawPrices) {
        if (item is! Map<String, dynamic>) continue;
        final fuelValue = item['fuelTypeName'] ?? item['tipoCombustible'] ?? item['tipoCombustibleNombre'];
        final priceValue = item['price'] ?? item['precio'];
        if (fuelValue != null && priceValue != null) {
          map[FuelExtension.fromString(fuelValue.toString())] = (priceValue as num).toDouble();
        }
        // Capture sync timestamp if embedded
        final ts = item['apiTimestamp'];
        if (ts != null) json['ultima_sincronizacion'] ??= ts.toString();
      }
      return map;
    }

    // Map format: { "fuelTypeName": price } (legacy mock)
    if (rawPrices is Map<String, dynamic>) {
      final map = <Fuel, double>{};
      rawPrices.forEach((key, value) {
        if (value != null) map[FuelExtension.fromString(key)] = (value as num).toDouble();
      });
      return map;
    }

    return {};
  }
}
