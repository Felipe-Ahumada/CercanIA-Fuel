import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';
import 'glass_card.dart';

class GlassKpiCard extends StatelessWidget {
  final String label;
  final String value;
  final Color color;
  final EdgeInsets padding;
  final double valueFontSize;
  final int? valueMaxLines;

  const GlassKpiCard({
    super.key,
    required this.label,
    required this.value,
    required this.color,
    this.padding = const EdgeInsets.symmetric(horizontal: 10, vertical: 12),
    this.valueFontSize = 18,
    this.valueMaxLines,
  });

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GlassCard(
        radius: GlassTokens.radiusMd,
        level: 1,
        padding: padding,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              value,
              maxLines: valueMaxLines,
              overflow: valueMaxLines != null ? TextOverflow.ellipsis : null,
              style: TextStyle(
                fontSize: valueFontSize,
                fontWeight: FontWeight.w800,
                color: color,
                shadows: [
                  Shadow(color: color.withValues(alpha: 0.25), blurRadius: 8),
                ],
              ),
            ),
            const SizedBox(height: 2),
            Text(
              label,
              style: GlassTokens.sectionLabelStyle,
            ),
          ],
        ),
      ),
    );
  }
}
