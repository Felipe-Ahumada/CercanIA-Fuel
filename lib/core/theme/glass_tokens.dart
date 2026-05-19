import 'package:flutter/material.dart';

class GlassTokens {
  GlassTokens._();

  // ── BACKGROUNDS ─────────────────────────────────────────
  static const pageGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0xFFF0F4FA), Color(0xFFECF0F8)],
  );
  // Scaffold backgroundColor for standalone pages — matches gradient end color
  // so the system navigation bar area never shows black.
  static const pageBg = Color(0xFFECF0F8);
  static const headerBg = Color(0xCCFFFFFF); // rgba(255,255,255,0.80)
  static const navBg    = Color(0xF0F8FAFF); // rgba(248,250,255,0.94)

  // ── GLASS SURFACES ──────────────────────────────────────
  static const glass0     = Color(0x59FFFFFF); // 0.35
  static const glass1     = Color(0x8CFFFFFF); // 0.55
  static const glass2     = Color(0xB8FFFFFF); // 0.72
  static const glass3     = Color(0xE0FFFFFF); // 0.88
  static const glassInput = Color(0xA6FFFFFF); // 0.65

  // ── BORDERS ─────────────────────────────────────────────
  static const border0   = Color(0x0F64748B); // slate-500 8%
  static const border1   = Color(0x1E64748B); // slate-500 12%
  static const border2   = Color(0x2E64748B); // slate-500 18%
  static const borderAcc = Color(0x55059669); // green 33%

  // ── TEXT ────────────────────────────────────────────────
  static const text0 = Color(0xFF0F172A); // Slate-900 — títulos
  static const text1 = Color(0xFF475569); // Slate-600 — body
  static const text2 = Color(0xFF94A3B8); // Slate-400 — captions (sólido, WCAG AA)
  static const text3 = Color(0xFFCBD5E1); // Slate-300 — disabled
  static const onAccent = Colors.white;   // texto sobre gradiente acento

  // ── ACCENTS ─────────────────────────────────────────────
  static const green     = Color(0xFF059669); // emerald-600
  static const greenDark = Color(0xFF047857); // emerald-700 — estado pressed
  static const greenGlow = Color(0x33059669);
  static const cyan      = Color(0xFF0369A1); // sky-700 — más contraste
  static const red       = Color(0xFFDC2626); // red-600
  static const yellow    = Color(0xFFD97706); // amber-600
  static const orange    = Color(0xFFEA7C2B); // naranja cálido
  static const purple    = Color(0xFF7C3AED); // violet-600

  // ── GRADIENTS ───────────────────────────────────────────
  static const accentGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0xFF059669), Color(0xFF047857)], // verde → verde oscuro (misma familia)
  );
  static const accentGradientSoft = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0x33059669), Color(0x28047857)],
  );
  static const heroGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0x1F059669), Color(0x10047857), Color(0x00000000)],
    stops: [0.0, 0.6, 1.0],
  );

  // ── BLUR (sigma para BackdropFilter) ────────────────────
  static const blurSigma      = 20.0;
  static const blurSigmaHeavy = 40.0;

  // ── RADII ───────────────────────────────────────────────
  static const radiusSm = 10.0;
  static const radiusMd = 14.0;
  static const radiusLg = 18.0;
  static const radiusXl = 22.0;

  // ── TEXT STYLES ─────────────────────────────────────────
  static const TextStyle sectionLabelStyle = TextStyle(
    fontSize: 10,
    fontWeight: FontWeight.w700,
    letterSpacing: 0.5,
    color: text2,
  );

  // ── SHADOWS ─────────────────────────────────────────────
  static const List<BoxShadow> shadowCard = [
    BoxShadow(color: Color(0x1A3C50B4), blurRadius: 24, offset: Offset(0, 4)),
    BoxShadow(color: Color(0x123C50B4), blurRadius: 6,  offset: Offset(0, 1)),
  ];
  static const List<BoxShadow> shadowFloat = [
    BoxShadow(color: Color(0x2E3C50B4), blurRadius: 48, offset: Offset(0, 12)),
    BoxShadow(color: Color(0x1A3C50B4), blurRadius: 12, offset: Offset(0, 3)),
  ];

  // ── DARK MODE (foundation — apply via ThemeExtension en futuro) ──────────
  static const pageGradientDark = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0xFF0D1117), Color(0xFF0F172A)],
  );
  static const headerBgDark  = Color(0xCC0D1117);
  static const navBgDark     = Color(0xF00D1117);
  static const glass0Dark    = Color(0x40000000);
  static const glass1Dark    = Color(0x66000000);
  static const glass2Dark    = Color(0x99000000);
  static const glass3Dark    = Color(0xCC000000);
  static const text0Dark     = Color(0xFFF1F5F9); // Slate-100
  static const text1Dark     = Color(0xFF94A3B8); // Slate-400
  static const text2Dark     = Color(0xFF64748B); // Slate-500
  static const border1Dark   = Color(0x1EFFFFFF);
  static const border2Dark   = Color(0x33FFFFFF);
}
