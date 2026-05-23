import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../blocs/auth/auth_bloc.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_input.dart';
import '../../core/widgets/glass_loading_indicator.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  String? _emailError;
  String? _passwordError;

  static final _emailRegex = RegExp(r'^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$');

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _onSignIn() {
    final email = _emailController.text.trim();
    final password = _passwordController.text;

    String? emailErr;
    String? passErr;

    if (email.isEmpty) {
      emailErr = 'Ingresa tu correo';
    } else if (!_emailRegex.hasMatch(email)) {
      emailErr = 'Correo electrónico inválido';
    }

    if (password.isEmpty) {
      passErr = 'Ingresa tu contraseña';
    }

    setState(() {
      _emailError = emailErr;
      _passwordError = passErr;
    });

    if (emailErr != null || passErr != null) return;
    context.read<AuthBloc>().add(AuthSignInRequested(email, password));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: GlassTokens.pageBg,
      body: Container(
        decoration: const BoxDecoration(gradient: GlassTokens.pageGradient),
        child: SafeArea(
          child: BlocConsumer<AuthBloc, AuthState>(
            listener: (context, state) {
              if (state is AuthError) {
                ScaffoldMessenger.of(context)
                    .showSnackBar(SnackBar(content: Text(state.message)));
              } else if (state is AuthAuthenticated) {
                context.go('/home/map');
              }
            },
            builder: (context, state) {
              if (state is AuthLoading) {
                return const GlassLoadingIndicator();
              }

              return SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(24, 48, 24, 24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    // Logo / brand header
                    const SizedBox(height: 32),
                    Container(
                      width: 72,
                      height: 72,
                      decoration: const BoxDecoration(
                        gradient: GlassTokens.accentGradient,
                        shape: BoxShape.circle,
                      ),
                      alignment: Alignment.center,
                      child: const Icon(Icons.local_gas_station,
                          color: Colors.white, size: 36),
                    ),
                    const SizedBox(height: 20),
                    const Text(
                      'CercanIA Fuel',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.w900,
                        letterSpacing: -0.5,
                        color: GlassTokens.text0,
                      ),
                    ),
                    const SizedBox(height: 6),
                    const Text(
                      'Encuentra las mejores ofertas de combustible',
                      textAlign: TextAlign.center,
                      style: TextStyle(fontSize: 13, color: GlassTokens.text2),
                    ),
                    const SizedBox(height: 40),

                    // Form card
                    GlassCard(
                      radius: GlassTokens.radiusXl,
                      level: 2,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          const Text(
                            'INICIAR SESIÓN',
                            style: GlassTokens.sectionLabelStyle,
                          ),
                          const SizedBox(height: 16),
                          GlassInput(
                            controller: _emailController,
                            hintText: 'Correo electrónico',
                            prefixIcon: const Icon(Icons.email_outlined,
                                size: 18, color: GlassTokens.text2),
                            textInputAction: TextInputAction.next,
                            errorText: _emailError,
                            onChanged: (_) {
                              if (_emailError != null) {
                                setState(() => _emailError = null);
                              }
                            },
                          ),
                          const SizedBox(height: 12),
                          GlassInput(
                            controller: _passwordController,
                            hintText: 'Contraseña',
                            prefixIcon: const Icon(Icons.lock_outline,
                                size: 18, color: GlassTokens.text2),
                            obscureText: true,
                            textInputAction: TextInputAction.done,
                            onSubmitted: (_) => _onSignIn(),
                            errorText: _passwordError,
                            onChanged: (_) {
                              if (_passwordError != null) {
                                setState(() => _passwordError = null);
                              }
                            },
                          ),
                          const SizedBox(height: 6),
                          Align(
                            alignment: Alignment.centerRight,
                            child: GestureDetector(
                              onTap: () => context.push('/forgot_password'),
                              child: const Text(
                                '¿Olvidaste tu contraseña?',
                                style: TextStyle(
                                  fontSize: 12,
                                  color: GlassTokens.green,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 20),
                          GlassButton(
                            label: 'Ingresar',
                            width: double.infinity,
                            onPressed: _onSignIn,
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 16),

                    // Google sign-in
                    ClipRRect(
                      borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
                      child: BackdropFilter(
                        filter: ImageFilter.blur(
                            sigmaX: GlassTokens.blurSigma,
                            sigmaY: GlassTokens.blurSigma),
                        child: GestureDetector(
                          onTap: () => context
                              .read<AuthBloc>()
                              .add(AuthGoogleSignInRequested()),
                          child: Container(
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            decoration: BoxDecoration(
                              color: GlassTokens.glass2,
                              borderRadius:
                                  BorderRadius.circular(GlassTokens.radiusMd),
                              border: Border.all(color: GlassTokens.border2),
                            ),
                            child: const Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Icon(Icons.g_mobiledata,
                                    size: 24, color: GlassTokens.text0),
                                SizedBox(width: 8),
                                Text(
                                  'Continuar con Google',
                                  style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w700,
                                    color: GlassTokens.text0,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),

                    // Register link
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text(
                          '¿No tienes cuenta? ',
                          style: TextStyle(
                              fontSize: 13, color: GlassTokens.text2),
                        ),
                        GestureDetector(
                          onTap: () => context.push('/register'),
                          child: const Text(
                            'Registrarse',
                            style: TextStyle(
                              fontSize: 13,
                              fontWeight: FontWeight.w700,
                              color: GlassTokens.green,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}
