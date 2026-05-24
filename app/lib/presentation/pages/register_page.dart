import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../blocs/auth/auth_bloc.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/utils/rut_formatter.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_loading_indicator.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _formKey = GlobalKey<FormState>();
  final _firstNameCtrl       = TextEditingController();
  final _middleNameCtrl      = TextEditingController();
  final _lastNameCtrl        = TextEditingController();
  final _secondLastNameCtrl  = TextEditingController();
  final _rutCtrl             = TextEditingController();
  final _emailCtrl           = TextEditingController();
  final _passwordCtrl        = TextEditingController();
  final _confirmPasswordCtrl = TextEditingController();

  DateTime? _birthDate;
  bool _obscurePassword        = true;
  bool _obscureConfirmPassword = true;
  bool _submitted              = false;
  String? _rutServerError;
  String? _emailServerError;
  String? _birthDateError;

  static final _emailRegex = RegExp(r'^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$');
  static const int _minAgeChile = 18;

  @override
  void dispose() {
    _firstNameCtrl.dispose();
    _middleNameCtrl.dispose();
    _lastNameCtrl.dispose();
    _secondLastNameCtrl.dispose();
    _rutCtrl.dispose();
    _emailCtrl.dispose();
    _passwordCtrl.dispose();
    _confirmPasswordCtrl.dispose();
    super.dispose();
  }

  // ── Validación RUT chileno (mod-11) ────────────────────────────────────────
  static String? _validateRut(String? value) {
    if (value == null || value.trim().isEmpty) return 'Ingresa tu RUT';
    final clean = value.trim().replaceAll('.', '').replaceAll('-', '').toUpperCase();
    if (clean.length < 2) return 'RUT inválido';
    final body = clean.substring(0, clean.length - 1);
    final dv   = clean[clean.length - 1];
    if (!RegExp(r'^\d+$').hasMatch(body)) return 'RUT inválido (solo números antes del guion)';

    int sum = 0, mult = 2;
    for (int i = body.length - 1; i >= 0; i--) {
      sum  += int.parse(body[i]) * mult;
      mult  = mult == 7 ? 2 : mult + 1;
    }
    final rem = 11 - (sum % 11);
    final computed = rem == 11 ? '0' : rem == 10 ? 'K' : rem.toString();
    if (dv != computed) return 'Dígito verificador incorrecto';
    return null;
  }

  static bool _isAdultChile(DateTime birthDate) {
    final now = DateTime.now();
    final adultDate = DateTime(now.year - _minAgeChile, now.month, now.day);
    return !birthDate.isAfter(adultDate);
  }

  String? _validateBirthDate(DateTime? birthDate) {
    if (birthDate == null) return null;
    if (!_isAdultChile(birthDate)) return 'Debes tener al menos 18 años.';
    return null;
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final maxDate = DateTime(now.year - _minAgeChile, now.month, now.day);
    final defaultDate = DateTime(2000);
    final picked = await showDatePicker(
      context: context,
      initialDate: defaultDate.isAfter(maxDate) ? maxDate : defaultDate,
      firstDate: DateTime(1920),
      lastDate: maxDate,
      helpText: 'Selecciona tu fecha de nacimiento',
    );
    if (picked != null) {
      setState(() {
        _birthDate = picked;
        _birthDateError = _validateBirthDate(picked);
      });
    }
  }

  void _onSignUp() {
    setState(() {
      _submitted = true;
      _birthDateError = _validateBirthDate(_birthDate);
    });
    if (!_formKey.currentState!.validate()) return;
    if (_birthDateError != null) return;
    if (_birthDate == null) return;

    final mid = _middleNameCtrl.text.trim();
    context.read<AuthBloc>().add(AuthSignUpRequested(
      email:          _emailCtrl.text.trim(),
      password:       _passwordCtrl.text,
      firstName:      _firstNameCtrl.text.trim(),
      middleName:     mid.isEmpty ? null : mid,
      lastName:       _lastNameCtrl.text.trim(),
      secondLastName: _secondLastNameCtrl.text.trim(),
      rut:            rutRaw(_rutCtrl.text),
      birthDate:      _birthDate!,
    ));
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
                if (state.message == 'rut_taken') {
                  setState(() {
                    _rutServerError = 'Este RUT ya está registrado.';
                    _emailServerError = null;
                  });
                } else if (state.message == 'email_taken') {
                  setState(() {
                    _emailServerError = 'Este correo ya está registrado.';
                    _rutServerError = null;
                  });
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(state.message)),
                  );
                }
              }
            },
            builder: (context, state) {
              if (state is AuthLoading) return const GlassLoadingIndicator();

              return SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(24, 24, 24, 40),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      // Header
                      Row(
                        children: [
                          GestureDetector(
                            onTap: () => Navigator.pop(context),
                            child: const Icon(Icons.arrow_back_ios_new,
                                size: 20, color: GlassTokens.text0),
                          ),
                          const SizedBox(width: 12),
                          const Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('Crear cuenta',
                                  style: TextStyle(
                                      fontSize: 22,
                                      fontWeight: FontWeight.w900,
                                      color: GlassTokens.text0)),
                              Text('Únete a CercanIA Fuel',
                                  style: TextStyle(
                                      fontSize: 12, color: GlassTokens.text2)),
                            ],
                          ),
                        ],
                      ),
                      const SizedBox(height: 28),

                      // ── Datos personales ───────────────────────────────────
                      GlassCard(
                        radius: GlassTokens.radiusXl,
                        level: 2,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            const Text('DATOS PERSONALES',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 16),
                            _GlassFormField(
                              controller: _firstNameCtrl,
                              hintText: 'Primer nombre',
                              prefixIcon: Icons.person_outline,
                              validator: (v) => (v == null || v.trim().isEmpty)
                                  ? 'Ingresa tu primer nombre'
                                  : null,
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _middleNameCtrl,
                              hintText: 'Segundo nombre (opcional)',
                              prefixIcon: Icons.person_outline,
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _lastNameCtrl,
                              hintText: 'Apellido paterno',
                              prefixIcon: Icons.person_outline,
                              validator: (v) => (v == null || v.trim().isEmpty)
                                  ? 'Ingresa tu apellido paterno'
                                  : null,
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _secondLastNameCtrl,
                              hintText: 'Apellido materno',
                              prefixIcon: Icons.person_outline,
                              validator: (v) => (v == null || v.trim().isEmpty)
                                  ? 'Ingresa tu apellido materno'
                                  : null,
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _rutCtrl,
                              hintText: 'RUT (ej: 12.345.678-9)',
                              prefixIcon: Icons.badge_outlined,
                              keyboardType: TextInputType.text,
                              inputFormatters: [RutInputFormatter()],
                              errorText: _rutServerError,
                              onChanged: (_) => setState(() => _rutServerError = null),
                              validator: _validateRut,
                            ),
                            const SizedBox(height: 12),

                            // Date picker con estado de error
                            GestureDetector(
                              onTap: _pickDate,
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: 14, vertical: 14),
                                    decoration: BoxDecoration(
                                      color: GlassTokens.glassInput,
                                      borderRadius: BorderRadius.circular(
                                          GlassTokens.radiusMd),
                                      border: Border.all(
                                        color: ((_submitted && _birthDate == null) || _birthDateError != null)
                                            ? GlassTokens.red
                                            : GlassTokens.border2,
                                        width: 1.5,
                                      ),
                                    ),
                                    child: Row(
                                      children: [
                                        Icon(Icons.calendar_today,
                                            size: 18,
                                            color: ((_submitted && _birthDate == null) || _birthDateError != null)
                                                ? GlassTokens.red
                                                : GlassTokens.text2),
                                        const SizedBox(width: 10),
                                        Text(
                                          _birthDate != null
                                              ? DateFormat('dd/MM/yyyy')
                                                  .format(_birthDate!)
                                              : 'Fecha de nacimiento',
                                          style: TextStyle(
                                            fontSize: 14,
                                            color: _birthDate != null
                                                ? GlassTokens.text0
                                                : ((_submitted || _birthDateError != null)
                                                    ? GlassTokens.red
                                                    : GlassTokens.text2),
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                  if (_birthDateError != null)
                                    Padding(
                                      padding: const EdgeInsets.only(left: 12, top: 4),
                                      child: Text(
                                        _birthDateError!,
                                        style: const TextStyle(
                                            color: GlassTokens.red,
                                            fontSize: 12),
                                      ),
                                    )
                                  else if (_submitted && _birthDate == null)
                                    const Padding(
                                      padding: EdgeInsets.only(left: 12, top: 4),
                                      child: Text(
                                        'Selecciona tu fecha de nacimiento',
                                        style: TextStyle(
                                            color: GlassTokens.red,
                                            fontSize: 12),
                                      ),
                                    ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 16),

                      // ── Credenciales ───────────────────────────────────────
                      GlassCard(
                        radius: GlassTokens.radiusXl,
                        level: 2,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            const Text('CREDENCIALES',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 16),
                            _GlassFormField(
                              controller: _emailCtrl,
                              hintText: 'Correo electrónico',
                              prefixIcon: Icons.email_outlined,
                              keyboardType: TextInputType.emailAddress,
                              errorText: _emailServerError,
                              onChanged: (_) => setState(() => _emailServerError = null),
                              validator: (v) {
                                if (v == null || v.trim().isEmpty) {
                                  return 'Ingresa tu correo';
                                }
                                if (!_emailRegex.hasMatch(v.trim())) {
                                  return 'Correo electrónico inválido';
                                }
                                return null;
                              },
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _passwordCtrl,
                              hintText: 'Contraseña (mínimo 8 caracteres)',
                              prefixIcon: Icons.lock_outline,
                              obscureText: _obscurePassword,
                              suffixIcon: GestureDetector(
                                onTap: () => setState(
                                    () => _obscurePassword = !_obscurePassword),
                                child: Icon(
                                  _obscurePassword
                                      ? Icons.visibility_off
                                      : Icons.visibility,
                                  size: 18,
                                  color: GlassTokens.text2,
                                ),
                              ),
                              validator: (v) {
                                if (v == null || v.isEmpty) {
                                  return 'Ingresa una contraseña';
                                }
                                if (v.length < 8) {
                                  return 'La contraseña debe tener al menos 8 caracteres';
                                }
                                if (v.length > 100) {
                                  return 'La contraseña es demasiado larga';
                                }
                                return null;
                              },
                            ),
                            const SizedBox(height: 12),
                            _GlassFormField(
                              controller: _confirmPasswordCtrl,
                              hintText: 'Confirmar contraseña',
                              prefixIcon: Icons.lock_outline,
                              obscureText: _obscureConfirmPassword,
                              suffixIcon: GestureDetector(
                                onTap: () => setState(() =>
                                    _obscureConfirmPassword =
                                        !_obscureConfirmPassword),
                                child: Icon(
                                  _obscureConfirmPassword
                                      ? Icons.visibility_off
                                      : Icons.visibility,
                                  size: 18,
                                  color: GlassTokens.text2,
                                ),
                              ),
                              validator: (v) {
                                if (v == null || v.isEmpty) {
                                  return 'Confirma tu contraseña';
                                }
                                if (v != _passwordCtrl.text) {
                                  return 'Las contraseñas no coinciden';
                                }
                                return null;
                              },
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 24),

                      GlassButton(
                        label: 'Crear cuenta',
                        width: double.infinity,
                        onPressed: _onSignUp,
                      ),
                      const SizedBox(height: 16),

                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Text('¿Ya tienes cuenta? ',
                              style: TextStyle(
                                  fontSize: 13, color: GlassTokens.text2)),
                          GestureDetector(
                            onTap: () => Navigator.pop(context),
                            child: const Text('Iniciar sesión',
                                style: TextStyle(
                                    fontSize: 13,
                                    fontWeight: FontWeight.w700,
                                    color: GlassTokens.green)),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}

class _GlassFormField extends StatelessWidget {
  final TextEditingController controller;
  final String hintText;
  final IconData prefixIcon;
  final Widget? suffixIcon;
  final bool obscureText;
  final TextInputType? keyboardType;
  final List<TextInputFormatter>? inputFormatters;
  final String? errorText;
  final ValueChanged<String>? onChanged;
  final String? Function(String?)? validator;

  const _GlassFormField({
    required this.controller,
    required this.hintText,
    required this.prefixIcon,
    this.suffixIcon,
    this.obscureText = false,
    this.keyboardType,
    this.inputFormatters,
    this.errorText,
    this.onChanged,
    this.validator,
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      obscureText: obscureText,
      keyboardType: keyboardType,
      inputFormatters: inputFormatters,
      onChanged: onChanged,
      validator: validator,
      style: const TextStyle(color: GlassTokens.text0),
      decoration: InputDecoration(
        hintText: hintText,
        errorText: errorText,
        hintStyle: const TextStyle(color: GlassTokens.text2),
        filled: true,
        fillColor: GlassTokens.glassInput,
        prefixIcon: Icon(prefixIcon, size: 18, color: GlassTokens.text2),
        suffixIcon: suffixIcon,
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          borderSide: const BorderSide(color: GlassTokens.border2, width: 1.5),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          borderSide: const BorderSide(color: GlassTokens.border2, width: 1.5),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          borderSide:
              const BorderSide(color: GlassTokens.borderAcc, width: 1.5),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          borderSide: const BorderSide(color: GlassTokens.red, width: 1.5),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(GlassTokens.radiusMd),
          borderSide: const BorderSide(color: GlassTokens.red, width: 1.5),
        ),
      ),
    );
  }
}
