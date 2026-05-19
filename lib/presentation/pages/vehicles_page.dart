import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_event.dart';
import '../blocs/vehicle/vehicle_state.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_avatar.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_empty_state.dart';
import '../../core/widgets/glass_page_header.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../widgets/add_vehicle_bottom_sheet.dart';

class VehiclesPage extends StatefulWidget {
  const VehiclesPage({super.key});

  @override
  State<VehiclesPage> createState() => _VehiclesPageState();
}

class _VehiclesPageState extends State<VehiclesPage> {
  @override
  void initState() {
    super.initState();
    context.read<VehicleBloc>().add(LoadVehiclesEvent());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Column(
        children: [
          const SafeArea(
            bottom: false,
            child: GlassPageHeader(
              title: 'Mis Vehículos',
            ),
          ),
          Expanded(
            child: BlocConsumer<VehicleBloc, VehicleState>(
              listener: (context, state) {
                if (state is VehicleError) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(state.message)),
                  );
                }
              },
              builder: (context, state) {
                if (state is VehicleLoading || state is VehicleInitial) {
                  return const GlassLoadingIndicator();
                }

                if (state is VehicleLoaded) {
                  final vehicles = state.vehicles;

                  if (vehicles.isEmpty) {
                    return const GlassEmptyState(
                      icon: Icons.directions_car_outlined,
                      title: 'Sin vehículos',
                      subtitle: 'Agrega tu primer vehículo con el botón +',
                    );
                  }

                  return ListView.separated(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
                    itemCount: vehicles.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (context, index) {
                      final vehicle = vehicles[index];
                      final isActive = vehicle.id == state.activeVehicleId;
                      return _VehicleCard(
                        vehicle: vehicle,
                        isActive: isActive,
                        onSetActive: isActive
                            ? null
                            : () => context
                                .read<VehicleBloc>()
                                .add(SetActiveVehicleEvent(vehicle.id)),
                        onDelete: () async {
                          final confirmed = await showDialog<bool>(
                            context: context,
                            builder: (_) => AlertDialog(
                              title: const Text('Eliminar vehículo'),
                              content: Text(
                                  '¿Eliminar ${vehicle.brand} ${vehicle.model}? Esta acción no se puede deshacer.'),
                              actions: [
                                TextButton(
                                  onPressed: () =>
                                      Navigator.of(context).pop(false),
                                  child: const Text('Cancelar'),
                                ),
                                TextButton(
                                  onPressed: () =>
                                      Navigator.of(context).pop(true),
                                  child: const Text('Eliminar',
                                      style:
                                          TextStyle(color: GlassTokens.red)),
                                ),
                              ],
                            ),
                          );
                          if (confirmed == true && context.mounted) {
                            context
                                .read<VehicleBloc>()
                                .add(DeleteVehicleEvent(vehicle.id));
                          }
                        },
                      );
                    },
                  );
                }

                return const Center(
                  child: Text('Error al cargar vehículos',
                      style: TextStyle(color: GlassTokens.text0)),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: GestureDetector(
        onTap: () => AddVehicleBottomSheet.show(context),
        child: ClipOval(
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: GlassTokens.blurSigma, sigmaY: GlassTokens.blurSigma),
            child: Container(
              width: 56,
              height: 56,
              decoration: const BoxDecoration(
                gradient: GlassTokens.accentGradient,
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: Color(0x4400B87A),
                    blurRadius: 20,
                    offset: Offset(0, 6),
                  ),
                ],
              ),
              child: const Icon(Icons.add, color: Colors.white, size: 26),
            ),
          ),
        ),
      ),
    );
  }
}

class _VehicleCard extends StatelessWidget {
  final VehicleEntity vehicle;
  final bool isActive;
  final VoidCallback? onSetActive;
  final VoidCallback onDelete;

  const _VehicleCard({
    required this.vehicle,
    required this.isActive,
    required this.onSetActive,
    required this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final initials = vehicle.brand.length >= 2
        ? vehicle.brand.substring(0, 2).toUpperCase()
        : vehicle.brand.toUpperCase();

    return GlassCard(
      radius: 16,
      level: 1,
      selected: isActive,
      accent: GlassTokens.green,
      padding: const EdgeInsets.all(14),
      child: Row(
        children: [
          GlassAvatar(initials: initials, accent: GlassTokens.green, size: 28),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${vehicle.brand} ${vehicle.model} ${vehicle.year}',
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w700,
                    color: GlassTokens.text0,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  '${vehicle.licensePlate} · ${vehicle.fuelType.displayName}',
                  style: const TextStyle(fontSize: 12, color: GlassTokens.text2),
                ),
                if (isActive) ...[
                  const SizedBox(height: 5),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: GlassTokens.green.withValues(alpha: 0.12),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(
                          color: GlassTokens.green.withValues(alpha: 0.25)),
                    ),
                    child: const Text(
                      'Activo',
                      style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.w700,
                        color: GlassTokens.green,
                      ),
                    ),
                  ),
                ],
              ],
            ),
          ),
          Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (onSetActive != null) ...[
                GestureDetector(
                  onTap: onSetActive,
                  child: Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    decoration: BoxDecoration(
                      color: GlassTokens.glass2,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: GlassTokens.border1),
                    ),
                    child: const Text(
                      'Activar',
                      style: TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.w700,
                        color: GlassTokens.text1,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 8),
              ],
              GestureDetector(
                onTap: onDelete,
                child: const Icon(Icons.delete_outline,
                    color: GlassTokens.red, size: 22),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
