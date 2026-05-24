import 'dart:math';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';

/// Result of price resolution, optionally including the best discount applied.
class ResolvedPrice {
  final double basePrice;

  /// Price after applying the best per-liter discount. Null when no applicable
  /// discount exists or the discount type cannot be expressed per-liter.
  final double? discountedPrice;

  /// The discount that was applied (if any).
  final DiscountEntity? appliedDiscount;

  const ResolvedPrice({
    required this.basePrice,
    this.discountedPrice,
    this.appliedDiscount,
  });

  /// The price to display — discounted if available, otherwise base.
  double get displayPrice => discountedPrice ?? basePrice;

  /// Whether a per-liter discount was applied and effectively reduces the price.
  bool get hasDiscount =>
      discountedPrice != null && discountedPrice! < basePrice;
}

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

  /// Resolves the display price and applies the **best** discount from
  /// [discounts] that can be expressed as a per-liter reduction.
  ///
  /// Discount types handled:
  /// - `FIXED_PER_LITER`: directly subtracted from the unit price.
  /// - `PERCENTAGE`: `price * rate / 100`, capped by [DiscountEntity.maxCap].
  /// - `FIXED_AMOUNT`: cannot be mapped to a per-liter amount without knowing
  ///   the volume, so it is **not** applied to the price. It will still be
  ///   surfaced in [allApplicableDiscounts] so the UI can display it.
  static ResolvedPrice resolveWithDiscount(
    StationEntity station,
    Fuel? activeFuel,
    List<DiscountEntity> discounts,
  ) {
    final base = resolve(station, activeFuel);
    if (base == null) {
      return const ResolvedPrice(basePrice: 0);
    }

    if (discounts.isEmpty) {
      return ResolvedPrice(basePrice: base);
    }

    // Evaluate each discount and pick the one that saves the most per liter.
    DiscountEntity? bestDiscount;
    double bestSaving = 0;

    for (final d in discounts) {
      final saving = _perLiterSaving(d, base);
      if (saving > bestSaving) {
        bestSaving = saving;
        bestDiscount = d;
      }
    }

    if (bestDiscount == null || bestSaving <= 0) {
      return ResolvedPrice(basePrice: base);
    }

    final discounted = max(0.0, base - bestSaving);
    return ResolvedPrice(
      basePrice: base,
      discountedPrice: discounted,
      appliedDiscount: bestDiscount,
    );
  }

  /// Computes how much a single discount saves **per liter** at the given
  /// [unitPrice]. Returns 0 for discount types that cannot be mapped to a
  /// per-liter amount (e.g. FIXED_AMOUNT).
  static double _perLiterSaving(DiscountEntity d, double unitPrice) {
    switch (d.discountType) {
      case 'FIXED_PER_LITER':
        return d.discountValue;

      case 'PERCENTAGE':
        final raw = unitPrice * d.discountValue / 100;
        // maxCap limits the total discount — when applied per-liter the cap
        // is effectively the cap itself (assuming 1 L reference).
        if (d.maxCap != null && raw > d.maxCap!) return d.maxCap!;
        return raw;

      case 'FIXED_AMOUNT':
        // Cannot express as per-liter without knowing the volume.
        return 0;

      default:
        return 0;
    }
  }
}
