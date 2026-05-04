import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/usecases/usecase.dart';
import '../../domain/usecases/get_fuel_stations.dart';
import 'fuel_station_event.dart';
import 'fuel_station_state.dart';

class FuelStationBloc extends Bloc<FuelStationEvent, FuelStationState> {
  final GetFuelStations getFuelStations;

  FuelStationBloc({required this.getFuelStations}) : super(FuelStationInitial()) {
    on<LoadFuelStationsEvent>(_onLoadFuelStations);
  }

  Future<void> _onLoadFuelStations(
    LoadFuelStationsEvent event,
    Emitter<FuelStationState> emit,
  ) async {
    emit(FuelStationLoading());
    final failureOrStations = await getFuelStations(NoParams());
    
    failureOrStations.fold(
      (failure) => emit(FuelStationError(failure.message)),
      (stations) => emit(FuelStationLoaded(stations)),
    );
  }
}
