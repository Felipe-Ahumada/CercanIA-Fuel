import 'package:equatable/equatable.dart';

abstract class FuelStationEvent extends Equatable {
  const FuelStationEvent();

  @override
  List<Object> get props => [];
}

class LoadFuelStationsEvent extends FuelStationEvent {}
