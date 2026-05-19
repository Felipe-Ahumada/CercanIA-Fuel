import 'package:flutter/material.dart';
import '../theme/glass_tokens.dart';

class GlassLoadingIndicator extends StatelessWidget {
  const GlassLoadingIndicator({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: CircularProgressIndicator(
        valueColor: AlwaysStoppedAnimation<Color>(GlassTokens.green),
      ),
    );
  }
}
