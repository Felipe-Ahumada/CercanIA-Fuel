import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../theme/glass_tokens.dart';
import 'brand_colors.dart';

/// Renders custom map markers via [dart:ui] Canvas (no widget tree) and caches
/// the resulting [BitmapDescriptor] keyed by brand+price to avoid re-drawing
/// identical markers on every camera move.
///
/// Call [clearCache] in the owning widget's [dispose] to free memory.
class MarkerGenerator {
  MarkerGenerator._();

  // ── Cache ──────────────────────────────────────────────────────────────────
  static final Map<String, BitmapDescriptor> _cache = {};

  static void clearCache() => _cache.clear();

  // ── Public API ─────────────────────────────────────────────────────────────

  static Future<BitmapDescriptor> createCustomMarker({
    required String brand,
    required double? price,
  }) async {
    final key = _cacheKey(brand, price);
    final cached = _cache[key];
    if (cached != null) return cached;

    final descriptor = await _draw(brand, price);
    _cache[key] = descriptor;
    return descriptor;
  }

  // ── Internals ──────────────────────────────────────────────────────────────

  static String _cacheKey(String brand, double? price) =>
      '${brand.toLowerCase().trim()}|${price?.toStringAsFixed(0) ?? '--'}';



  static String _formatPrice(double price) {
    final p = price.round();
    if (p >= 1000) {
      final thousands = p ~/ 1000;
      final remainder = (p % 1000).toString().padLeft(3, '0');
      return '\$$thousands.$remainder';
    }
    return '\$$p';
  }

  // ── Canvas drawing ─────────────────────────────────────────────────────────
  // Logical dimensions (dp):  pill 104×48 + 8 tail = 104×56 total
  // Rendered at dpr 2.5     → physical 260×140
  static const double _dpr = 2.5;
  static const double _lw = 104; // logical width
  static const double _lh = 48;  // logical pill height
  static const double _tailH = 8; // logical tail height
  static const double _r = 11;   // corner radius (logical)

  static Future<BitmapDescriptor> _draw(String brand, double? price) async {
    final brandColor = BrandColors.of(brand);
    final brandLabel = brand.trim();
    final priceStr = price != null ? _formatPrice(price) : '--';

    const pw = _lw * _dpr;
    const ph = _lh * _dpr;
    const tailH = _tailH * _dpr;
    const r = _r * _dpr;

    final recorder = ui.PictureRecorder();
    final canvas = Canvas(
      recorder,
      const Rect.fromLTWH(0, 0, pw, ph + tailH),
    );

    // ── Drop shadow ──────────────────────────────────────────────────────────
    final shadowPaint = Paint()
      ..color = const Color(0x303C50B4)
      ..maskFilter = const ui.MaskFilter.blur(ui.BlurStyle.normal, 5 * _dpr);
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        const Rect.fromLTWH(2 * _dpr, 3 * _dpr, pw - 4 * _dpr, ph),
        const Radius.circular(r),
      ),
      shadowPaint,
    );

    // ── Pill background ──────────────────────────────────────────────────────
    const pillRect = Rect.fromLTWH(0, 0, pw, ph);
    final pillRRect = RRect.fromRectAndRadius(pillRect, const Radius.circular(r));

    canvas.drawRRect(
      pillRRect,
      Paint()..color = const Color(0xF0FFFFFF),
    );

    // ── Left accent bar ──────────────────────────────────────────────────────
    const barW = 5.0 * _dpr;
    canvas.drawRRect(
      RRect.fromRectAndCorners(
        const Rect.fromLTWH(0, 0, barW, ph),
        topLeft: const Radius.circular(r),
        bottomLeft: const Radius.circular(r),
      ),
      Paint()..color = brandColor,
    );

    // ── Pill border ──────────────────────────────────────────────────────────
    canvas.drawRRect(
      pillRRect,
      Paint()
        ..color = GlassTokens.border1
        ..style = PaintingStyle.stroke
        ..strokeWidth = 0.8 * _dpr,
    );

    // ── Tail / pointer ───────────────────────────────────────────────────────
    const cx = pw / 2;
    final tailPath = Path()
      ..moveTo(cx - 6 * _dpr, ph)
      ..lineTo(cx + 6 * _dpr, ph)
      ..lineTo(cx, ph + tailH)
      ..close();

    canvas.drawPath(tailPath, Paint()..color = const Color(0xF0FFFFFF));
    canvas.drawPath(
      tailPath,
      Paint()
        ..color = GlassTokens.border1
        ..style = PaintingStyle.stroke
        ..strokeWidth = 0.8 * _dpr,
    );

    // ── Brand name ───────────────────────────────────────────────────────────
    final brandPara = _buildParagraph(
      text: brandLabel,
      color: GlassTokens.text1,
      fontSize: 11 * _dpr,
      fontWeight: ui.FontWeight.w700,
      maxWidth: pw - barW - 8 * _dpr,
    );
    canvas.drawParagraph(
      brandPara,
      Offset(barW + 5 * _dpr, 7 * _dpr),
    );

    // ── Price ────────────────────────────────────────────────────────────────
    final pricePara = _buildParagraph(
      text: priceStr,
      color: GlassTokens.green,
      fontSize: 14 * _dpr,
      fontWeight: ui.FontWeight.w800,
      maxWidth: pw - barW - 8 * _dpr,
    );
    canvas.drawParagraph(
      pricePara,
      Offset(barW + 5 * _dpr, 24 * _dpr),
    );

    // ── Convert to BitmapDescriptor ──────────────────────────────────────────
    final picture = recorder.endRecording();
    final image = await picture.toImage(pw.toInt(), (ph + tailH).toInt());
    final byteData = await image.toByteData(format: ui.ImageByteFormat.png);
    image.dispose();

    return BitmapDescriptor.bytes(
      byteData!.buffer.asUint8List(),
      width: _lw,
      height: _lh + _tailH,
    );
  }

  static ui.Paragraph _buildParagraph({
    required String text,
    required Color color,
    required double fontSize,
    required ui.FontWeight fontWeight,
    required double maxWidth,
  }) {
    final builder = ui.ParagraphBuilder(
      ui.ParagraphStyle(
        textAlign: TextAlign.left,
        fontSize: fontSize,
        fontWeight: fontWeight,
        maxLines: 1,
        ellipsis: '..',
      ),
    )
      ..pushStyle(ui.TextStyle(
        color: color,
        fontSize: fontSize,
        fontWeight: fontWeight,
      ))
      ..addText(text);
    return builder.build()..layout(ui.ParagraphConstraints(width: maxWidth));
  }
}
