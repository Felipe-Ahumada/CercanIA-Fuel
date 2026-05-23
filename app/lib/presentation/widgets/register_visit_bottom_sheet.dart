import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_input.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../injection_container.dart';
import '../blocs/auth/auth_bloc.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';
import '../blocs/register_visit/register_visit_cubit.dart';
import '../blocs/register_visit/register_visit_state.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_state.dart';

class RegisterVisitBottomSheet extends StatelessWidget {
  final StationEntity station;

  const RegisterVisitBottomSheet({super.key, required this.station});

  static Future<void> show(BuildContext context, StationEntity station) {
    return showModalBottomSheet(
      context: context,
      useRootNavigator: true,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => RegisterVisitBottomSheet(station: station),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => sl<RegisterVisitCubit>(),
      child: _RegisterVisitContent(station: station),
    );
  }
}

// ── Content ───────────────────────────────────────────────────────────────────

class _RegisterVisitContent extends StatefulWidget {
  final StationEntity station;
  const _RegisterVisitContent({required this.station});

  @override
  State<_RegisterVisitContent> createState() => _RegisterVisitContentState();
}

class _RegisterVisitContentState extends State<_RegisterVisitContent> {
  final _litersCtrl    = TextEditingController();
  final _totalPaidCtrl = TextEditingController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _init());
  }

  @override
  void dispose() {
    _litersCtrl.dispose();
    _totalPaidCtrl.dispose();
    super.dispose();
  }

  void _init() {
    final vehicleState = context.read<VehicleBloc>().state;
    final bankState    = context.read<BankProfileCubit>().state;

    if (vehicleState is! VehicleLoaded || vehicleState.vehicles.isEmpty) {
      context.read<RegisterVisitCubit>().emitNoVehicles();
      return;
    }

    // Construir mapeo Fuel → fuelTypeId desde el catálogo
    final fuelTypeIds = <Fuel, int>{};
    for (final ft in vehicleState.fuelTypes) {
      fuelTypeIds[ft.fuel] = ft.id;
    }
    // Vehículo activo (o el primero disponible)
    final vehicle = vehicleState.activeVehicleId != null
        ? vehicleState.vehicles.firstWhere(
            (v) => v.id == vehicleState.activeVehicleId,
            orElse: () => vehicleState.vehicles.first,
          )
        : vehicleState.vehicles.first;

    fuelTypeIds[vehicle.fuelType] = vehicle.fuelTypeId;
    // Asegurar IDs de todos los vehículos del usuario
    for (final v in vehicleState.vehicles) {
      fuelTypeIds[v.fuelType] = v.fuelTypeId;
    }

    // Solo los descuentos del usuario que aplican HOY en ESTA estación:
    // - coinciden con la marca de la bencinera
    // - son válidos para el día de la semana actual (o no tienen restricción de día)
    final allDiscounts = bankState is BankProfileLoaded &&
            widget.station.brandId != null
        ? bankState.discountsForStation(
            widget.station.brandId!,
            DateTime.now().weekday,
          )
        : <DiscountEntity>[];

    context.read<RegisterVisitCubit>().init(
      station: widget.station,
      vehicle: vehicle,
      availableVehicles: vehicleState.vehicles,
      fuelTypeIds: fuelTypeIds,
      allDiscounts: allDiscounts,
    );
  }

  @override
  Widget build(BuildContext context) {
    final fmt       = NumberFormat('#,##0', 'es_CL');
    final priceFmt  = NumberFormat('#,##0', 'es_CL');
    final bottomInset = MediaQuery.of(context).viewInsets.bottom;

    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
      child: BackdropFilter(
        filter: ImageFilter.blur(
            sigmaX: GlassTokens.blurSigmaHeavy,
            sigmaY: GlassTokens.blurSigmaHeavy),
        child: Container(
          decoration: const BoxDecoration(
            color: GlassTokens.glass3,
            borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
            border: Border(top: BorderSide(color: GlassTokens.border1)),
          ),
          padding: EdgeInsets.fromLTRB(20, 12, 20, 20 + bottomInset),
          child: BlocConsumer<RegisterVisitCubit, RegisterVisitState>(
            listener: (context, state) {
              if (state is RegisterVisitSuccess) {
                Navigator.of(context, rootNavigator: true).pop();
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('¡Carga registrada exitosamente!'),
                    backgroundColor: GlassTokens.green,
                  ),
                );
              }
              if (state is RegisterVisitError) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text(state.message)),
                );
              }
            },
            builder: (context, state) {
              if (state is RegisterVisitNoVehicles) {
                return Padding(
                  padding: const EdgeInsets.all(32),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.directions_car_outlined,
                          size: 48, color: GlassTokens.text2),
                      const SizedBox(height: 16),
                      const Text('Sin vehículos registrados',
                          style: TextStyle(fontSize: 16,
                              fontWeight: FontWeight.w700,
                              color: GlassTokens.text0),
                          textAlign: TextAlign.center),
                      const SizedBox(height: 8),
                      const Text(
                          'Agrega un vehículo en Mis Vehículos antes de registrar una carga.',
                          style: TextStyle(
                              fontSize: 13, color: GlassTokens.text2),
                          textAlign: TextAlign.center),
                      const SizedBox(height: 20),
                      TextButton(
                        onPressed: () =>
                            Navigator.of(context, rootNavigator: true).pop(),
                        child: const Text('Cerrar'),
                      ),
                    ],
                  ),
                );
              }
              if (state is RegisterVisitInitial ||
                  state is RegisterVisitSubmitting) {
                return const Padding(
                  padding: EdgeInsets.all(40),
                  child: GlassLoadingIndicator(),
                );
              }
              if (state is! RegisterVisitReady) return const SizedBox();

              return SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Handle
                    Center(
                      child: Container(
                        width: 36, height: 4,
                        margin: const EdgeInsets.only(bottom: 16),
                        decoration: BoxDecoration(
                          color: GlassTokens.border2,
                          borderRadius: BorderRadius.circular(2),
                        ),
                      ),
                    ),

                    // Station header
                    _StationHeader(station: widget.station, vehicle: state.vehicle),
                    const SizedBox(height: 16),

                    // ── Selector de vehículo (solo si hay más de uno) ───────
                    if (state.availableVehicles.length > 1) ...[
                      const Text('VEHÍCULO',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      _VehicleSelector(
                        vehicles: state.availableVehicles,
                        selectedId: state.vehicle.id,
                        onSelect: (v) =>
                            context.read<RegisterVisitCubit>().updateVehicle(v),
                      ),
                      const SizedBox(height: 20),
                    ] else
                      const SizedBox(height: 4),

                    // ── SECCIÓN 1: Tipo de combustible ──────────────────────
                    const Text('TIPO DE COMBUSTIBLE',
                        style: GlassTokens.sectionLabelStyle),
                    const SizedBox(height: 8),
                    _FuelSelector(
                      availableFuels: state.availableFuels,
                      selectedFuel: state.selectedFuel,
                      onSelect: (f) =>
                          context.read<RegisterVisitCubit>().updateSelectedFuel(f),
                    ),
                    const SizedBox(height: 20),

                    // ── SECCIÓN 2: Litros + Monto pagado ───────────────────
                    Row(
                      children: [
                        Expanded(
                          child: _LabeledInput(
                            label: 'LITROS CARGADOS',
                            child: GlassInput(
                              controller: _litersCtrl,
                              hintText: 'ej: 40.5',
                              keyboardType: const TextInputType.numberWithOptions(
                                  decimal: true),
                              onChanged: (v) {
                                final l = double.tryParse(v) ?? 0;
                                context
                                    .read<RegisterVisitCubit>()
                                    .updateLiters(l);
                              },
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _LabeledInput(
                            label: 'TOTAL PAGADO (\$)',
                            child: GlassInput(
                              controller: _totalPaidCtrl,
                              hintText: 'ej: 36.000',
                              keyboardType: TextInputType.number,
                              onChanged: (v) {
                                final p = double.tryParse(
                                        v.replaceAll('.', '').replaceAll(',', '.')) ??
                                    0;
                                context
                                    .read<RegisterVisitCubit>()
                                    .updateTotalPaid(p);
                              },
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),

                    // ── Precio implícito calculado (read-only) ──────────────
                    if (state.liters > 0 && state.totalPaid > 0)
                      _ImplicitPriceRow(
                        implicitPrice: state.implicitUnitPrice,
                        officialPrice: state.officialCnePrice,
                        hasCneDiscrepancy: state.hasCneDiscrepancy,
                        priceDelta: state.priceDelta,
                        fmt: priceFmt,
                      ),

                    const SizedBox(height: 20),

                    // ── SECCIÓN 3: Descuento ────────────────────────────────
                    if (state.applicableDiscounts.isNotEmpty) ...[
                      const Text('DESCUENTO APLICADO',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: [
                          _DiscountChip(
                            label: 'Sin descuento',
                            selected: state.selectedDiscountId == null,
                            onTap: () => context
                                .read<RegisterVisitCubit>()
                                .selectDiscount(null),
                          ),
                          ...state.applicableDiscounts.map((d) => _DiscountChip(
                                label:
                                    '${d.cardProductName} · ${d.valueLabel}',
                                selected: state.selectedDiscountId == d.id,
                                onTap: () => context
                                    .read<RegisterVisitCubit>()
                                    .selectDiscount(d.id),
                              )),
                        ],
                      ),
                      const SizedBox(height: 20),
                    ],

                    // ── SECCIÓN 4: Resumen ──────────────────────────────────
                    _SummaryCard(state: state, fmt: fmt),
                    const SizedBox(height: 20),

                    // ── CTA ─────────────────────────────────────────────────
                    GlassButton(
                      label: 'Registrar carga',
                      width: double.infinity,
                      onPressed: state.canSubmit
                          ? () {
                              final authState =
                                  context.read<AuthBloc>().state;
                              if (authState is! AuthAuthenticated) return;
                              final backendId = authState.user.backendId;
                              if (backendId == null || backendId.isEmpty) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(
                                    content: Text(
                                      'No se pudo identificar tu usuario. '
                                      'Cierra sesión e inténtalo de nuevo.',
                                    ),
                                  ),
                                );
                                return;
                              }
                              context
                                  .read<RegisterVisitCubit>()
                                  .submit(backendId);
                            }
                          : null,
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}

// ── Sub-widgets ───────────────────────────────────────────────────────────────

class _StationHeader extends StatelessWidget {
  final StationEntity station;
  final VehicleEntity vehicle;
  const _StationHeader({required this.station, required this.vehicle});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 44, height: 44,
          decoration: BoxDecoration(
            gradient: GlassTokens.accentGradient,
            borderRadius: BorderRadius.circular(12),
          ),
          child: const Icon(Icons.local_gas_station,
              color: Colors.white, size: 22),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(station.brand,
                  style: const TextStyle(
                    fontSize: 17, fontWeight: FontWeight.w800,
                    color: GlassTokens.text0,
                  )),
              if (station.address != null)
                Text(station.address!,
                    style: const TextStyle(
                        fontSize: 11, color: GlassTokens.text2),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis),
            ],
          ),
        ),
        Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            const Icon(Icons.directions_car, size: 13, color: GlassTokens.text2),
            const SizedBox(height: 2),
            Text('${vehicle.brand} ${vehicle.model}',
                style: const TextStyle(fontSize: 11, color: GlassTokens.text2)),
          ],
        ),
      ],
    );
  }
}

/// Selector de vehículo — horizontal scroll de chips.
class _VehicleSelector extends StatelessWidget {
  final List<VehicleEntity> vehicles;
  final String selectedId;
  final ValueChanged<VehicleEntity> onSelect;

  const _VehicleSelector({
    required this.vehicles,
    required this.selectedId,
    required this.onSelect,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 56,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        itemCount: vehicles.length,
        separatorBuilder: (_, __) => const SizedBox(width: 8),
        itemBuilder: (_, i) {
          final v = vehicles[i];
          final selected = v.id == selectedId;
          return GestureDetector(
            onTap: () => onSelect(v),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 150),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
              decoration: BoxDecoration(
                gradient: selected ? GlassTokens.accentGradient : null,
                color: selected ? null : GlassTokens.glass1,
                borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                border: Border.all(
                  color: selected
                      ? GlassTokens.borderAcc
                      : GlassTokens.border2,
                  width: selected ? 1.5 : 1.0,
                ),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    Icons.directions_car,
                    size: 15,
                    color: selected
                        ? GlassTokens.onAccent
                        : GlassTokens.text2,
                  ),
                  const SizedBox(width: 7),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        '${v.brand} ${v.model}',
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w700,
                          color: selected
                              ? GlassTokens.onAccent
                              : GlassTokens.text0,
                        ),
                      ),
                      Text(
                        v.licensePlate,
                        style: TextStyle(
                          fontSize: 10,
                          color: selected
                              ? GlassTokens.onAccent.withValues(alpha: 0.75)
                              : GlassTokens.text2,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}

/// Selector de tipo de combustible como chips horizontales.
class _FuelSelector extends StatelessWidget {
  final List<Fuel> availableFuels;
  final Fuel selectedFuel;
  final ValueChanged<Fuel> onSelect;

  const _FuelSelector({
    required this.availableFuels,
    required this.selectedFuel,
    required this.onSelect,
  });

  static const _fuelColors = {
    Fuel.gasoline93: GlassTokens.green,
    Fuel.gasoline95: GlassTokens.cyan,
    Fuel.gasoline97: GlassTokens.purple,
    Fuel.diesel:     GlassTokens.orange,
    Fuel.naturalGas: GlassTokens.yellow,
  };

  @override
  Widget build(BuildContext context) {
    if (availableFuels.isEmpty) {
      return const Text('Sin combustibles disponibles',
          style: TextStyle(fontSize: 12, color: GlassTokens.text2));
    }
    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: availableFuels.map((f) {
        final selected = f == selectedFuel;
        final color = _fuelColors[f] ?? GlassTokens.text1;
        return GestureDetector(
          onTap: () => onSelect(f),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 150),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 9),
            decoration: BoxDecoration(
              color: selected
                  ? color.withValues(alpha: 0.12)
                  : GlassTokens.glass1,
              borderRadius: BorderRadius.circular(20),
              border: Border.all(
                color: selected
                    ? color.withValues(alpha: 0.5)
                    : GlassTokens.border2,
                width: selected ? 1.5 : 1.0,
              ),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 8, height: 8,
                  decoration: BoxDecoration(
                    color: selected ? color : GlassTokens.text3,
                    shape: BoxShape.circle,
                  ),
                ),
                const SizedBox(width: 6),
                Text(
                  f.displayName,
                  style: TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: selected ? color : GlassTokens.text1,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }
}

/// Fila de precio implícito calculado (solo lectura) + advertencia CNE.
class _ImplicitPriceRow extends StatelessWidget {
  final double implicitPrice;
  final double officialPrice;
  final bool hasCneDiscrepancy;
  final double priceDelta;
  final NumberFormat fmt;

  const _ImplicitPriceRow({
    required this.implicitPrice,
    required this.officialPrice,
    required this.hasCneDiscrepancy,
    required this.priceDelta,
    required this.fmt,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Precio calculado
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(
            color: hasCneDiscrepancy
                ? GlassTokens.yellow.withValues(alpha: 0.08)
                : GlassTokens.green.withValues(alpha: 0.07),
            borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
            border: Border.all(
              color: hasCneDiscrepancy
                  ? GlassTokens.yellow.withValues(alpha: 0.35)
                  : GlassTokens.green.withValues(alpha: 0.25),
            ),
          ),
          child: Row(
            children: [
              Icon(
                Icons.calculate_outlined,
                size: 16,
                color: hasCneDiscrepancy
                    ? GlassTokens.yellow
                    : GlassTokens.green,
              ),
              const SizedBox(width: 8),
              Text(
                'Precio calculado: \$${fmt.format(implicitPrice)}/L',
                style: TextStyle(
                  fontSize: 13,
                  fontWeight: FontWeight.w700,
                  color: hasCneDiscrepancy
                      ? GlassTokens.yellow
                      : GlassTokens.green,
                ),
              ),
              if (officialPrice > 0) ...[
                const Spacer(),
                Text(
                  'CNE: \$${fmt.format(officialPrice)}/L',
                  style: const TextStyle(
                      fontSize: 11, color: GlassTokens.text2),
                ),
              ],
            ],
          ),
        ),

        // Advertencia de discrepancia CNE
        if (hasCneDiscrepancy) ...[
          const SizedBox(height: 6),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(
              color: GlassTokens.yellow.withValues(alpha: 0.08),
              borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
              border: Border.all(
                  color: GlassTokens.yellow.withValues(alpha: 0.35)),
            ),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Icon(Icons.info_outline,
                    size: 15, color: GlassTokens.yellow),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'El precio calculado difiere del oficial CNE en '
                    '\$${fmt.format(priceDelta)}/L. '
                    '¿Son correctos los montos ingresados?',
                    style: const TextStyle(
                        fontSize: 11,
                        color: GlassTokens.yellow,
                        fontWeight: FontWeight.w500),
                  ),
                ),
              ],
            ),
          ),
        ],
      ],
    );
  }
}

/// Tarjeta de resumen de la transacción.
class _SummaryCard extends StatelessWidget {
  final RegisterVisitReady state;
  final NumberFormat fmt;

  const _SummaryCard({required this.state, required this.fmt});

  @override
  Widget build(BuildContext context) {
    return GlassCard(
      radius: GlassTokens.radiusMd,
      level: 1,
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          _SummaryRow(
            label: 'Monto bruto (estimado)',
            value: '\$${fmt.format(state.grossAmount)}',
          ),
          if (state.selectedDiscount != null && state.discountAmount > 0) ...[
            _SummaryRow(
              label: state.selectedDiscount!.description ??
                  state.selectedDiscount!.cardProductName,
              value: '- \$${fmt.format(state.discountAmount)}',
              valueColor: GlassTokens.green,
            ),
          ],
          const Divider(color: GlassTokens.border1, height: 20),
          _SummaryRow(
            label: 'Total pagado',
            value: '\$${fmt.format(state.totalPaid)}',
            bold: true,
          ),
          if (state.liters > 0 && state.implicitUnitPrice > 0) ...[
            const SizedBox(height: 6),
            _SummaryRow(
              label: 'Precio por litro (calculado)',
              value: '\$${fmt.format(state.implicitUnitPrice)}/L',
              valueColor: GlassTokens.text2,
            ),
          ],
        ],
      ),
    );
  }
}

class _LabeledInput extends StatelessWidget {
  final String label;
  final Widget child;
  const _LabeledInput({required this.label, required this.child});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: GlassTokens.sectionLabelStyle),
        const SizedBox(height: 6),
        child,
      ],
    );
  }
}

class _DiscountChip extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;

  const _DiscountChip(
      {required this.label, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
        decoration: BoxDecoration(
          gradient: selected ? GlassTokens.accentGradient : null,
          color: selected ? null : GlassTokens.glass1,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: selected ? GlassTokens.borderAcc : GlassTokens.border2,
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w600,
            color: selected ? GlassTokens.onAccent : GlassTokens.text1,
          ),
        ),
      ),
    );
  }
}

class _SummaryRow extends StatelessWidget {
  final String label;
  final String value;
  final Color? valueColor;
  final bool bold;

  const _SummaryRow({
    required this.label,
    required this.value,
    this.valueColor,
    this.bold = false,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: TextStyle(
            fontSize: bold ? 14 : 13,
            fontWeight: bold ? FontWeight.w700 : FontWeight.w400,
            color: bold ? GlassTokens.text0 : GlassTokens.text2,
          ),
        ),
        Text(
          value,
          style: TextStyle(
            fontSize: bold ? 16 : 13,
            fontWeight: FontWeight.w700,
            color: valueColor ??
                (bold ? GlassTokens.text0 : GlassTokens.text1),
          ),
        ),
      ],
    );
  }
}
