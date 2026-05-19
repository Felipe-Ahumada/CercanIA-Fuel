import 'dart:math';
import '../../../domain/entities/bank_profile_entity.dart';
import '../../../domain/entities/station_entity.dart';
import '../../../domain/entities/vehicle_entity.dart';

abstract class RegisterVisitState {}

class RegisterVisitInitial extends RegisterVisitState {}

class RegisterVisitNoVehicles extends RegisterVisitState {}

class RegisterVisitReady extends RegisterVisitState {
  final StationEntity station;

  /// Vehículo actualmente seleccionado en el formulario.
  final VehicleEntity vehicle;

  /// Todos los vehículos del usuario — para mostrar el selector.
  final List<VehicleEntity> availableVehicles;

  /// Combustible elegido por el usuario (puede diferir del vehículo).
  final Fuel selectedFuel;

  /// Mapeo Fuel → fuelTypeId de la DB, construido desde VehicleLoaded.fuelTypes.
  final Map<Fuel, int> fuelTypeIds;

  /// Litros cargados (input del usuario).
  final double liters;

  /// Monto total pagado en caja (input del usuario = finalAmount).
  final double totalPaid;

  /// Todos los descuentos guardados por el usuario — sin filtrar por estación/día.
  final List<DiscountEntity> applicableDiscounts;
  final int? selectedDiscountId;

  RegisterVisitReady({
    required this.station,
    required this.vehicle,
    required this.availableVehicles,
    required this.selectedFuel,
    required this.fuelTypeIds,
    required this.liters,
    required this.totalPaid,
    required this.applicableDiscounts,
    this.selectedDiscountId,
  });

  // ── Derived ──────────────────────────────────────────────────────────────────

  int get fuelTypeId => fuelTypeIds[selectedFuel] ?? vehicle.fuelTypeId;

  DiscountEntity? get selectedDiscount {
    if (selectedDiscountId == null) return null;
    try {
      return applicableDiscounts.firstWhere((d) => d.id == selectedDiscountId);
    } catch (_) {
      return null;
    }
  }

  /// Monto bruto (antes de descuento), calculado de forma inversa desde totalPaid.
  ///
  /// totalPaid = gross - discount  →  gross = totalPaid + discount
  ///
  /// Para PERCENTAGE con maxCap:
  ///   gross = totalPaid / (1 − rate/100)   si no supera el cap
  ///   gross = totalPaid + maxCap            si supera el cap
  double get grossAmount {
    final d = selectedDiscount;
    if (d == null || totalPaid <= 0) return totalPaid;

    switch (d.discountType) {
      case 'PERCENTAGE':
        final rate = d.discountValue / 100;
        if (rate >= 1) return totalPaid; // protección
        final uncapped = totalPaid / (1 - rate);
        final uncappedDiscount = uncapped * rate;
        if (d.maxCap != null && uncappedDiscount > d.maxCap!) {
          return totalPaid + d.maxCap!;
        }
        return uncapped;

      case 'FIXED_AMOUNT':
        return totalPaid + d.discountValue;

      case 'FIXED_PER_LITER':
        final rawDiscount = liters * d.discountValue;
        final effectiveDiscount =
            d.maxCap != null ? rawDiscount.clamp(0.0, d.maxCap!) : rawDiscount;
        return totalPaid + effectiveDiscount;

      default:
        return totalPaid;
    }
  }

  double get discountAmount => max(0, grossAmount - totalPaid);

  /// Precio por litro implícito = monto bruto / litros.
  double get implicitUnitPrice =>
      liters > 0 && grossAmount > 0 ? grossAmount / liters : 0;

  /// Precio oficial CNE (full-service preferido si hay ambos tipos).
  double get officialCnePrice => station.prices[selectedFuel]?.displayPrice ?? 0;

  /// Diferencia absoluta entre precio implícito y precio CNE oficial.
  double get priceDelta => (implicitUnitPrice - officialCnePrice).abs();

  /// true cuando la diferencia supera $15/L y ambos valores son válidos.
  static const double _discrepancyThreshold = 15;
  bool get hasCneDiscrepancy =>
      officialCnePrice > 0 &&
      liters > 0 &&
      totalPaid > 0 &&
      priceDelta > _discrepancyThreshold;

  bool get canSubmit => liters > 0 && totalPaid > 0;

  List<Fuel> get availableFuels =>
      station.prices.keys.toList()..sort((a, b) => a.index.compareTo(b.index));

  RegisterVisitReady copyWith({
    VehicleEntity? vehicle,
    Fuel? selectedFuel,
    Map<Fuel, int>? fuelTypeIds,
    double? liters,
    double? totalPaid,
    int? selectedDiscountId,
    bool clearDiscount = false,
  }) =>
      RegisterVisitReady(
        station: station,
        vehicle: vehicle ?? this.vehicle,
        availableVehicles: availableVehicles,
        selectedFuel: selectedFuel ?? this.selectedFuel,
        fuelTypeIds: fuelTypeIds ?? this.fuelTypeIds,
        liters: liters ?? this.liters,
        totalPaid: totalPaid ?? this.totalPaid,
        applicableDiscounts: applicableDiscounts,
        selectedDiscountId: clearDiscount
            ? null
            : (selectedDiscountId ?? this.selectedDiscountId),
      );
}

class RegisterVisitSubmitting extends RegisterVisitState {}

class RegisterVisitSuccess extends RegisterVisitState {}

class RegisterVisitError extends RegisterVisitState {
  final String message;
  RegisterVisitError(this.message);
}
