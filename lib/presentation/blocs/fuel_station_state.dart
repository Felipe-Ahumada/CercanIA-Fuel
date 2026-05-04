import 'package:equatable/equatable.dart';
import '../../domain/entities/fuel_station.dart';

abstract class FuelStationState extends Equatable {
  const FuelStationState();

  @override
  List<Object> get props => [];
}

class FuelStationInitial extends FuelStationState {}

class FuelStationLoading extends FuelStationState {}

class FuelStationLoaded extends FuelStationState {
  final List<FuelStation> stations;

  const FuelStationLoaded(this.stations);

  @override
  List<Object> get props => [stations];
}

class FuelStationError extends FuelStationState {
  final String message;

  const FuelStationError(this.message);

  @override
  List<Object> get props => [message];
}
