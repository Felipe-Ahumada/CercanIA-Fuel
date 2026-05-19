import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

/// Transparent Scaffold with the standard page gradient background.
/// Wraps [body] in SafeArea automatically.
class GlassScaffold extends StatelessWidget {
  final Widget body;
  final bool useSafeArea;

  const GlassScaffold({
    super.key,
    required this.body,
    this.useSafeArea = true,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        decoration: const BoxDecoration(gradient: GlassTokens.pageGradient),
        child: useSafeArea ? SafeArea(child: body) : body,
      ),
    );
  }
}
