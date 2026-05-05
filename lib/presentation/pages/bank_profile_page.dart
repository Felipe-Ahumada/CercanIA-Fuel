import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';
import '../../domain/entities/bank_profile_entity.dart';

class BankProfilePage extends StatefulWidget {
  const BankProfilePage({super.key});

  @override
  State<BankProfilePage> createState() => _BankProfilePageState();
}

class _BankProfilePageState extends State<BankProfilePage> {
  @override
  void initState() {
    super.initState();
    context.read<BankProfileCubit>().fetchProfileAndCatalogs();
  }

  void _showAddConvenioDialog(BuildContext context, BankProfileLoaded state) {
    String? selectedBanco;
    String? selectedTarjeta;

    showDialog(
      context: context,
      builder: (dialogContext) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Agregar Convenio/Tarjeta'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  DropdownButtonFormField<String>(
                    decoration: const InputDecoration(labelText: 'Banco'),
                    value: selectedBanco,
                    items: state.banksCatalog.map((banco) {
                      return DropdownMenuItem(value: banco, child: Text(banco));
                    }).toList(),
                    onChanged: (val) => setState(() => selectedBanco = val),
                  ),
                  const SizedBox(height: 16),
                  DropdownButtonFormField<String>(
                    decoration: const InputDecoration(labelText: 'Tipo de Tarjeta'),
                    value: selectedTarjeta,
                    items: state.cardsCatalog.map((tarjeta) {
                      return DropdownMenuItem(value: tarjeta, child: Text(tarjeta));
                    }).toList(),
                    onChanged: (val) => setState(() => selectedTarjeta = val),
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(dialogContext),
                  child: const Text('Cancelar'),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (selectedBanco != null && selectedTarjeta != null) {
                      final convenio = BankConvenio(banco: selectedBanco!, tipoTarjeta: selectedTarjeta!);
                      this.context.read<BankProfileCubit>().addConvenio(convenio);
                      Navigator.pop(dialogContext);
                    }
                  },
                  child: const Text('Agregar'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mis Métodos de Pago'),
      ),
      body: BlocConsumer<BankProfileCubit, BankProfileState>(
        listener: (context, state) {
          if (state is BankProfileError) {
             ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message)),
            );
          }
        },
        builder: (context, state) {
          if (state is BankProfileLoading || state is BankProfileInitial) {
             return const Center(child: CircularProgressIndicator());
          }

          if (state is BankProfileLoaded) {
            final convenios = state.profile.convenios;

            return Column(
              children: [
                const Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text(
                    'Agrega tus bancos para mostrarte descuentos automáticos en las estaciones de servicio cercanas. No guardamos información sensible.',
                    style: TextStyle(color: Colors.grey),
                  ),
                ),
                Expanded(
                  child: convenios.isEmpty
                      ? const Center(child: Text('No tienes convenios agregados.'))
                      : ListView.builder(
                          itemCount: convenios.length,
                          itemBuilder: (context, index) {
                            final convenio = convenios[index];
                            return ListTile(
                              leading: const Icon(Icons.credit_card),
                              title: Text(convenio.banco),
                              subtitle: Text(convenio.tipoTarjeta),
                              trailing: IconButton(
                                icon: const Icon(Icons.delete, color: Colors.red),
                                onPressed: () {
                                  context.read<BankProfileCubit>().removeConvenio(convenio);
                                },
                              ),
                            );
                          },
                        ),
                ),
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: ElevatedButton(
                    onPressed: () => _showAddConvenioDialog(context, state),
                    child: const Text('Agregar Tarjeta / Convenio'),
                  ),
                )
              ],
            );
          }

          return const Center(child: Text('Error al cargar perfil bancario'));
        },
      ),
    );
  }
}
