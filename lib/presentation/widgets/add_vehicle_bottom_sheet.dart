import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_event.dart';

class AddVehicleBottomSheet extends StatefulWidget {
  const AddVehicleBottomSheet({super.key});

  @override
  State<AddVehicleBottomSheet> createState() => _AddVehicleBottomSheetState();

  static void show(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(context).viewInsets.bottom,
        ),
        child: const AddVehicleBottomSheet(),
      ),
    );
  }
}

class _AddVehicleBottomSheetState extends State<AddVehicleBottomSheet> {
  final _marcaController = TextEditingController();
  final _modeloController = TextEditingController();
  Fuel _selectedFuel = Fuel.bencina95;

  @override
  void dispose() {
    _marcaController.dispose();
    _modeloController.dispose();
    super.dispose();
  }

  void _onSave() {
    final marca = _marcaController.text.trim();
    final modelo = _modeloController.text.trim();

    if (marca.isNotEmpty && modelo.isNotEmpty) {
      context.read<VehicleBloc>().add(
        AddVehicleEvent(
          marca: marca,
          modelo: modelo,
          tipoCombustible: _selectedFuel,
        ),
      );
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Por favor, completa todos los campos.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            'Agregar Vehículo',
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              fontSize: 20,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          TextField(
            controller: _marcaController,
            decoration: const InputDecoration(labelText: 'Marca (ej. Toyota)'),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _modeloController,
            decoration: const InputDecoration(labelText: 'Modelo (ej. Yaris)'),
          ),
          const SizedBox(height: 16),
          const Text('Tipo de Combustible:', style: TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          DropdownButtonFormField<Fuel>(
            value: _selectedFuel,
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            ),
            items: Fuel.values.map((Fuel fuel) {
              return DropdownMenuItem<Fuel>(
                value: fuel,
                child: Text(fuel.displayName),
              );
            }).toList(),
            onChanged: (Fuel? newValue) {
              if (newValue != null) {
                setState(() {
                  _selectedFuel = newValue;
                });
              }
            },
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: _onSave,
            child: const Text('Guardar Vehículo'),
          ),
        ],
      ),
    );
  }
}
