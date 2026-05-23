import 'package:equatable/equatable.dart';
import '../../../domain/entities/station_entity.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

abstract class MapState extends Equatable {
  const MapState();

  @override
  List<Object?> get props => [];
}

class MapInitial extends MapState {}

class MapLoading extends MapState {}

class MapLoaded extends MapState {
  final List<StationEntity> stations;
  final LatLng? userLocation;
  final bool locationPermissionDenied;
  final bool locationPermissionDeniedForever;
  final String? inlineError;

  const MapLoaded({
    required this.stations,
    this.userLocation,
    this.locationPermissionDenied = false,
    this.locationPermissionDeniedForever = false,
    this.inlineError,
  });

  MapLoaded copyWith({
    List<StationEntity>? stations,
    LatLng? userLocation,
    bool? locationPermissionDenied,
    bool? locationPermissionDeniedForever,
    String? inlineError,
    bool clearError = false,
  }) {
    return MapLoaded(
      stations: stations ?? this.stations,
      userLocation: userLocation ?? this.userLocation,
      locationPermissionDenied: locationPermissionDenied ?? this.locationPermissionDenied,
      locationPermissionDeniedForever: locationPermissionDeniedForever ?? this.locationPermissionDeniedForever,
      inlineError: clearError ? null : (inlineError ?? this.inlineError),
    );
  }

  @override
  List<Object?> get props => [stations, userLocation, locationPermissionDenied, locationPermissionDeniedForever, inlineError];
}

class MapError extends MapState {
  final String message;

  const MapError(this.message);

  @override
  List<Object> get props => [message];
}
