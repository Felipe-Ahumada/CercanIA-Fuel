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

  const VehicleLoaded({required this.vehicles, this.activeVehicleId});

  @override
  List<Object?> get props => [vehicles, activeVehicleId];
}

class VehicleError extends VehicleState {
  final String message;

  const VehicleError(this.message);

  @override
  List<Object?> get props => [message];
}