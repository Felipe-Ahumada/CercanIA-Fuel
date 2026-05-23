import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../theme/glass_tokens.dart';

class GlassButton extends StatefulWidget {
  final String label;
  final VoidCallback? onPressed;
  final double? width;

  const GlassButton({
    super.key,
    required this.label,
    this.onPressed,
    this.width,
  });

  @override
  State<GlassButton> createState() => _GlassButtonState();
}

class _GlassButtonState extends State<GlassButton> {
  bool _pressed = false;

  void _onTapDown(TapDownDetails _) {
    if (widget.onPressed == null) return;
    HapticFeedback.lightImpact();
    setState(() => _pressed = true);
  }

  void _onTapUp(TapUpDetails _) => setState(() => _pressed = false);
  void _onTapCancel() => setState(() => _pressed = false);

  @override
  Widget build(BuildContext context) {
    final enabled = widget.onPressed != null;

    return GestureDetector(
      onTap: widget.onPressed,
      onTapDown: _onTapDown,
      onTapUp: _onTapUp,
      onTapCancel: _onTapCancel,
      child: AnimatedScale(
        scale: _pressed ? 0.97 : 1.0,
        duration: const Duration(milliseconds: 80),
        child: AnimatedOpacity(
          opacity: _pressed ? 0.88 : 1.0,
          duration: const Duration(milliseconds: 80),
          child: Container(
            width: widget.width,
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
            decoration: BoxDecoration(
              gradient: enabled
                  ? (_pressed
                      ? const LinearGradient(
                          begin: Alignment.topLeft,
                          end: Alignment.bottomRight,
                          colors: [GlassTokens.greenDark, Color(0xFF065F46)])
                      : GlassTokens.accentGradient)
                  : null,
              color: enabled ? null : GlassTokens.glass1,
              borderRadius: BorderRadius.circular(16),
              boxShadow: enabled && !_pressed
                  ? const [
                      BoxShadow(
                        color: GlassTokens.greenGlow,
                        blurRadius: 20,
                        offset: Offset(0, 4),
                      ),
                    ]
                  : null,
            ),
            alignment: Alignment.center,
            child: Text(
              widget.label,
              style: TextStyle(
                color: enabled ? GlassTokens.onAccent : GlassTokens.text2,
                fontWeight: FontWeight.w700,
                fontSize: 15,
              ),
            ),
          ),
        ),
      ),
    );
  }
}

/// Botón de acción secundaria (ghost/outline) — para CTAs de menor prioridad.
class GlassButtonSecondary extends StatefulWidget {
  final String label;
  final IconData? icon;
  final VoidCallback? onPressed;
  final double? width;

  const GlassButtonSecondary({
    super.key,
    required this.label,
    this.icon,
    this.onPressed,
    this.width,
  });

  @override
  State<GlassButtonSecondary> createState() => _GlassButtonSecondaryState();
}

class _GlassButtonSecondaryState extends State<GlassButtonSecondary> {
  bool _pressed = false;

  @override
  Widget build(BuildContext context) {
    final enabled = widget.onPressed != null;
    return GestureDetector(
      onTap: widget.onPressed,
      onTapDown: enabled ? (_) {
        HapticFeedback.lightImpact();
        setState(() => _pressed = true);
      } : null,
      onTapUp: enabled ? (_) => setState(() => _pressed = false) : null,
      onTapCancel: enabled ? () => setState(() => _pressed = false) : null,
      child: AnimatedOpacity(
        opacity: _pressed ? 0.7 : 1.0,
        duration: const Duration(milliseconds: 80),
        child: Container(
          width: widget.width,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 13),
          decoration: BoxDecoration(
            color: _pressed
                ? GlassTokens.glass1
                : Colors.transparent,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(
              color: enabled ? GlassTokens.border2 : GlassTokens.text3,
              width: 1.5,
            ),
          ),
          alignment: Alignment.center,
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (widget.icon != null) ...[
                Icon(widget.icon, size: 16, color: enabled ? GlassTokens.text1 : GlassTokens.text3),
                const SizedBox(width: 8),
              ],
              Text(
                widget.label,
                style: TextStyle(
                  color: enabled ? GlassTokens.text1 : GlassTokens.text3,
                  fontWeight: FontWeight.w600,
                  fontSize: 14,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
