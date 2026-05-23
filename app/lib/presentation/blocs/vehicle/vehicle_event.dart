import 'package:equatable/equatable.dart';

abstract class VehicleEvent extends Equatable {
  const VehicleEvent();
  @override
  List<Object?> get props => [];
}

class LoadVehiclesEvent extends VehicleEvent {}

class LoadVehicleModelsEvent extends VehicleEvent {
  final int brandId;
  const LoadVehicleModelsEvent(this.brandId);
  @override
  List<Object?> get props => [brandId];
}

class AddVehicleEvent extends VehicleEvent {
  final int vehicleModelId;
  final int fuelTypeId;
  final String licensePlate;
  final int year;
  final String brandName;
  final String modelName;

  const AddVehicleEvent({
    required this.vehicleModelId,
    required this.fuelTypeId,
    required this.licensePlate,
    required this.year,
    required this.brandName,
    required this.modelName,
  });

  @override
  List<Object?> get props => [vehicleModelId, fuelTypeId, licensePlate, year];
}

class DeleteVehicleEvent extends VehicleEvent {
  final String id;
  const DeleteVehicleEvent(this.id);
  @override
  List<Object?> get props => [id];
}

class SetActiveVehicleEvent extends VehicleEvent {
  final String id;
  const SetActiveVehicleEvent(this.id);
  @override
  List<Object?> get props => [id];
}
