import 'package:equatable/equatable.dart';
import '../../../../domain/entities/vehicle_entity.dart';

abstract class VehicleState extends Equatable {
  const VehicleState();
  @override
  List<Object?> get props => [];
}

class VehicleInitial extends VehicleState {}

class VehicleLoading extends VehicleState {}

class VehicleLoaded extends VehicleState {
  final List<VehicleEntity> vehicles;
  final String? activeVehicleId;
  final List<VehicleBrandEntity> brands;
  final List<FuelTypeEntity> fuelTypes;
  final List<VehicleModelEntity> brandModels;

  final String? modelsError;

  const VehicleLoaded({
    required this.vehicles,
    this.activeVehicleId,
    this.brands = const [],
    this.fuelTypes = const [],
    this.brandModels = const [],
    this.modelsError,
  });

  VehicleLoaded copyWith({
    List<VehicleEntity>? vehicles,
    String? activeVehicleId,
    bool clearActiveVehicle = false,
    List<VehicleBrandEntity>? brands,
    List<FuelTypeEntity>? fuelTypes,
    List<VehicleModelEntity>? brandModels,
    String? modelsError,
    bool clearModelsError = false,
  }) {
    return VehicleLoaded(
      vehicles: vehicles ?? this.vehicles,
      activeVehicleId: clearActiveVehicle ? null : (activeVehicleId ?? this.activeVehicleId),
      brands: brands ?? this.brands,
      fuelTypes: fuelTypes ?? this.fuelTypes,
      brandModels: brandModels ?? this.brandModels,
      modelsError: clearModelsError ? null : (modelsError ?? this.modelsError),
    );
  }

  @override
  List<Object?> get props => [vehicles, activeVehicleId, brands, fuelTypes, brandModels, modelsError];
}

class VehicleError extends VehicleState {
  final String message;
  const VehicleError(this.message);
  @override
  List<Object?> get props => [message];
}
