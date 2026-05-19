import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_input.dart';
import '../../domain/usecases/auth_usecases.dart';
import '../../injection_container.dart';

class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({super.key});

  @override
  State<ForgotPasswordPage> createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final _emailController = TextEditingController();
  bool _loading = false;
  bool _sent = false;
  bool _isLocalUser = false;

  @override
  void initState() {
    super.initState();
    _checkAuthProvider();
  }

  Future<void> _checkAuthProvider() async {
    final prefs = await SharedPreferences.getInstance();
    final provider = prefs.getString('auth_provider');
    if (mounted && provider == 'LOCAL') {
      setState(() => _isLocalUser = true);
    }
  }

  @override
  void dispose() {
    _emailController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    final email = _emailController.text.trim();
    if (email.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Ingresa tu correo electrónico.')),
      );
      return;
    }

    setState(() => _loading = true);
    final result = await sl<ResetPasswordUseCase>()(email);
    if (!mounted) return;
    setState(() => _loading = false);

    result.fold(
      (failure) => ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(failure.message)),
      ),
      (_) => setState(() => _sent = true),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
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
                  child: _isLocalUser
                    ? _LocalAuthContent()
                    : _sent
                        ? _SuccessContent()
                        : _FormContent(
                            controller: _emailController,
                            loading: _loading,
                            onSubmit: _submit,
                          ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _LocalAuthContent extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.info_outline_rounded, size: 48, color: GlassTokens.green),
        const SizedBox(height: 16),
        const Text(
          'Recuperación no disponible',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.w800,
            color: GlassTokens.text0,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        const Text(
          'Tu cuenta usa correo y contraseña. El restablecimiento por enlace solo está disponible para cuentas de Google.\n\nPor favor, contacta a soporte si olvidaste tu contraseña.',
          style: TextStyle(fontSize: 13, color: GlassTokens.text2),
          textAlign: TextAlign.center,
        ),
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

class _FormContent extends StatelessWidget {
  final TextEditingController controller;
  final bool loading;
  final VoidCallback onSubmit;

  const _FormContent({
    required this.controller,
    required this.loading,
    required this.onSubmit,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.lock_reset_rounded,
            size: 48, color: GlassTokens.green),
        const SizedBox(height: 16),
        const Text(
          'Recuperar contraseña',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.w800,
            color: GlassTokens.text0,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        const Text(
          'Te enviaremos un enlace para restablecer tu contraseña.',
          style: TextStyle(fontSize: 13, color: GlassTokens.text2),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 28),
        GlassInput(
          controller: controller,
          hintText: 'Correo electrónico',
          prefixIcon: const Icon(Icons.email_outlined,
              size: 18, color: GlassTokens.text2),
          textInputAction: TextInputAction.done,
          onSubmitted: (_) => onSubmit(),
        ),
        const SizedBox(height: 20),
        GlassButton(
          label: loading ? 'Enviando...' : 'Enviar enlace',
          onPressed: loading ? null : onSubmit,
          width: double.infinity,
        ),
      ],
    );
  }
}

class _SuccessContent extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(Icons.mark_email_read_outlined,
            size: 56, color: GlassTokens.green),
        const SizedBox(height: 16),
        const Text(
          '¡Correo enviado!',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.w800,
            color: GlassTokens.text0,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        const Text(
          'Revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.',
          style: TextStyle(fontSize: 13, color: GlassTokens.text2),
          textAlign: TextAlign.center,
        ),
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
