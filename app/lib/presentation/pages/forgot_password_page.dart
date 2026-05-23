import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_input.dart';
import '../../domain/usecases/auth_usecases.dart';
import '../../injection_container.dart';

enum _LocalStep { email, otp, newPassword, done }

class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({super.key});

  @override
  State<ForgotPasswordPage> createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final _emailCtrl       = TextEditingController();
  final _otpCtrl         = TextEditingController();
  final _passwordCtrl    = TextEditingController();
  final _confirmPassCtrl = TextEditingController();

  bool _loading    = false;
  bool _isLocal    = false;
  bool _sentFirebase = false;
  _LocalStep _step = _LocalStep.email;
  String _submittedEmail = '';

  @override
  void initState() {
    super.initState();
    _detectProvider();
  }

  @override
  void dispose() {
    _emailCtrl.dispose();
    _otpCtrl.dispose();
    _passwordCtrl.dispose();
    _confirmPassCtrl.dispose();
    super.dispose();
  }

  Future<void> _detectProvider() async {
    final prefs = await SharedPreferences.getInstance();
    if (mounted && prefs.getString('auth_provider') == 'LOCAL') {
      setState(() => _isLocal = true);
    }
  }

  // ── Firebase flow ───────────────────────────────────────────────────────────

  Future<void> _submitFirebase() async {
    final email = _emailCtrl.text.trim();
    if (email.isEmpty) {
      _snack('Ingresa tu correo electrónico.');
      return;
    }
    setState(() => _loading = true);
    final result = await sl<ResetPasswordUseCase>()(email);
    if (!mounted) return;
    setState(() => _loading = false);
    result.fold(
      (f) => _snack(f.message),
      (_) => setState(() => _sentFirebase = true),
    );
  }

  // ── LOCAL step 1: request OTP ───────────────────────────────────────────────

  Future<void> _requestOtp() async {
    final email = _emailCtrl.text.trim();
    if (email.isEmpty) { _snack('Ingresa tu correo electrónico.'); return; }
    setState(() => _loading = true);
    final result = await sl<RequestLocalPasswordResetUseCase>()(email);
    if (!mounted) return;
    setState(() => _loading = false);
    result.fold(
      (f) => _snack(f.message),
      (_) {
        _submittedEmail = email;
        setState(() => _step = _LocalStep.otp);
      },
    );
  }

  // ── LOCAL step 2: verify OTP → new password ─────────────────────────────────

  Future<void> _submitReset() async {
    final otp      = _otpCtrl.text.trim();
    final password = _passwordCtrl.text;
    final confirm  = _confirmPassCtrl.text;
    if (otp.length != 6)         { _snack('El código debe tener 6 dígitos.'); return; }
    if (password.length < 8)     { _snack('La contraseña debe tener al menos 8 caracteres.'); return; }
    if (password != confirm)     { _snack('Las contraseñas no coinciden.'); return; }

    setState(() => _loading = true);
    final result = await sl<ConfirmLocalPasswordResetUseCase>()(
      email: _submittedEmail,
      otp: otp,
      newPassword: password,
    );
    if (!mounted) return;
    setState(() => _loading = false);
    result.fold(
      (f) => _snack(f.message),
      (_) => setState(() => _step = _LocalStep.done),
    );
  }

  void _snack(String msg) => ScaffoldMessenger.of(context)
      .showSnackBar(SnackBar(content: Text(msg)));

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: GlassTokens.pageBg,
      body: Container(
        decoration: const BoxDecoration(gradient: GlassTokens.pageGradient),
        child: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                GestureDetector(
                  onTap: () => context.pop(),
                  child: const Icon(Icons.arrow_back_ios_new,
                      size: 20, color: GlassTokens.text0),
                ),
                const SizedBox(height: 40),
                GlassCard(
                  radius: GlassTokens.radiusXl,
                  level: 1,
                  padding: const EdgeInsets.all(28),
                  child: _isLocal ? _localContent() : _firebaseContent(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _firebaseContent() {
    if (_sentFirebase) return _successView();
    return _EmailForm(
      controller: _emailCtrl,
      loading: _loading,
      subtitle: 'Te enviaremos un enlace para restablecer tu contraseña.',
      onSubmit: _submitFirebase,
    );
  }

  Widget _localContent() {
    switch (_step) {
      case _LocalStep.email:
        return _EmailForm(
          controller: _emailCtrl,
          loading: _loading,
          subtitle: 'Te enviaremos un código de 6 dígitos a tu correo.',
          buttonLabel: 'Enviar código',
          onSubmit: _requestOtp,
        );
      case _LocalStep.otp:
        return _OtpForm(
          otpCtrl: _otpCtrl,
          passwordCtrl: _passwordCtrl,
          confirmCtrl: _confirmPassCtrl,
          email: _submittedEmail,
          loading: _loading,
          onSubmit: _submitReset,
          onResend: () { setState(() => _step = _LocalStep.email); },
        );
      case _LocalStep.done:
      case _LocalStep.newPassword:
        return _successView(
          title: '¡Contraseña actualizada!',
          subtitle: 'Tu contraseña ha sido cambiada exitosamente. Ya puedes iniciar sesión.',
        );
    }
  }

  Widget _successView({
    String title = '¡Correo enviado!',
    String subtitle = 'Revisa tu bandeja de entrada y sigue las instrucciones.',
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.mark_email_read_outlined, size: 56, color: GlassTokens.green),
        const SizedBox(height: 16),
        Text(title,
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w800,
                color: GlassTokens.text0),
            textAlign: TextAlign.center),
        const SizedBox(height: 8),
        Text(subtitle,
            style: const TextStyle(fontSize: 13, color: GlassTokens.text2),
            textAlign: TextAlign.center),
        const SizedBox(height: 28),
        GlassButton(
          label: 'Volver al inicio',
          onPressed: () => context.go('/login'),
          width: double.infinity,
        ),
      ],
    );
  }
}

// ── Sub-widgets ────────────────────────────────────────────────────────────────

class _EmailForm extends StatelessWidget {
  final TextEditingController controller;
  final bool loading;
  final String subtitle;
  final String buttonLabel;
  final VoidCallback onSubmit;

  const _EmailForm({
    required this.controller,
    required this.loading,
    required this.subtitle,
    this.buttonLabel = 'Enviar enlace',
    required this.onSubmit,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.lock_reset_rounded, size: 48, color: GlassTokens.green),
        const SizedBox(height: 16),
        const Text('Recuperar contraseña',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.w800,
                color: GlassTokens.text0),
            textAlign: TextAlign.center),
        const SizedBox(height: 8),
        Text(subtitle,
            style: const TextStyle(fontSize: 13, color: GlassTokens.text2),
            textAlign: TextAlign.center),
        const SizedBox(height: 28),
        GlassInput(
          controller: controller,
          hintText: 'Correo electrónico',
          prefixIcon: const Icon(Icons.email_outlined, size: 18, color: GlassTokens.text2),
          keyboardType: TextInputType.emailAddress,
          textInputAction: TextInputAction.done,
          onSubmitted: (_) => onSubmit(),
        ),
        const SizedBox(height: 20),
        GlassButton(
          label: loading ? 'Enviando...' : buttonLabel,
          onPressed: loading ? null : onSubmit,
          width: double.infinity,
        ),
      ],
    );
  }
}

class _OtpForm extends StatelessWidget {
  final TextEditingController otpCtrl;
  final TextEditingController passwordCtrl;
  final TextEditingController confirmCtrl;
  final String email;
  final bool loading;
  final VoidCallback onSubmit;
  final VoidCallback onResend;

  const _OtpForm({
    required this.otpCtrl,
    required this.passwordCtrl,
    required this.confirmCtrl,
    required this.email,
    required this.loading,
    required this.onSubmit,
    required this.onResend,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.pin_outlined, size: 48, color: GlassTokens.green),
        const SizedBox(height: 16),
        const Text('Ingresa el código',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.w800,
                color: GlassTokens.text0),
            textAlign: TextAlign.center),
        const SizedBox(height: 8),
        Text('Enviamos un código de 6 dígitos a $email',
            style: const TextStyle(fontSize: 13, color: GlassTokens.text2),
            textAlign: TextAlign.center),
        const SizedBox(height: 24),
        GlassInput(
          controller: otpCtrl,
          hintText: 'Código de 6 dígitos',
          prefixIcon: const Icon(Icons.pin_outlined, size: 18, color: GlassTokens.text2),
          keyboardType: TextInputType.number,
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),
        GlassInput(
          controller: passwordCtrl,
          hintText: 'Nueva contraseña (mínimo 8 caracteres)',
          prefixIcon: const Icon(Icons.lock_outline, size: 18, color: GlassTokens.text2),
          obscureText: true,
          textInputAction: TextInputAction.next,
        ),
        const SizedBox(height: 16),
        GlassInput(
          controller: confirmCtrl,
          hintText: 'Confirmar contraseña',
          prefixIcon: const Icon(Icons.lock_outline, size: 18, color: GlassTokens.text2),
          obscureText: true,
          textInputAction: TextInputAction.done,
          onSubmitted: (_) => onSubmit(),
        ),
        const SizedBox(height: 24),
        GlassButton(
          label: loading ? 'Verificando...' : 'Cambiar contraseña',
          onPressed: loading ? null : onSubmit,
          width: double.infinity,
        ),
        const SizedBox(height: 12),
        TextButton(
          onPressed: onResend,
          child: const Text('¿No recibiste el código? Reenviar',
              style: TextStyle(fontSize: 12, color: GlassTokens.text2)),
        ),
      ],
    );
  }
}
