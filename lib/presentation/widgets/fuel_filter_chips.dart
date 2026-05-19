import 'package:flutter/material.dart';
import '../../core/theme/glass_tokens.dart';
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
      child: Row(
        children: const [Fuel.gasoline93, Fuel.gasoline95, Fuel.gasoline97, Fuel.diesel].map((fuel) {
          final isSelected = selectedFuel == fuel;
          return Padding(
            padding: const EdgeInsets.only(right: 6),
            child: GestureDetector(
              onTap: () { if (!isSelected) onSelected(fuel); },
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
