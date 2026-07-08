import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../../domain/entities/vehicle_entity.dart';
import '../../../../domain/usecases/vehicle_usecases.dart';
import 'vehicle_event.dart';
import 'vehicle_state.dart';

const _kActiveVehicle = 'active_vehicle_id';

class VehicleBloc extends Bloc<VehicleEvent, VehicleState> {
  final GetVehicleBrandsUseCase getBrandsUseCase;
  final GetVehicleModelsByBrandUseCase getModelsByBrandUseCase;
  final GetFuelTypesUseCase getFuelTypesUseCase;
  final GetVehiclesUseCase getVehiclesUseCase;
  final AddVehicleUseCase addVehicleUseCase;
  final DeleteVehicleUseCase deleteVehicleUseCase;

  VehicleBloc({
    required this.getBrandsUseCase,
    required this.getModelsByBrandUseCase,
    required this.getFuelTypesUseCase,
    required this.getVehiclesUseCase,
    required this.addVehicleUseCase,
    required this.deleteVehicleUseCase,
  }) : super(VehicleInitial()) {
    on<LoadVehiclesEvent>(_onLoad);
    on<LoadVehicleModelsEvent>(_onLoadModels);
    on<AddVehicleEvent>(_onAdd);
    on<DeleteVehicleEvent>(_onDelete);
    on<SetActiveVehicleEvent>(_onSetActive);
  }

  Future<void> _onLoad(LoadVehiclesEvent event, Emitter<VehicleState> emit) async {
    emit(VehicleLoading());

    final prefs = await SharedPreferences.getInstance();
    final savedActiveId = prefs.getString(_kActiveVehicle);

    final vehiclesFuture = getVehiclesUseCase();
    final brandsFuture   = getBrandsUseCase();
    final fuelFuture     = getFuelTypesUseCase();

    final vehiclesResult = await vehiclesFuture;
    final brandsResult   = await brandsFuture;
    final fuelResult     = await fuelFuture;

    final brands = brandsResult.getOrElse(() => <VehicleBrandEntity>[]);
    final fuels  = fuelResult.getOrElse(() => <FuelTypeEntity>[]);

    if (vehiclesResult.isLeft()) {
      emit(VehicleLoaded(vehicles: const [], brands: brands, fuelTypes: fuels));
      return;
    }

    final vehicles = vehiclesResult.getOrElse(() => <VehicleEntity>[]);
    final activeId = savedActiveId != null && vehicles.any((v) => v.id == savedActiveId)
        ? savedActiveId
        : vehicles.isNotEmpty
            ? vehicles.first.id
            : null;

    emit(VehicleLoaded(
      vehicles: vehicles,
      activeVehicleId: activeId,
      brands: brands,
      fuelTypes: fuels,
    ));
  }

  Future<void> _onLoadModels(
      LoadVehicleModelsEvent event, Emitter<VehicleState> emit) async {
    if (state is! VehicleLoaded) return;
    final current = state as VehicleLoaded;

    final result = await getModelsByBrandUseCase(event.brandId);
    result.fold(
      (failure) => emit(current.copyWith(
          brandModels: [], modelsError: failure.message)),
      (models) => emit(current.copyWith(brandModels: models, clearModelsError: true)),
    );
  }

  Future<void> _onAdd(AddVehicleEvent event, Emitter<VehicleState> emit) async {
    if (state is! VehicleLoaded) return;
    final current = state as VehicleLoaded;
    emit(VehicleLoading());

    final result = await addVehicleUseCase(
      vehicleModelId: event.vehicleModelId,
      fuelTypeId: event.fuelTypeId,
      licensePlate: event.licensePlate,
      year: event.year,
      brandName: event.brandName,
      modelName: event.modelName,
    );

    await result.fold(
      (failure) async {
        emit(VehicleError(failure.message));
        emit(current);
      },
      (vehicle) async {
        final updated = List.from(current.vehicles)..add(vehicle);
        final activeId = updated.length == 1 ? vehicle.id : current.activeVehicleId;
        if (updated.length == 1) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setString(_kActiveVehicle, vehicle.id);
        }
        emit(current.copyWith(vehicles: List.from(updated), activeVehicleId: activeId));
      },
    );
  }

  Future<void> _onDelete(DeleteVehicleEvent event, Emitter<VehicleState> emit) async {
    if (state is! VehicleLoaded) return;
    final current = state as VehicleLoaded;
    emit(VehicleLoading());

    final result = await deleteVehicleUseCase(event.id);
    await result.fold(
      (failure) async {
        emit(VehicleError(failure.message));
        emit(current);
      },
      (_) async {
        final updated = current.vehicles.where((v) => v.id != event.id).toList();
        String? activeId = current.activeVehicleId;
        if (activeId == event.id) {
          activeId = updated.isNotEmpty ? updated.first.id : null;
          final prefs = await SharedPreferences.getInstance();
          if (activeId != null) {
            await prefs.setString(_kActiveVehicle, activeId);
          } else {
            await prefs.remove(_kActiveVehicle);
          }
        }
        emit(current.copyWith(
          vehicles: updated,
          activeVehicleId: activeId,
          clearActiveVehicle: activeId == null,
        ));
      },
    );
  }

  Future<void> _onSetActive(
      SetActiveVehicleEvent event, Emitter<VehicleState> emit) async {
    if (state is! VehicleLoaded) return;
    final current = state as VehicleLoaded;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_kActiveVehicle, event.id);
    emit(current.copyWith(activeVehicleId: event.id));
  }
}
