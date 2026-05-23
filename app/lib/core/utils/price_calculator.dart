import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';

class PriceCalculator {
  PriceCalculator._();

  /// Returns the price for [activeFuel] if set, otherwise the lowest available
  /// price in the station. Returns null if the station has no prices at all.
  static double? resolve(StationEntity station, Fuel? activeFuel) {
    if (station.prices.isEmpty) return null;
    if (activeFuel != null) return station.prices[activeFuel]?.displayPrice;
    return station.prices.values
        .map((e) => e.displayPrice)
        .whereType<double>()
        .reduce((a, b) => a < b ? a : b);
  }
}
