import 'package:flutter/material.dart';
import '../../domain/entities/vehicle_entity.dart';

class FuelFilterChips extends StatelessWidget {
  final Fuel? selectedFuel;
  final Function(Fuel?) onSelected;

  const FuelFilterChips({
    super.key,
    required this.selectedFuel,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: Row(
        children: Fuel.values.map((fuel) {
          final isSelected = selectedFuel == fuel;
          return Padding(
            padding: const EdgeInsets.only(right: 8.0),
            child: FilterChip(
              label: Text(fuel.displayName),
              selected: isSelected,
              onSelected: (selected) {
                onSelected(selected ? fuel : null);
              },
              backgroundColor: Colors.white,
              selectedColor:
                  Theme.of(context).primaryColor.withValues(alpha: 0.2),
              checkmarkColor: Theme.of(context).primaryColor,
            ),
          );
        }).toList(),
      ),
    );
  }
}
