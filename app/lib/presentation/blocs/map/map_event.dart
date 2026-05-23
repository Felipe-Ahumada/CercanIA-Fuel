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
  final double radiusKm;

  const FetchStations(this.location, {this.radiusKm = 10.0});

  @override
  List<Object> get props => [location, radiusKm];
}
