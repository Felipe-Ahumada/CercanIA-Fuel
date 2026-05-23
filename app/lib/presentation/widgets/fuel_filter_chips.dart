import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../core/theme/glass_tokens.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../blocs/vehicle/vehicle_bloc.dart';
import '../blocs/vehicle/vehicle_state.dart';

/// Chip row for filtering by fuel type.
/// Reads active fuel types from [VehicleBloc] so the list is driven by the DB.
/// Falls back to the four canonical types if the data is not yet loaded.
class FuelFilterChips extends StatelessWidget {
  final Fuel? selectedFuel;
  final Function(Fuel?) onSelected;

  const FuelFilterChips({
    super.key,
    required this.selectedFuel,
    required this.onSelected,
  });

  // The four consumer-facing fuel types — the only ones that appear in the filter.
  // Kerosene, GNV, GLP etc. live in the DB for CNE sync and price history but
  // are not surfaced here.
  static const _canonical = {
    Fuel.gasoline93,
    Fuel.gasoline95,
    Fuel.gasoline97,
    Fuel.diesel,
  };

  static const _fallback = [
    Fuel.gasoline93,
    Fuel.gasoline95,
    Fuel.gasoline97,
    Fuel.diesel,
  ];

  @override
  Widget build(BuildContext context) {
    final vehicleState = context.watch<VehicleBloc>().state;

    // Load from DB but apply canonical whitelist so GNV, Kerosene, GLP etc.
    // never appear in the map filter even if the CNE sync created them.
    final List<Fuel> fuels;
    if (vehicleState is VehicleLoaded && vehicleState.fuelTypes.isNotEmpty) {
      final fromDb = vehicleState.fuelTypes
          .map((ft) => ft.fuel)
          .where(_canonical.contains)
          .toSet()
          .toList();
      fuels = fromDb.isNotEmpty ? fromDb : _fallback;
    } else {
      fuels = _fallback;
    }

    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: fuels.map((fuel) {
          final isSelected = selectedFuel == fuel;
          return Padding(
            padding: const EdgeInsets.only(right: 6),
            child: GestureDetector(
              onTap: () {
                if (!isSelected) onSelected(fuel);
              },
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  gradient: isSelected ? GlassTokens.accentGradient : null,
                  color: isSelected ? null : GlassTokens.glass0,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(
                    color: isSelected ? GlassTokens.borderAcc : GlassTokens.border1,
                  ),
                ),
                child: Text(
                  fuel.displayName,
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                    color: isSelected ? GlassTokens.text0 : GlassTokens.text1,
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
