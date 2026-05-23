import 'package:flutter/services.dart';

/// Auto-formats a Chilean RUT as the user types: 12.345.678-9
/// Strips everything except digits and K, then rebuilds the formatted string.
class RutInputFormatter extends TextInputFormatter {
  @override
  TextEditingValue formatEditUpdate(
    TextEditingValue oldValue,
    TextEditingValue newValue,
  ) {
    final clean =
        newValue.text.replaceAll(RegExp(r'[^0-9kK]'), '').toUpperCase();

    if (clean.isEmpty) return const TextEditingValue();

    final String formatted;
    if (clean.length <= 1) {
      formatted = clean;
    } else {
      final verifier = clean[clean.length - 1];
      final body = clean.substring(0, clean.length - 1);
      formatted = '${_dotBody(body)}-$verifier';
    }

    return TextEditingValue(
      text: formatted,
      selection: TextSelection.collapsed(offset: formatted.length),
    );
  }

  static String _dotBody(String body) {
    if (body.length <= 3) return body;
    final chars = body.split('').reversed.toList();
    final result = <String>[];
    for (int i = 0; i < chars.length; i++) {
      if (i > 0 && i % 3 == 0) result.add('.');
      result.add(chars[i]);
    }
    return result.reversed.join();
  }
}

/// Strips RUT formatting for backend submission: "12.345.678-9" → "123456789"
String rutRaw(String formatted) =>
    formatted.trim().replaceAll('.', '').replaceAll('-', '').toUpperCase();
