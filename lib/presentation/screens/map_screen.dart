import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:go_router/go_router.dart';
import 'package:geolocator/geolocator.dart'; // Asegúrate de tener este paquete importado
import '../blocs/fuel_station_bloc.dart';
import '../blocs/fuel_station_event.dart';
import '../blocs/fuel_station_state.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  GoogleMapController? mapController;
  StreamSubscription<Position>? _positionStreamSubscription;
  bool _isFirstLocationUpdate = true; // Para centrar el mapa solo la primera vez automáticamente

  @override
  void initState() {
    super.initState();
    _startLocationTracking();
  }

  Future<void> _startLocationTracking() async {
    // 1. Verificar si los servicios de ubicación están habilitados
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      // Manejar el caso donde el GPS está apagado (mostrar diálogo, etc.)
      return;
    }

    // 2. Verificar permisos
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied || permission == LocationPermission.deniedForever) {
        // Manejar el caso donde el usuario niega los permisos permanentemente
        return;
      }
    }

    // 3. Configurar el Umbral (Threshold)
    const LocationSettings locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 50, // UMBRAL: Solo emite eventos cada 50 metros recorridos
    );

    // 4. Iniciar el Stream
    _positionStreamSubscription = Geolocator.getPositionStream(
      locationSettings: locationSettings,
    ).listen((Position position) {
      
      // Mover la cámara a la ubicación del usuario solo la primera vez que se detecta
      if (_isFirstLocationUpdate && mapController != null) {
        mapController!.animateCamera(
          CameraUpdate.newCameraPosition(
            CameraPosition(
              target: LatLng(position.latitude, position.longitude),
              zoom: 14,
            ),
          ),
        );
        _isFirstLocationUpdate = false;
      }

      // Opcional: Si tu evento recibe lat y lng, puedes pasarlos aquí para cargar
      // estaciones cercanas reales en lugar de un área fija.
      // context.read<FuelStationBloc>().add(LoadFuelStationsEvent(lat: position.latitude, lng: position.longitude));
      
      // Por ahora, dejamos la carga genérica que ya tenías:
      context.read<FuelStationBloc>().add(LoadFuelStationsEvent());
    });
  }

  @override
  void dispose() {
    // Es VITAL cancelar la suscripción del stream para evitar memory leaks
    _positionStreamSubscription?.cancel();
    mapController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('CercanIA Fuel - Mapa'),
        actions: [
          IconButton(
            icon: const Icon(Icons.account_circle),
            onPressed: () {
              context.push('/profile');
            },
          ),
        ],
      ),
      body: BlocBuilder<FuelStationBloc, FuelStationState>(
        builder: (context, state) {
          if (state is FuelStationLoading && _isFirstLocationUpdate) {
            return const Center(child: CircularProgressIndicator());
          } else if (state is FuelStationError) {
            return Center(child: Text('Error: ${state.message}'));
          } else if (state is FuelStationLoaded) {
            final markers = state.stations.map((station) {
              return Marker(
                markerId: MarkerId(station.id),
                position: LatLng(station.latitude, station.longitude),
                infoWindow: InfoWindow(
                  title: station.name,
                  snippet: station.address,
                ),
              );
            }).toSet();

            return GoogleMap(
              onMapCreated: (controller) => mapController = controller,
              initialCameraPosition: const CameraPosition(
                // Posición inicial por defecto mientras el GPS calcula la real
                target: LatLng(-33.4489, -70.6693), 
                zoom: 10,
              ),
              markers: markers,
              myLocationEnabled: true, // Punto azul local
              myLocationButtonEnabled: true, // Botón para centrar
              compassEnabled: true,
            );
          }
          // Fallback visual si el estado no es ninguno de los anteriores
          return const Center(child: CircularProgressIndicator());
        },
      ),
    );
  }
}