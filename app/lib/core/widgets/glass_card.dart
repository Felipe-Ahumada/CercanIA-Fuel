import 'dart:ui';
import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

class GlassCard extends StatelessWidget {
  final Widget child;
  final EdgeInsets padding;
  final double radius;
  final int level;
  final bool selected;
  final Color? accent;

  const GlassCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(16),
    this.radius = GlassTokens.radiusMd,
    this.level = 1,
    this.selected = false,
    this.accent,
  });

  @override
  Widget build(BuildContext context) {
    const levels = [
      GlassTokens.glass0,
      GlassTokens.glass1,
      GlassTokens.glass2,
      GlassTokens.glass3,
    ];
    final Color bg = (selected && accent != null)
        ? accent!.withValues(alpha: 0.10)
        : levels[level.clamp(0, 3)];
    final Color borderColor = (selected && accent != null)
        ? accent!.withValues(alpha: 0.33)
        : GlassTokens.border1;

    return ClipRRect(
      borderRadius: BorderRadius.circular(radius),
      child: BackdropFilter(
        filter: ImageFilter.blur(sigmaX: GlassTokens.blurSigma, sigmaY: GlassTokens.blurSigma),
        child: Container(
          padding: padding,
          decoration: BoxDecoration(
            color: bg,
            borderRadius: BorderRadius.circular(radius),
            border: Border.all(color: borderColor, width: selected ? 1.5 : 1.0),
            boxShadow: GlassTokens.shadowCard,
          ),
          child: child,
        ),
      ),
    );
  }
}
