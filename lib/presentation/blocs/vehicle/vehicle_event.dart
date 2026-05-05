import 'package:equatable/equatable.dart';
import '../../../../domain/entities/vehicle_entity.dart';

abstract class VehicleEvent extends Equatable {
  const VehicleEvent();

  @override
  List<Object> get props => [];
}

class LoadVehiclesEvent extends VehicleEvent {}

class AddVehicleEvent extends VehicleEvent {
  final String marca;
  final String modelo;
  final Fuel tipoCombustible;

  const AddVehicleEvent({
    required this.marca,
    required this.modelo,
    required this.tipoCombustible,
  });

  @override
  List<Object> get props => [marca, modelo, tipoCombustible];
}

class DeleteVehicleEvent extends VehicleEvent {
  final String id;

  const DeleteVehicleEvent(this.id);

  @override
  List<Object> get props => [id];
}

class SetActiveVehicleEvent extends VehicleEvent {
  final String id;

  const SetActiveVehicleEvent(this.id);

  @override
  List<Object> get props => [id];
}