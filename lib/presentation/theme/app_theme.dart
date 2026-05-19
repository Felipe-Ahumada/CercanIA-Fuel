import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../core/theme/glass_tokens.dart';

class AppTheme {
  static ThemeData get lightTheme {
    final base = ThemeData(useMaterial3: true);

    // Apply Plus Jakarta Sans as the base font, then merge color/weight overrides.
    final fontTextTheme = GoogleFonts.plusJakartaSansTextTheme(base.textTheme);
    final textTheme = fontTextTheme.merge(const TextTheme(
      displayLarge:  TextStyle(color: GlassTokens.text0, fontWeight: FontWeight.w900, letterSpacing: -0.5),
      headlineLarge: TextStyle(color: GlassTokens.text0, fontWeight: FontWeight.w800, letterSpacing: -0.5),
      headlineMedium:TextStyle(color: GlassTokens.text0, fontWeight: FontWeight.w800),
      titleLarge:    TextStyle(color: GlassTokens.text0, fontWeight: FontWeight.w700),
      titleMedium:   TextStyle(color: GlassTokens.text0, fontWeight: FontWeight.w600),
      bodyLarge:     TextStyle(color: GlassTokens.text1),
      bodyMedium:    TextStyle(color: GlassTokens.text1),
      bodySmall:     TextStyle(color: GlassTokens.text2),
      labelSmall:    TextStyle(color: GlassTokens.text2, fontWeight: FontWeight.w700, letterSpacing: 0.5),
    ));

    return base.copyWith(
      textTheme: textTheme,
      colorScheme: const ColorScheme.light(
        primary:   GlassTokens.green,
        secondary: GlassTokens.cyan,
        surface:   Color(0xFFF0F4FA),
        error:     GlassTokens.red,
        onPrimary: Colors.white,
        onSurface: GlassTokens.text0,
      ),
      scaffoldBackgroundColor: const Color(0xFFF0F4FA),
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent,
        foregroundColor: GlassTokens.text0,
        centerTitle: false,
        elevation: 0,
        scrolledUnderElevation: 0,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: GlassTokens.green,
          foregroundColor: Colors.white,
          minimumSize: const Size.fromHeight(50),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
        ),
        filled: true,
        fillColor: GlassTokens.glassInput,
      ),
      navigationBarTheme: NavigationBarThemeData(
        indicatorColor: GlassTokens.greenGlow,
        labelTextStyle: WidgetStatePropertyAll(
          GoogleFonts.plusJakartaSans(
            fontSize: 11,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }
}
