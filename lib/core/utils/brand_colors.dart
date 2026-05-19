import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

class BrandColors {
  BrandColors._();

  static Color of(String marca) {
    final m = marca.toLowerCase();
    if (m.contains('copec'))     return GlassTokens.orange;
    if (m.contains('shell'))     return GlassTokens.red;
    if (m.contains('enex'))      return GlassTokens.cyan;
    if (m.contains('petrobras')) return GlassTokens.green;
    if (m.contains('primax'))    return GlassTokens.purple;
    if (m.contains('aramco'))    return GlassTokens.cyan;
    return GlassTokens.cyan;
  }
}
