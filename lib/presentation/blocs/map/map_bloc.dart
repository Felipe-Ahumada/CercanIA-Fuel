import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../../domain/usecases/station_usecases.dart';
import 'map_event.dart';
import 'map_state.dart';

class MapBloc extends Bloc<MapEvent, MapState> {
  final GetNearbyStationsUseCase getNearbyStationsUseCase;

  MapBloc({
    required this.getNearbyStationsUseCase,
  }) : super(MapInitial()) {
    on<RequestLocationAndLoadStations>(_onRequestLocationAndLoadStations);
    on<FetchStations>(_onFetchStations);
  }

  Future<void> _onRequestLocationAndLoadStations(
    RequestLocationAndLoadStations event,
    Emitter<MapState> emit,
  ) async {
    emit(MapLoading());

    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      emit(const MapLoaded(stations: [], locationPermissionDenied: true));
      return;
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        emit(const MapLoaded(stations: [], locationPermissionDenied: true));
        return;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      emit(const MapLoaded(
          stations: [],
          locationPermissionDenied: true,
          locationPermissionDeniedForever: true));
      return;
    }

    try {
      final position = await Geolocator.getCurrentPosition();
      final userLocation = LatLng(position.latitude, position.longitude);

      final result = await getNearbyStationsUseCase(
          userLocation.latitude, userLocation.longitude, 10.0);

      result.fold(
        (failure) => emit(MapError(failure.message)),
        (stations) => emit(MapLoaded(stations: stations, userLocation: userLocation)),
      );
    } catch (e) {
      emit(MapError(e.toString()));
    }
  }

  Future<void> _onFetchStations(
    FetchStations event,
    Emitter<MapState> emit,
  ) async {
    if (state is! MapLoaded) return;
    final currentState = state as MapLoaded;

    final result = await getNearbyStationsUseCase(
      event.location.latitude,
      event.location.longitude,
      event.radiusKm,
    );

    result.fold(
      (failure) => emit(currentState.copyWith(inlineError: failure.message)),
      (stations) => emit(currentState.copyWith(stations: stations, clearError: true)),
    );
  }
}
