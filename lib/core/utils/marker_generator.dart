import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../theme/glass_tokens.dart';
import 'brand_colors.dart';

/// Renders compact, minimalist map markers via [dart:ui] Canvas and caches
/// the resulting [BitmapDescriptor] keyed by brand+price.
///
/// Design: white pill — small brand-color dot on the left — price in dark text.
/// Call [clearCache] in the owning widget's [dispose] to free memory.
class MarkerGenerator {
  MarkerGenerator._();

  static final Map<String, BitmapDescriptor> _cache = {};

  static void clearCache() => _cache.clear();

  static Future<BitmapDescriptor> createCustomMarker({
    required String brand,
    required double? price,
  }) async {
    final key = '${brand.toLowerCase().trim()}|${price?.toStringAsFixed(0) ?? '--'}';
    return _cache[key] ??= await _draw(brand, price);
  }

  static String _formatPrice(double price) {
    final p = price.round();
    if (p >= 1000) {
      final t = p ~/ 1000;
      final r = (p % 1000).toString().padLeft(3, '0');
      return '\$$t.$r';
    }
    return '\$$p';
  }

  // Logical dimensions (dp): pill 76×32 + 6 tail = 76×38 total
  static const double _dpr  = 2.5;
  static const double _lw   = 76;   // logical width
  static const double _lh   = 32;   // logical pill height
  static const double _tailH = 6;   // logical tail height
  static const double _r    = 8;    // corner radius

  static Future<BitmapDescriptor> _draw(String brand, double? price) async {
    final brandColor = BrandColors.of(brand);
    final priceStr = price != null ? _formatPrice(price) : '--';

    const pw    = _lw * _dpr;
    const ph    = _lh * _dpr;
    const tailH = _tailH * _dpr;
    const r     = _r * _dpr;

    final recorder = ui.PictureRecorder();
    final canvas = Canvas(recorder, const Rect.fromLTWH(0, 0, pw, ph + tailH));

    // Drop shadow
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Rect.fromLTWH(1.5 * _dpr, 2 * _dpr, pw - 3 * _dpr, ph),
        const Radius.circular(r),
      ),
      Paint()
        ..color = const Color(0x28000000)
        ..maskFilter = ui.MaskFilter.blur(ui.BlurStyle.normal, 3 * _dpr),
    );

    // Pill background
    final pillRect  = const Rect.fromLTWH(0, 0, pw, ph);
    final pillRRect = RRect.fromRectAndRadius(pillRect, const Radius.circular(r));
    canvas.drawRRect(pillRRect, Paint()..color = const Color(0xFFFAFAFA));

    // Pill border
    canvas.drawRRect(
      pillRRect,
      Paint()
        ..color = const Color(0x1A000000)
        ..style  = PaintingStyle.stroke
        ..strokeWidth = 0.6 * _dpr,
    );

    // Brand-color dot (left side, vertically centered)
    const dotR  = 3.5 * _dpr;
    const dotCx = 10 * _dpr;
    const dotCy = ph / 2;
    canvas.drawCircle(
      const Offset(dotCx, dotCy),
      dotR,
      Paint()..color = brandColor,
    );

    // Tail / pointer
    const cx = pw / 2;
    final tail = Path()
      ..moveTo(cx - 5 * _dpr, ph)
      ..lineTo(cx + 5 * _dpr, ph)
      ..lineTo(cx, ph + tailH)
      ..close();
    canvas.drawPath(tail, Paint()..color = const Color(0xFFFAFAFA));
    canvas.drawPath(
      tail,
      Paint()
        ..color = const Color(0x1A000000)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 0.6 * _dpr,
    );

    // Price text — dark slate, not green
    final pricePara = _buildParagraph(
      text: priceStr,
      color: GlassTokens.text0,
      fontSize: 11.5 * _dpr,
      fontWeight: ui.FontWeight.w700,
      maxWidth: pw - dotCx - dotR - 6 * _dpr,
    );
    canvas.drawParagraph(
      pricePara,
      Offset(dotCx + dotR + 4 * _dpr, (ph - 11.5 * _dpr * 1.2) / 2),
    );

    final picture = recorder.endRecording();
    final image   = await picture.toImage(pw.toInt(), (ph + tailH).toInt());
    final bytes   = await image.toByteData(format: ui.ImageByteFormat.png);
    image.dispose();

    return BitmapDescriptor.bytes(
      bytes!.buffer.asUint8List(),
      width:  _lw,
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
    return (ui.ParagraphBuilder(
      ui.ParagraphStyle(
        fontSize:   fontSize,
        fontWeight: fontWeight,
        maxLines:   1,
        ellipsis:   '..',
      ),
    )
      ..pushStyle(ui.TextStyle(color: color, fontSize: fontSize, fontWeight: fontWeight))
      ..addText(text))
        .build()
      ..layout(ui.ParagraphConstraints(width: maxWidth));
  }
}
