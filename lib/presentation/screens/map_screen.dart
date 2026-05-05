import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:go_router/go_router.dart';
import '../blocs/map/map_bloc.dart';
import '../blocs/map/map_event.dart';
import '../blocs/map/map_state.dart';
import '../widgets/fuel_filter_chips.dart';
import '../widgets/station_bottom_sheet.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/entities/station_entity.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  GoogleMapController? _mapController;
  Fuel? _selectedFuel;
  bool _isFirstLocationUpdate = true;

  @override
  void initState() {
    super.initState();
    context.read<MapBloc>().add(RequestLocationAndLoadStations());
  }

  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
  }

  void _showStationBottomSheet(StationEntity station) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (_) {
        return StationBottomSheet(
          station: station,
          selectedFuel: _selectedFuel,
          onToggleFavorite: () {
            context.read<MapBloc>().add(ToggleStationFavorite(
              stationId: station.id, 
              isFavorite: !station.esFavorita
            ));
            Navigator.pop(context);
          },
        );
      }
    );
  }

  Set<Marker> _buildMarkers(List<StationEntity> stations) {
    return stations.map((station) {
      return Marker(
        markerId: MarkerId(station.id),
        position: LatLng(station.lat, station.lng),
        onTap: () => _showStationBottomSheet(station),
        infoWindow: InfoWindow(title: station.nombre, snippet: station.marca),
      );
    }).toSet();
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
      body: BlocConsumer<MapBloc, MapState>(
        listener: (context, state) {
          if (state is MapLoaded && state.userLocation != null && _isFirstLocationUpdate) {
            _isFirstLocationUpdate = false;
            _mapController?.animateCamera(
              CameraUpdate.newLatLngZoom(state.userLocation!, 14.0)
            );
          }
          if (state is MapError) {
             ScaffoldMessenger.of(context).showSnackBar(
               SnackBar(content: Text(state.message)),
             );
          }
        },
        builder: (context, state) {
          if (state is MapLoading && _isFirstLocationUpdate) {
            return const Center(child: CircularProgressIndicator());
          }

          if (state is MapLoaded) {
            if (state.locationPermissionDenied) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text('Se necesita permiso de ubicación para usar el mapa.'),
                    ElevatedButton(
                      onPressed: () => context.read<MapBloc>().add(RequestLocationAndLoadStations()),
                      child: const Text('Reintentar'),
                    )
                  ],
                ),
              );
            }

            return Stack(
              children: [
                GoogleMap(
                  onMapCreated: _onMapCreated,
                  initialCameraPosition: CameraPosition(
                    target: state.userLocation ?? const LatLng(-33.4489, -70.6693),
                    zoom: 10,
                  ),
                  markers: _buildMarkers(state.stations),
                  myLocationEnabled: true,
                  myLocationButtonEnabled: false,
                  compassEnabled: true,
                ),
                Positioned(
                  top: 16,
                  left: 0,
                  right: 0,
                  child: FuelFilterChips(
                    selectedFuel: _selectedFuel,
                    onSelected: (fuel) {
                      setState(() {
                         _selectedFuel = fuel;
                      });
                    },
                  ),
                ),
                Positioned(
                  bottom: 16,
                  right: 16,
                  child: FloatingActionButton(
                    onPressed: () {
                       if (state.userLocation != null) {
                         _mapController?.animateCamera(
                           CameraUpdate.newLatLngZoom(state.userLocation!, 14.0)
                         );
                       }
                    },
                    child: const Icon(Icons.my_location),
                  ),
                ),
              ],
            );
          }

          return const Center(child: CircularProgressIndicator());
        },
      ),
    );
  }
}
