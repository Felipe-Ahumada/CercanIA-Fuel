import 'dart:ui';
import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

/// Frosted header for auxiliary pages (push routes outside the shell).
/// Shows a back button, title, optional subtitle, and optional trailing widget.
class GlassPageHeader extends StatelessWidget {
  final String title;
  final String? subtitle;
  final Widget? trailing;
  final VoidCallback? onBack;

  const GlassPageHeader({
    super.key,
    required this.title,
    this.subtitle,
    this.trailing,
    this.onBack,
  });

  @override
  Widget build(BuildContext context) {
    return ClipRect(
      child: BackdropFilter(
        filter: ImageFilter.blur(
            sigmaX: GlassTokens.blurSigmaHeavy,
            sigmaY: GlassTokens.blurSigmaHeavy),
        child: Container(
          color: GlassTokens.headerBg,
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 16),
          child: Row(
            children: [
              GestureDetector(
                onTap: onBack ?? () => Navigator.of(context).pop(),
                child: const Icon(Icons.arrow_back_ios_new,
                    size: 20, color: GlassTokens.text0),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.w800,
                        color: GlassTokens.text0,
                      ),
                    ),
                    if (subtitle != null)
                      Text(
                        subtitle!,
                        style: const TextStyle(
                            fontSize: 12, color: GlassTokens.text2),
                      ),
                  ],
                ),
              ),
              if (trailing != null) trailing!,
            ],
          ),
        ),
      ),
    );
  }
}
