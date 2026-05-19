import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_event.dart';
import '../blocs/vehicle/vehicle_state.dart';

class AddVehicleBottomSheet extends StatefulWidget {
  const AddVehicleBottomSheet({super.key});

  static void show(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      barrierColor: const Color(0x55000020),
      builder: (_) => BlocProvider.value(
        value: context.read<VehicleBloc>(),
        child: Padding(
          padding: EdgeInsets.only(
            bottom: MediaQuery.of(context).viewInsets.bottom,
          ),
          child: const AddVehicleBottomSheet(),
        ),
      ),
    );
  }

  @override
  State<AddVehicleBottomSheet> createState() => _AddVehicleBottomSheetState();
}

class _AddVehicleBottomSheetState extends State<AddVehicleBottomSheet> {
  final _formKey = GlobalKey<FormState>();
  final _plateCtrl = TextEditingController();

  VehicleBrandEntity? _brand;
  VehicleModelEntity? _model;
  FuelTypeEntity? _fuelType;
  int _year = DateTime.now().year;

  @override
  void dispose() {
    _plateCtrl.dispose();
    super.dispose();
  }

  void _onBrandChanged(VehicleBrandEntity? brand) {
    setState(() {
      _brand = brand;
      _model = null;
    });
    if (brand != null) {
      context.read<VehicleBloc>().add(LoadVehicleModelsEvent(brand.id));
    }
  }

  void _onSave() {
    if (!_formKey.currentState!.validate()) return;
    if (_brand == null || _model == null || _fuelType == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Completa todos los campos.')),
      );
      return;
    }
    context.read<VehicleBloc>().add(AddVehicleEvent(
      vehicleModelId: _model!.id,
      fuelTypeId: _fuelType!.id,
      licensePlate: _plateCtrl.text.trim().toUpperCase(),
      year: _year,
      brandName: _brand!.name,
      modelName: _model!.name,
    ));
    Navigator.pop(context);
  }

  InputDecoration _fieldDecoration(String label, {String? hint}) {
    return InputDecoration(
      labelText: label,
      hintText: hint,
      labelStyle: const TextStyle(color: GlassTokens.text2, fontSize: 13),
      hintStyle: const TextStyle(color: GlassTokens.text2),
      filled: true,
      fillColor: GlassTokens.glassInput,
      contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        borderSide: const BorderSide(color: GlassTokens.border2, width: 1.5),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        borderSide: const BorderSide(color: GlassTokens.border2, width: 1.5),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        borderSide: const BorderSide(color: GlassTokens.borderAcc, width: 1.5),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        borderSide: const BorderSide(color: GlassTokens.red, width: 1.5),
      ),
      focusedErrorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        borderSide: const BorderSide(color: GlassTokens.red, width: 1.5),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<VehicleBloc, VehicleState>(
      builder: (context, state) {
        final loaded = state is VehicleLoaded ? state : null;
        final brands = loaded?.brands ?? [];
        final models = loaded?.brandModels ?? [];
        final fuels = loaded?.fuelTypes ?? [];
        final modelsError = loaded?.modelsError;

        return ClipRRect(
          borderRadius: const BorderRadius.vertical(
            top: Radius.circular(GlassTokens.radiusXl),
          ),
          child: BackdropFilter(
            filter: ImageFilter.blur(
              sigmaX: GlassTokens.blurSigmaHeavy,
              sigmaY: GlassTokens.blurSigmaHeavy,
            ),
            child: Container(
              decoration: const BoxDecoration(
                color: GlassTokens.glass2,
                borderRadius: BorderRadius.vertical(
                  top: Radius.circular(GlassTokens.radiusXl),
                ),
                border: Border(
                  top: BorderSide(color: GlassTokens.border1),
                  left: BorderSide(color: GlassTokens.border1),
                  right: BorderSide(color: GlassTokens.border1),
                ),
              ),
              padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    // Handle bar
                    Center(
                      child: Container(
                        width: 36,
                        height: 4,
                        margin: const EdgeInsets.only(bottom: 16),
                        decoration: BoxDecoration(
                          color: GlassTokens.border2,
                          borderRadius: BorderRadius.circular(2),
                        ),
                      ),
                    ),
                    const Text(
                      'Agregar Vehículo',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.w800,
                        color: GlassTokens.text0,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 20),

                    // Marca
                    DropdownButtonFormField<VehicleBrandEntity>(
                      initialValue: _brand,
                      decoration: _fieldDecoration('Marca'),
                      dropdownColor: GlassTokens.glass3,
                      style: const TextStyle(color: GlassTokens.text0, fontSize: 14),
                      items: brands
                          .map((b) => DropdownMenuItem(value: b, child: Text(b.name)))
                          .toList(),
                      onChanged: _onBrandChanged,
                      validator: (_) => _brand == null ? 'Selecciona una marca' : null,
                    ),
                    const SizedBox(height: 12),

                    // Modelo
                    if (modelsError != null && _brand != null)
                      Padding(
                        padding: const EdgeInsets.only(bottom: 8),
                        child: Text(
                          'No se pudieron cargar los modelos. Intenta de nuevo.',
                          style: const TextStyle(
                              fontSize: 12, color: GlassTokens.red),
                        ),
                      ),
                    DropdownButtonFormField<VehicleModelEntity>(
                      key: ValueKey(_brand?.id),
                      initialValue: _model,
                      decoration: _fieldDecoration('Modelo'),
                      dropdownColor: GlassTokens.glass3,
                      style: const TextStyle(color: GlassTokens.text0, fontSize: 14),
                      hint: _brand == null
                          ? const Text(
                              'Primero selecciona una marca',
                              style: TextStyle(color: GlassTokens.text2, fontSize: 13),
                            )
                          : null,
                      items: models
                          .map((m) => DropdownMenuItem(value: m, child: Text(m.name)))
                          .toList(),
                      onChanged: _brand == null ? null : (m) => setState(() => _model = m),
                      validator: (_) => _model == null ? 'Selecciona un modelo' : null,
                    ),
                    const SizedBox(height: 12),

                    // Combustible
                    DropdownButtonFormField<FuelTypeEntity>(
                      initialValue: _fuelType,
                      decoration: _fieldDecoration('Combustible'),
                      dropdownColor: GlassTokens.glass3,
                      style: const TextStyle(color: GlassTokens.text0, fontSize: 14),
                      items: fuels
                          .map((f) => DropdownMenuItem(value: f, child: Text(f.name)))
                          .toList(),
                      onChanged: (f) => setState(() => _fuelType = f),
                      validator: (_) => _fuelType == null ? 'Selecciona el combustible' : null,
                    ),
                    const SizedBox(height: 12),

                    // Patente
                    ClipRRect(
                      borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                      child: BackdropFilter(
                        filter: ImageFilter.blur(sigmaX: 20, sigmaY: 20),
                        child: TextFormField(
                          controller: _plateCtrl,
                          textCapitalization: TextCapitalization.characters,
                          style: const TextStyle(color: GlassTokens.text0),
                          decoration: _fieldDecoration('Patente', hint: 'ej: ABCD12'),
                          validator: (v) {
                            if (v == null || v.trim().isEmpty) return 'Ingresa la patente';
                            if (v.trim().length > 10) return 'Patente demasiado larga';
                            return null;
                          },
                        ),
                      ),
                    ),
                    const SizedBox(height: 12),

                    // Año
                    DropdownButtonFormField<int>(
                      initialValue: _year,
                      decoration: _fieldDecoration('Año'),
                      dropdownColor: GlassTokens.glass3,
                      style: const TextStyle(color: GlassTokens.text0, fontSize: 14),
                      items: List.generate(
                        DateTime.now().year - 1989,
                        (i) => DateTime.now().year - i,
                      ).map((y) => DropdownMenuItem(value: y, child: Text('$y'))).toList(),
                      onChanged: (y) => setState(() => _year = y ?? _year),
                    ),
                    const SizedBox(height: 24),

                    GlassButton(
                      label: 'Guardar Vehículo',
                      onPressed: _onSave,
                      width: double.infinity,
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
