import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

class GlassToggle extends StatelessWidget {
  final bool value;
  final ValueChanged<bool> onChanged;

  const GlassToggle({super.key, required this.value, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => onChanged(!value),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        width: 46,
        height: 27,
        padding: const EdgeInsets.all(4.5),
        decoration: BoxDecoration(
          gradient: value ? GlassTokens.accentGradient : null,
          color: value ? null : GlassTokens.glass1,
          borderRadius: BorderRadius.circular(14),
          boxShadow: value
              ? const [BoxShadow(color: GlassTokens.greenGlow, blurRadius: 10)]
              : null,
        ),
        child: AnimatedAlign(
          duration: const Duration(milliseconds: 200),
          alignment: value ? Alignment.centerRight : Alignment.centerLeft,
          child: Container(
            width: 18,
            height: 18,
            decoration: const BoxDecoration(
              color: Colors.white,
              shape: BoxShape.circle,
            ),
          ),
        ),
      ),
    );
  }
}
