import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'brand_colors.dart';

/// Renders compact, minimalist map markers via [dart:ui] Canvas and caches
/// the resulting [BitmapDescriptor] keyed by brand+price+discount.
///
/// Design: white pill — brand logo circle on the left — price in dark text.
/// When a discount is active the pill turns green-tinted and price text is green.
/// Call [clearCache] in the owning widget's [dispose] to free memory.
class MarkerGenerator {
  MarkerGenerator._();

  static final Map<String, BitmapDescriptor> _cache = {};

  /// Pre-decoded brand logo images keyed by lowercase-no-spaces brand name.
  static final Map<String, ui.Image> _logoCache = {};

  static void clearCache() {
    _cache.clear();
    for (final img in _logoCache.values) {
      img.dispose();
    }
    _logoCache.clear();
  }

  static Future<BitmapDescriptor> createCustomMarker({
    required String brand,
    required double? price,
    bool hasDiscount = false,
  }) async {
    final key = '${brand.toLowerCase().trim()}|${price?.toStringAsFixed(0) ?? '--'}|${hasDiscount ? 'd' : 'n'}';
    return _cache[key] ??= await _draw(brand, price, hasDiscount);
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

  // ── Layout constants (logical dp, scaled by _dpr for HiDPI) ──────────────
  static const double _dpr   = 3.0;
  static const double _lw    = 96;    // logical width
  static const double _lh    = 36;    // logical pill height
  static const double _tailH = 5;     // logical tail height
  static const double _imgSz = 24;    // logo circle diameter
  static const double _pad   = 6;     // inner padding

  /// Try to load and cache the brand logo from assets.
  static Future<ui.Image?> _loadLogo(String brand) async {
    final safeBrand = brand.toLowerCase().replaceAll(' ', '');
    if (_logoCache.containsKey(safeBrand)) return _logoCache[safeBrand];
    try {
      final data = await rootBundle.load('assets/brands/$safeBrand.png');
      final targetPx = (_imgSz * _dpr).toInt();
      final codec = await ui.instantiateImageCodec(
        data.buffer.asUint8List(),
        targetWidth: targetPx,
        targetHeight: targetPx,
      );
      final frame = await codec.getNextFrame();
      _logoCache[safeBrand] = frame.image;
      return frame.image;
    } catch (_) {
      return null;
    }
  }

  static Future<BitmapDescriptor> _draw(
    String brand,
    double? price,
    bool hasDiscount,
  ) async {
    final brandColor = BrandColors.of(brand);
    final priceStr = price != null ? _formatPrice(price) : '--';

    // Discount visual: green-tinted pill
    final pillColor   = hasDiscount ? const Color(0xFFECFDF5) : const Color(0xFFFFFFFF);
    final borderColor = hasDiscount ? const Color(0x3010B981) : const Color(0x18000000);
    final tailColor   = pillColor;

    const double pw    = _lw    * _dpr;
    const double ph    = _lh    * _dpr;
    const double tailP = _tailH * _dpr;
    const double imgP  = _imgSz * _dpr;
    const double padP  = _pad   * _dpr;
    const double rP    = ph / 2; // fully rounded ends

    final rrect = RRect.fromLTRBR(0, 0, pw, ph, const Radius.circular(rP));

    final recorder = ui.PictureRecorder();
    final canvas = Canvas(recorder, const Rect.fromLTWH(0, 0, pw, ph + tailP));

    // Drop shadow
    canvas.drawRRect(
      rrect.shift(const Offset(0, 2 * _dpr)),
      Paint()
        ..color = const Color(0x30000000)
        ..maskFilter = const ui.MaskFilter.blur(ui.BlurStyle.normal, 3 * _dpr),
    );

    // Pill background
    canvas.drawRRect(rrect, Paint()..color = pillColor);

    // Pill border
    canvas.drawRRect(
      rrect,
      Paint()
        ..color = borderColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = (hasDiscount ? 1.0 : 0.8) * _dpr,
    );

    // ── Brand logo — vertically centred circle on the left ────────────────
    const double imgY = (ph - imgP) / 2;
    final logo = await _loadLogo(brand);
    if (logo != null) {
      // Clip to circle and draw the image
      canvas.save();
      canvas.clipPath(
        Path()..addOval(const Rect.fromLTWH(padP, imgY, imgP, imgP)),
      );
      canvas.drawImage(logo, const Offset(padP, imgY), Paint());
      canvas.restore();
    } else {
      // Fallback: colored circle
      canvas.drawCircle(
        const Offset(padP + imgP / 2, ph / 2),
        imgP / 2,
        Paint()..color = brandColor,
      );
    }

    // ── Price text — right of logo ────────────────────────────────────────
    final priceColor = hasDiscount ? const Color(0xFF059669) : const Color(0xFF0F172A);
    final textPainter = TextPainter(textDirection: TextDirection.ltr)
      ..text = TextSpan(
        text: priceStr,
        style: TextStyle(
          fontSize: 12.5 * _dpr,
          fontWeight: FontWeight.w700,
          color: priceColor,
        ),
      )
      ..layout();

    const double textAreaLeft = padP + imgP + padP;
    const double textAreaWidth = pw - textAreaLeft - padP;
    final double textX =
        textAreaLeft + ((textAreaWidth - textPainter.width) / 2).clamp(0, textAreaWidth);
    final double textY = (ph - textPainter.height) / 2;
    textPainter.paint(canvas, Offset(textX, textY));

    // ── Tail pointer ──────────────────────────────────────────────────────
    const double cx = pw / 2;
    final tail = Path()
      ..moveTo(cx - 5 * _dpr, ph)
      ..lineTo(cx + 5 * _dpr, ph)
      ..lineTo(cx, ph + tailP)
      ..close();
    canvas.drawPath(tail, Paint()..color = tailColor);

    // Convert to bitmap
    final img = await recorder
        .endRecording()
        .toImage(pw.toInt(), (ph + tailP).toInt());
    final byteData = await img.toByteData(format: ui.ImageByteFormat.png);
    img.dispose();

    return BitmapDescriptor.bytes(
      byteData!.buffer.asUint8List(),
      width: _lw,
      height: _lh + _tailH,
    );
  }
}
