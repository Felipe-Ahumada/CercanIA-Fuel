import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:go_router/go_router.dart';
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

  @override
  void initState() {
    super.initState();
    // Gatillamos la carga de las estaciones apenas inicie la pantalla
    context.read<FuelStationBloc>().add(LoadFuelStationsEvent());
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
          if (state is FuelStationLoading) {
            return const Center(child: CircularProgressIndicator());
          } else if (state is FuelStationError) {
            return Center(child: Text('Error: ${state.message}'));
          } else if (state is FuelStationLoaded) {
            // Transformamos las entidades FuelStation a Markers de Google Maps
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
                target: LatLng(-33.4489, -70.6693), // Santiago centro por defecto
                zoom: 10,
              ),
              markers: markers,
              myLocationEnabled: true,
              myLocationButtonEnabled: true,
            );
          }
          return const Center(child: Text('Iniciando...'));
        },
      ),
    );
  }
}
