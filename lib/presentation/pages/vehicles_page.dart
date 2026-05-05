import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_event.dart';
import '../blocs/vehicle/vehicle_state.dart';
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
      appBar: AppBar(
        title: const Text('Mis Vehículos'),
      ),
      body: BlocConsumer<VehicleBloc, VehicleState>(
        listener: (context, state) {
          if (state is VehicleError) {
             ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message)),
            );
          }
        },
        builder: (context, state) {
          if (state is VehicleLoading || state is VehicleInitial) {
            return const Center(child: CircularProgressIndicator());
          }

          if (state is VehicleLoaded) {
            final vehicles = state.vehicles;

            if (vehicles.isEmpty) {
              return const Center(
                child: Text('No tienes vehículos registrados.'),
              );
            }

            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: vehicles.length,
              itemBuilder: (context, index) {
                final vehicle = vehicles[index];
                final isActive = vehicle.id == state.activeVehicleId;

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  shape: RoundedRectangleBorder(
                    side: BorderSide(
                      color: isActive ? Theme.of(context).colorScheme.primary : Colors.transparent,
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: ListTile(
                    leading: Icon(
                      Icons.directions_car,
                      color: isActive ? Theme.of(context).colorScheme.primary : Colors.grey,
                      size: 32,
                    ),
                    title: Text('${vehicle.marca} ${vehicle.modelo}', style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text(vehicle.tipoCombustible.displayName),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        if (!isActive)
                          TextButton(
                            onPressed: () {
                              context.read<VehicleBloc>().add(SetActiveVehicleEvent(vehicle.id));
                            },
                            child: const Text('Activar'),
                          ),
                        IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          onPressed: () {
                             context.read<VehicleBloc>().add(DeleteVehicleEvent(vehicle.id));
                          },
                        ),
                      ],
                    ),
                  ),
                );
              },
            );
          }

          return const Center(child: Text('Error al cargar vehículos'));
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          AddVehicleBottomSheet.show(context);
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
