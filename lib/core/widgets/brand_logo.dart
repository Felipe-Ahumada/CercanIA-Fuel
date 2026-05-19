import 'package:flutter/material.dart';
import 'glass_avatar.dart';
import '../utils/brand_colors.dart';

/// Shows `assets/brands/<marca_lowercase>.png` when the file exists,
/// falls back to [GlassAvatar] with initials otherwise.
class BrandLogo extends StatelessWidget {
  final String marca;
  final double size;

  const BrandLogo({super.key, required this.marca, required this.size});

  String get _assetPath =>
      'assets/brands/${marca.toLowerCase().replaceAll(' ', '_')}.png';

  @override
  Widget build(BuildContext context) {
    final color = BrandColors.of(marca);
    final initials = marca.length >= 2
        ? marca.substring(0, 2).toUpperCase()
        : marca.toUpperCase();

    return Image.asset(
      _assetPath,
      width: size,
      height: size,
      fit: BoxFit.contain,
      errorBuilder: (_, __, ___) =>
          GlassAvatar(initials: initials, accent: color, size: size),
    );
  }
}
