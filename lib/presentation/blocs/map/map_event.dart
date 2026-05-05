import 'package:equatable/equatable.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

abstract class MapEvent extends Equatable {
  const MapEvent();

  @override
  List<Object?> get props => [];
}

class RequestLocationAndLoadStations extends MapEvent {}

class FetchStations extends MapEvent {
  final LatLng location;

  const FetchStations(this.location);

  @override
  List<Object> get props => [location];
}

class ToggleStationFavorite extends MapEvent {
  final String stationId;
  final bool isFavorite;

  const ToggleStationFavorite({required this.stationId, required this.isFavorite});

  @override
  List<Object> get props => [stationId, isFavorite];
}
