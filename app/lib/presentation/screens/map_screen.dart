import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../blocs/map/map_bloc.dart';
import '../blocs/map/map_event.dart';
import '../blocs/map/map_state.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_state.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';
import '../widgets/fuel_filter_chips.dart';
import '../widgets/station_bottom_sheet.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/utils/geo_utils.dart';
import '../../core/utils/price_calculator.dart';
import '../../core/utils/marker_generator.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/brand_logo.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../widgets/register_visit_bottom_sheet.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/bank_profile_entity.dart';

String _markerPrice(double price) {
  final p = price.round();
  if (p >= 1000) {
    return '\$${p ~/ 1000}.${(p % 1000).toString().padLeft(3, '0')}';
  }
  return '\$$p';
}

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  GoogleMapController? _mapController;
  Fuel? _selectedFuel = Fuel.gasoline95;
  bool _isFirstLocationUpdate = true;
  Set<Marker> _customMarkers = {};
  List<StationEntity> _currentStations = [];
  int _markerGeneration = 0;
  double _currentZoom = 14.0;
  LatLng _cameraTarget = const LatLng(-33.4489, -70.6693);
  List<StationEntity> _lastGeneratedStations = [];
  Fuel? _lastGeneratedFuel;

  // Viewport-based loading
  LatLng? _lastLoadedCenter;
  Timer? _cameraIdleTimer;

  // Proximity detection
  StreamSubscription<Position>? _locationSub;
  StationEntity? _nearbyStation;
  final Set<String> _promptedStations = {};

  @override
  void initState() {
    super.initState();
    context.read<MapBloc>().add(RequestLocationAndLoadStations());
  }

  @override
  void dispose() {
    _cameraIdleTimer?.cancel();
    _locationSub?.cancel();
    _mapController?.dispose();
    MarkerGenerator.clearCache();
    super.dispose();
  }

  void _startProximityDetection() {
    _locationSub?.cancel();
    _locationSub = Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.medium,
        distanceFilter: 30, // only fires when user moves 30m
      ),
    ).listen(_onPositionUpdate);
  }

  void _onPositionUpdate(Position pos) {
    if (_currentStations.isEmpty) return;

    StationEntity? closest;
    double closestDist = double.infinity;
    for (final s in _currentStations) {
      final d = GeoUtils.distKm(pos.latitude, pos.longitude, s.lat, s.lng);
      if (d < closestDist) {
        closestDist = d;
        closest = s;
      }
    }

    const proximityKm = 0.2; // 200 m
    if (closest != null &&
        closestDist <= proximityKm &&
        !_promptedStations.contains(closest.id)) {
      if (mounted) setState(() => _nearbyStation = closest);
    } else if (_nearbyStation != null && closestDist > proximityKm) {
      if (mounted) setState(() => _nearbyStation = null);
    }
  }

  void _dismissProximityBanner() {
    if (_nearbyStation != null) {
      _promptedStations.add(_nearbyStation!.id);
    }
    setState(() => _nearbyStation = null);
  }

  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
  }

  void _onCameraMove(CameraPosition position) {
    _currentZoom = position.zoom;
    _cameraTarget = position.target;
  }

  void _onCameraIdle() {
    _generateMarkers(_filterStations(_currentStations),
        _selectedFuel ?? _getPreferredFuel());
    _scheduleViewportLoad();
  }

  double _radiusForZoom() {
    if (_currentZoom >= 15) return 3.0;
    if (_currentZoom >= 13) return 8.0;
    if (_currentZoom >= 11) return 20.0;
    return 50.0;
  }

  void _scheduleViewportLoad() {
    _cameraIdleTimer?.cancel();
    _cameraIdleTimer = Timer(const Duration(milliseconds: 800), () {
      if (!mounted) return;
      final radius = _radiusForZoom();
      // Only reload if camera moved more than half the radius from last load.
      if (_lastLoadedCenter != null) {
        final moved = GeoUtils.distKm(
          _cameraTarget.latitude,
          _cameraTarget.longitude,
          _lastLoadedCenter!.latitude,
          _lastLoadedCenter!.longitude,
        );
        if (moved < radius * 0.5) return;
      }
      _lastLoadedCenter = _cameraTarget;
      context.read<MapBloc>().add(
            FetchStations(_cameraTarget, radiusKm: radius),
          );
    });
  }

  // Limits visible markers based on zoom level — keeps the map readable when
  // zoomed out and shows all stations once the user is close enough.
  List<StationEntity> _applyZoomLimit(List<StationEntity> stations) {
    final limit = _currentZoom >= 14
        ? stations.length
        : _currentZoom >= 12
            ? 20
            : 8;
    if (stations.length <= limit) return stations;
    final sorted = [...stations]..sort((a, b) {
        final da = GeoUtils.distKm(
            _cameraTarget.latitude, _cameraTarget.longitude, a.lat, a.lng);
        final db = GeoUtils.distKm(
            _cameraTarget.latitude, _cameraTarget.longitude, b.lat, b.lng);
        return da.compareTo(db);
      });
    return sorted.take(limit).toList();
  }

  Fuel? _getPreferredFuel() {
    final vs = context.read<VehicleBloc>().state;
    if (vs is! VehicleLoaded || vs.activeVehicleId == null) return null;
    try {
      return vs.vehicles.firstWhere((v) => v.id == vs.activeVehicleId).fuelType;
    } catch (_) {
      return null;
    }
  }

  Future<void> _generateMarkers(
    List<StationEntity> stations,
    Fuel? preferredFuel,
  ) async {
    final limited = _applyZoomLimit(stations);
    if (limited == _lastGeneratedStations &&
        preferredFuel == _lastGeneratedFuel) { return; }
    _lastGeneratedStations = limited;
    _lastGeneratedFuel = preferredFuel;
    final generation = ++_markerGeneration;
    final newMarkers = <Marker>{};

    // Read user's active discounts
    final bankState = context.read<BankProfileCubit>().state;
    final dayOfWeek = DateTime.now().weekday;

    for (final station in limited) {
      // Get applicable discounts for this station
      List<DiscountEntity> stationDiscounts = [];
      if (bankState is BankProfileLoaded && station.brandId != null) {
        stationDiscounts = bankState.discountsForStation(
          station.brandId!,
          dayOfWeek,
        );
      }

      final resolved = PriceCalculator.resolveWithDiscount(
        station,
        preferredFuel,
        stationDiscounts,
      );

      final icon = await MarkerGenerator.createCustomMarker(
        brand: station.brand,
        price: resolved.displayPrice,
        hasDiscount: resolved.hasDiscount,
      );

      if (generation != _markerGeneration) return;

      newMarkers.add(Marker(
        markerId: MarkerId(station.id),
        position: LatLng(station.lat, station.lng),
        icon: icon,
        onTap: () => _showStationBottomSheet(station),
      ));
    }

    if (mounted && generation == _markerGeneration) {
      setState(() => _customMarkers = newMarkers);
    }
  }

  void _showStationBottomSheet(StationEntity station) {
    // Use the same fuel resolution logic as the marker so both show the same price.
    final displayFuel = _selectedFuel ?? _getPreferredFuel();
    showModalBottomSheet(
      context: context,
      useRootNavigator: true,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => StationBottomSheet(
        station: station,
        selectedFuel: displayFuel,
      ),
    );
  }

  List<StationEntity> _filterStations(List<StationEntity> stations) {
    if (_selectedFuel == null) return stations;
    return stations.where((s) => s.prices.containsKey(_selectedFuel)).toList();
  }

  List<StationEntity> _sortByDistance(
      List<StationEntity> stations, LatLng? origin) {
    if (origin == null) return stations;
    return [...stations]..sort((a, b) {
        final da =
            GeoUtils.distKm(origin.latitude, origin.longitude, a.lat, a.lng);
        final db =
            GeoUtils.distKm(origin.latitude, origin.longitude, b.lat, b.lng);
        return da.compareTo(db);
      });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      extendBodyBehindAppBar: true,
      body: BlocListener<BankProfileCubit, BankProfileState>(
        listener: (context, bankState) {
          if (bankState is BankProfileLoaded && _currentStations.isNotEmpty) {
            // Discounts changed — force marker regeneration
            _lastGeneratedStations = [];
            _lastGeneratedFuel = null;
            MarkerGenerator.clearCache();
            _generateMarkers(
              _filterStations(_currentStations),
              _selectedFuel ?? _getPreferredFuel(),
            );
          }
        },
        child: BlocConsumer<MapBloc, MapState>(
        listener: (context, state) {
          if (state is MapLoaded) {
            if (state.userLocation != null && _isFirstLocationUpdate) {
              _isFirstLocationUpdate = false;
              _lastLoadedCenter = state.userLocation;
              _mapController?.animateCamera(
                CameraUpdate.newLatLngZoom(state.userLocation!, 14.0),
              );
              _startProximityDetection();
            }
            if (state.inlineError != null) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.inlineError!)),
              );
            }
            _currentStations = state.stations;
            _generateMarkers(
              _filterStations(_currentStations),
              _selectedFuel ?? _getPreferredFuel(),
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
            return const GlassLoadingIndicator();
          }

          if (state is MapLoaded) {
            if (state.locationPermissionDenied) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(32),
                  child: GlassCard(
                    radius: GlassTokens.radiusLg,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(Icons.location_off_outlined,
                            size: 48, color: GlassTokens.text2),
                        const SizedBox(height: 12),
                        const Text(
                          'Permiso de ubicación requerido',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                            color: GlassTokens.text0,
                          ),
                        ),
                        const SizedBox(height: 8),
                        const Text(
                          'Se necesita acceso a tu ubicación para mostrar bencineras cercanas.',
                          textAlign: TextAlign.center,
                          style:
                              TextStyle(fontSize: 13, color: GlassTokens.text2),
                        ),
                        const SizedBox(height: 20),
                        ElevatedButton(
                          onPressed: state.locationPermissionDeniedForever
                              ? () => Geolocator.openAppSettings()
                              : () => context
                                  .read<MapBloc>()
                                  .add(RequestLocationAndLoadStations()),
                          child: Text(state.locationPermissionDeniedForever
                              ? 'Abrir Configuración'
                              : 'Reintentar'),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            }

            final filtered = _sortByDistance(
              _filterStations(state.stations),
              state.userLocation,
            );
            final bottomInset = MediaQuery.of(context).padding.bottom + 12;

            return Stack(
              children: [
                GoogleMap(
                  onMapCreated: _onMapCreated,
                  onCameraMove: _onCameraMove,
                  onCameraIdle: _onCameraIdle,
                  initialCameraPosition: CameraPosition(
                    target:
                        state.userLocation ?? const LatLng(-33.4489, -70.6693),
                    zoom: 14,
                  ),
                  markers: _customMarkers,
                  myLocationEnabled: true,
                  myLocationButtonEnabled: false,
                  compassEnabled: false,
                ),
                // Glass header with search + fuel filters
// En tu pantalla principal: Filtros libres, sin GlassCard padre
                Positioned(
                  top: 10,
                  left: 0,
                  right: 0,
                  child: SafeArea(
                    bottom: false,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: FuelFilterChips(
                        // <-- Llamamos a los chips directamente
                        selectedFuel: _selectedFuel,
                        onSelected: (fuel) {
                          setState(() => _selectedFuel = fuel);
                          _generateMarkers(
                            _filterStations(_currentStations),
                            fuel ?? _getPreferredFuel(),
                          );
                        },
                      ),
                    ),
                  ),
                ),
                // Station strip above navbar
                if (filtered.isNotEmpty)
                  Positioned(
                    left: 0,
                    right: 0,
                    bottom: bottomInset,
                    child: SizedBox(
                      height: 148,
                      child: ListView.separated(
                        scrollDirection: Axis.horizontal,
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        itemCount: filtered.length,
                        separatorBuilder: (_, __) => const SizedBox(width: 10),
                        itemBuilder: (_, i) => _StationCard(
                          station: filtered[i],
                          selectedFuel: _selectedFuel,
                          distKm: state.userLocation != null
                              ? GeoUtils.distKm(
                                  state.userLocation!.latitude,
                                  state.userLocation!.longitude,
                                  filtered[i].lat,
                                  filtered[i].lng,
                                )
                              : null,
                          onTap: () => _showStationBottomSheet(filtered[i]),
                        ),
                      ),
                    ),
                  ),
                // Proximity banner
                if (_nearbyStation != null)
                  Positioned(
                    left: 16,
                    right: 16,
                    bottom: bottomInset + (filtered.isNotEmpty ? 164 : 16),
                    child: _ProximityBanner(
                      station: _nearbyStation!,
                      onRegister: () {
                        _dismissProximityBanner();
                        RegisterVisitBottomSheet.show(context, _nearbyStation!);
                      },
                      onDismiss: _dismissProximityBanner,
                    ),
                  ),
                // Location FAB
                Positioned(
                  right: 16,
                  bottom: bottomInset + 160,
                  child: _GlassFab(
                    onTap: () {
                      if (state.userLocation != null) {
                        _mapController?.animateCamera(
                          CameraUpdate.newLatLngZoom(state.userLocation!, 14.0),
                        );
                      }
                    },
                  ),
                ),
              ],
            );
          }

          if (state is MapError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(32),
                child: GlassCard(
                  radius: GlassTokens.radiusLg,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.wifi_off_rounded,
                          size: 48, color: GlassTokens.text2),
                      const SizedBox(height: 12),
                      const Text(
                        'No se pudo cargar el mapa',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: GlassTokens.text0,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        state.message,
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                          fontSize: 13,
                          color: GlassTokens.text2,
                        ),
                      ),
                      const SizedBox(height: 20),
                      ElevatedButton.icon(
                        onPressed: () => context
                            .read<MapBloc>()
                            .add(RequestLocationAndLoadStations()),
                        icon: const Icon(Icons.refresh_rounded, size: 18),
                        label: const Text('Reintentar'),
                      ),
                    ],
                  ),
                ),
              ),
            );
          }

          return const GlassLoadingIndicator();
        },
      ),
      ),
    );
  }
}

class _StationCard extends StatelessWidget {
  final StationEntity station;
  final Fuel? selectedFuel;
  final double? distKm;
  final VoidCallback onTap;

  const _StationCard({
    required this.station,
    required this.selectedFuel,
    required this.distKm,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    // Resolve price with discounts
    final bankState = context.read<BankProfileCubit>().state;
    final dayOfWeek = DateTime.now().weekday;
    List<DiscountEntity> stationDiscounts = [];
    if (bankState is BankProfileLoaded && station.brandId != null) {
      stationDiscounts = bankState.discountsForStation(
        station.brandId!,
        dayOfWeek,
      );
    }
    final resolved = PriceCalculator.resolveWithDiscount(
      station,
      selectedFuel,
      stationDiscounts,
    );
    final distStr = distKm != null ? '${distKm!.toStringAsFixed(1)} km' : '';

    return GestureDetector(
      onTap: onTap,
      child: SizedBox(
        width: 148,
        child: GlassCard(
          radius: 14,
          level: 1,
          padding: const EdgeInsets.all(10),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              BrandLogo(marca: station.brand, size: 28),
              const SizedBox(height: 6),
              Text(
                station.brand,
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w700,
                  color: GlassTokens.text0,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 4),
              if (resolved.basePrice > 0) ...[
                if (resolved.hasDiscount) ...[
                  // Original price struck through
                  Text(
                    _markerPrice(resolved.basePrice),
                    style: const TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.w500,
                      color: GlassTokens.text2,
                      decoration: TextDecoration.lineThrough,
                      decorationColor: GlassTokens.text2,
                    ),
                  ),
                  // Discounted price in green
                  Text(
                    _markerPrice(resolved.displayPrice),
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w800,
                      color: Color(0xFF059669),
                    ),
                  ),
                ] else
                  Text(
                    _markerPrice(resolved.displayPrice),
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w800,
                      color: GlassTokens.green,
                    ),
                  ),
              ],
              if (distStr.isNotEmpty)
                Text(
                  distStr,
                  style: const TextStyle(
                    fontSize: 11,
                    color: GlassTokens.text2,
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class _GlassFab extends StatelessWidget {
  final VoidCallback onTap;

  const _GlassFab({required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: ClipOval(
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 20, sigmaY: 20),
          child: Container(
            width: 48,
            height: 48,
            decoration: const BoxDecoration(
              color: GlassTokens.glass2,
              shape: BoxShape.circle,
              boxShadow: GlassTokens.shadowFloat,
            ),
            child: const Icon(Icons.my_location,
                color: GlassTokens.green, size: 22),
          ),
        ),
      ),
    );
  }
}

class _ProximityBanner extends StatelessWidget {
  final StationEntity station;
  final VoidCallback onRegister;
  final VoidCallback onDismiss;

  const _ProximityBanner({
    required this.station,
    required this.onRegister,
    required this.onDismiss,
  });

  @override
  Widget build(BuildContext context) {
    return GlassCard(
      radius: GlassTokens.radiusLg,
      level: 2,
      padding: const EdgeInsets.fromLTRB(14, 12, 10, 12),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              gradient: GlassTokens.accentGradient,
              borderRadius: BorderRadius.circular(10),
            ),
            child: const Icon(Icons.local_gas_station,
                color: Colors.white, size: 20),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '¿Estás en ${station.brand}?',
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: GlassTokens.text0,
                  ),
                ),
                const Text(
                  'Registra tu carga ahora',
                  style: TextStyle(fontSize: 11, color: GlassTokens.text2),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          GestureDetector(
            onTap: onRegister,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
              decoration: BoxDecoration(
                gradient: GlassTokens.accentGradient,
                borderRadius: BorderRadius.circular(10),
              ),
              child: const Text(
                'Registrar',
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w700,
                  color: Colors.white,
                ),
              ),
            ),
          ),
          const SizedBox(width: 4),
          GestureDetector(
            onTap: onDismiss,
            child: const Padding(
              padding: EdgeInsets.all(6),
              child: Icon(Icons.close, size: 16, color: GlassTokens.text2),
            ),
          ),
        ],
      ),
    );
  }
}
