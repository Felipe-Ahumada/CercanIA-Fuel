import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../domain/entities/vehicle_entity.dart';
import '../../../../domain/usecases/vehicle_usecases.dart';
import 'vehicle_event.dart';
import 'vehicle_state.dart';

class VehicleBloc extends Bloc<VehicleEvent, VehicleState> {
  final GetVehiclesUseCase getVehiclesUseCase;
  final AddVehicleUseCase addVehicleUseCase;
  final DeleteVehicleUseCase deleteVehicleUseCase;
  final SetActiveVehicleUseCase setActiveVehicleUseCase;

  VehicleBloc({
    required this.getVehiclesUseCase,
    required this.addVehicleUseCase,
    required this.deleteVehicleUseCase,
    required this.setActiveVehicleUseCase,
  }) : super(VehicleInitial()) {
    on<LoadVehiclesEvent>(_onLoadVehicles);
    on<AddVehicleEvent>(_onAddVehicle);
    on<DeleteVehicleEvent>(_onDeleteVehicle);
    on<SetActiveVehicleEvent>(_onSetActiveVehicle);
  }

  Future<void> _onLoadVehicles(LoadVehiclesEvent event, Emitter<VehicleState> emit) async {
    emit(VehicleLoading());
    final result = await getVehiclesUseCase();
    result.fold(
      (failure) => emit(VehicleError(failure.message)),
      (vehicles) {
        final activeId = vehicles.where((v) => v.activo).firstOrNull?.id;
        emit(VehicleLoaded(vehicles: vehicles, activeVehicleId: activeId));
      },
    );
  }

  Future<void> _onAddVehicle(AddVehicleEvent event, Emitter<VehicleState> emit) async {
    if (state is VehicleLoaded) {
      final currentState = state as VehicleLoaded;
      emit(VehicleLoading());
      
      final newVehicle = VehicleEntity(
        id: '', // Backend should generate this, but just passing a dummy empty ID initially
        marca: event.marca,
        modelo: event.modelo,
        tipoCombustible: event.tipoCombustible,
      );

      final result = await addVehicleUseCase(newVehicle);
      result.fold(
        (failure) {
          emit(VehicleError(failure.message));
          emit(currentState); // Revert back to loaded state
        },
        (vehicle) {
          final updatedVehicles = List<VehicleEntity>.from(currentState.vehicles)..add(vehicle);
          // Si es el primer vehículo, márcalo como activo localmente
          final activeId = updatedVehicles.length == 1 ? vehicle.id : currentState.activeVehicleId;
          emit(VehicleLoaded(vehicles: updatedVehicles, activeVehicleId: activeId));
        },
      );
    }
  }

  Future<void> _onDeleteVehicle(DeleteVehicleEvent event, Emitter<VehicleState> emit) async {
     if (state is VehicleLoaded) {
      final currentState = state as VehicleLoaded;
      emit(VehicleLoading());
      
      final result = await deleteVehicleUseCase(event.id);
      result.fold(
        (failure) {
           emit(VehicleError(failure.message));
           emit(currentState);
        },
        (_) {
          final updatedVehicles = currentState.vehicles.where((v) => v.id != event.id).toList();
          final activeId = currentState.activeVehicleId == event.id ? null : currentState.activeVehicleId;
          emit(VehicleLoaded(vehicles: updatedVehicles, activeVehicleId: activeId));
        }
      );
    }
  }

  Future<void> _onSetActiveVehicle(SetActiveVehicleEvent event, Emitter<VehicleState> emit) async {
    if (state is VehicleLoaded) {
      final currentState = state as VehicleLoaded;
      emit(VehicleLoading());

      final result = await setActiveVehicleUseCase(event.id);
      result.fold(
        (failure) {
           emit(VehicleError(failure.message));
           emit(currentState);
        },
        (_) {
          final updatedVehicles = currentState.vehicles.map((v) {
            return v.copyWith(activo: v.id == event.id);
          }).toList();
          emit(VehicleLoaded(vehicles: updatedVehicles, activeVehicleId: event.id));
        }
      );
    }
  }
}
