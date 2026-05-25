import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../theme/glass_tokens.dart';

class GlassInput extends StatelessWidget {
  final TextEditingController? controller;
  final String? hintText;
  final Widget? prefixIcon;
  final Widget? suffixIcon;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final ValueChanged<String>? onSubmitted;
  final ValueChanged<String>? onChanged;
  final int? maxLines;
  final bool obscureText;
  final String? errorText;
  final List<TextInputFormatter>? inputFormatters;

  const GlassInput({
    super.key,
    this.controller,
    this.hintText,
    this.prefixIcon,
    this.suffixIcon,
    this.keyboardType,
    this.textInputAction,
    this.onSubmitted,
    this.onChanged,
    this.maxLines = 1,
    this.obscureText = false,
    this.errorText,
    this.inputFormatters,
  });

  @override
  Widget build(BuildContext context) {
    final hasError = errorText != null;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: GlassTokens.blurSigma, sigmaY: GlassTokens.blurSigma),
            child: TextField(
              controller: controller,
              keyboardType: keyboardType,
              textInputAction: textInputAction,
              onSubmitted: onSubmitted,
              onChanged: onChanged,
              maxLines: maxLines,
              obscureText: obscureText,
              inputFormatters: inputFormatters,
              style: const TextStyle(color: GlassTokens.text0),
              decoration: InputDecoration(
                hintText: hintText,
                hintStyle: const TextStyle(color: GlassTokens.text2),
                filled: true,
                fillColor: GlassTokens.glassInput,
                prefixIcon: prefixIcon,
                suffixIcon: suffixIcon,
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 14,
                  vertical: 12,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                  borderSide: BorderSide(
                    color: hasError ? GlassTokens.red : GlassTokens.border2,
                    width: 1.5,
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                  borderSide: BorderSide(
                    color: hasError ? GlassTokens.red : GlassTokens.border2,
                    width: 1.5,
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                  borderSide: BorderSide(
                    color: hasError ? GlassTokens.red : GlassTokens.borderAcc,
                    width: 1.5,
                  ),
                ),
              ),
            ),
          ),
        ),
        if (hasError)
          Padding(
            padding: const EdgeInsets.only(left: 12, top: 4),
            child: Text(
              errorText!,
              style: const TextStyle(color: GlassTokens.red, fontSize: 12),
            ),
          ),
      ],
    );
  }
}
