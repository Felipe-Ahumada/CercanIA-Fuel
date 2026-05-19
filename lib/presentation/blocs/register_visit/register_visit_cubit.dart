import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../domain/entities/bank_profile_entity.dart';
import '../../../domain/entities/station_entity.dart';
import '../../../domain/entities/vehicle_entity.dart';
import '../../../domain/usecases/transaction_usecases.dart';
import 'register_visit_state.dart';

class RegisterVisitCubit extends Cubit<RegisterVisitState> {
  final CreateTransactionUseCase createTransactionUseCase;

  RegisterVisitCubit({required this.createTransactionUseCase})
      : super(RegisterVisitInitial());

  void emitNoVehicles() => emit(RegisterVisitNoVehicles());

  /// Inicializa el formulario.
  ///
  /// [fuelTypeIds] mapea cada Fuel enum al id entero de la DB.
  /// [availableVehicles] es la lista completa de vehículos del usuario.
  /// [allDiscounts] son TODOS los descuentos guardados por el usuario (sin filtrar).
  void init({
    required StationEntity station,
    required VehicleEntity vehicle,
    required List<VehicleEntity> availableVehicles,
    required Map<Fuel, int> fuelTypeIds,
    required List<DiscountEntity> allDiscounts,
  }) {
    final defaultFuel = _defaultFuel(station, vehicle);
    emit(RegisterVisitReady(
      station: station,
      vehicle: vehicle,
      availableVehicles: availableVehicles,
      selectedFuel: defaultFuel,
      fuelTypeIds: fuelTypeIds,
      liters: 0,
      totalPaid: 0,
      applicableDiscounts: allDiscounts,
    ));
  }

  /// Cambia el vehículo seleccionado, actualizando el combustible por defecto.
  void updateVehicle(VehicleEntity vehicle) {
    final s = state;
    if (s is! RegisterVisitReady) return;
    final newFuel = _defaultFuel(s.station, vehicle);
    final ids = Map<Fuel, int>.from(s.fuelTypeIds)
      ..[vehicle.fuelType] = vehicle.fuelTypeId;
    emit(s.copyWith(
      vehicle: vehicle,
      selectedFuel: newFuel,
      fuelTypeIds: ids,
      clearDiscount: true,
    ));
  }

  static Fuel _defaultFuel(StationEntity station, VehicleEntity vehicle) {
    final available = station.prices.keys.toList();
    return available.contains(vehicle.fuelType)
        ? vehicle.fuelType
        : (available.isNotEmpty ? available.first : vehicle.fuelType);
  }

  void updateSelectedFuel(Fuel fuel) {
    final s = state;
    if (s is! RegisterVisitReady) return;
    // Al cambiar combustible, limpiar descuento (puede no aplicar al nuevo tipo).
    emit(s.copyWith(selectedFuel: fuel, clearDiscount: true));
  }

  void updateLiters(double liters) {
    final s = state;
    if (s is! RegisterVisitReady) return;
    emit(s.copyWith(liters: liters));
  }

  void updateTotalPaid(double amount) {
    final s = state;
    if (s is! RegisterVisitReady) return;
    emit(s.copyWith(totalPaid: amount));
  }

  void selectDiscount(int? discountId) {
    final s = state;
    if (s is! RegisterVisitReady) return;
    emit(s.copyWith(
      selectedDiscountId: discountId,
      clearDiscount: discountId == null,
    ));
  }

  Future<void> submit(String userId) async {
    final s = state;
    if (s is! RegisterVisitReady || !s.canSubmit) return;

    emit(RegisterVisitSubmitting());

    // El backend recibe unitPrice + liters y recalcula gross/final internamente.
    // Enviamos el precio implícito calculado de forma inversa desde totalPaid.
    final result = await createTransactionUseCase(
      userId: userId,
      vehicleId: s.vehicle.id,
      stationId: s.station.id,
      fuelTypeId: s.fuelTypeId,
      unitPrice: s.implicitUnitPrice,
      liters: s.liters,
      discountId: s.selectedDiscountId,
      discountAmount: s.discountAmount > 0 ? s.discountAmount : null,
    );

    result.fold(
      (f) => emit(RegisterVisitError(f.message)),
      (_) => emit(RegisterVisitSuccess()),
    );
  }
}
