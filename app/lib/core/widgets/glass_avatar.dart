import 'package:flutter/material.dart';

class GlassAvatar extends StatelessWidget {
  final String initials;
  final Color accent;
  final double size;

  const GlassAvatar({
    super.key,
    required this.initials,
    required this.accent,
    this.size = 40,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: accent.withValues(alpha: 0.10),
        shape: BoxShape.circle,
        border: Border.all(color: accent, width: 1.5),
      ),
      alignment: Alignment.center,
      child: Text(
        initials,
        style: TextStyle(
          fontSize: size * 0.36,
          fontWeight: FontWeight.w700,
          color: accent,
        ),
      ),
    );
  }
}
