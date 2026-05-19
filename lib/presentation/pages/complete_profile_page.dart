import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/utils/rut_formatter.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../blocs/auth/auth_bloc.dart';

class CompleteProfilePage extends StatefulWidget {
  const CompleteProfilePage({super.key});

  @override
  State<CompleteProfilePage> createState() => _CompleteProfilePageState();
}

class _CompleteProfilePageState extends State<CompleteProfilePage> {
  final _formKey = GlobalKey<FormState>();
  final _rutCtrl = TextEditingController();
  final _secondLastNameCtrl = TextEditingController();
  DateTime? _birthDate;
  bool _submitted = false;
  String? _rutServerError;

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

  @override
  void dispose() {
    _rutCtrl.dispose();
    _secondLastNameCtrl.dispose();
    super.dispose();
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1920),
      lastDate: DateTime.now().subtract(const Duration(days: 365 * 13)),
      helpText: 'Selecciona tu fecha de nacimiento',
    );
    if (picked != null) setState(() => _birthDate = picked);
  }

  void _submit() {
    setState(() => _submitted = true);
    if (!_formKey.currentState!.validate()) return;
    if (_birthDate == null) return;
    context.read<AuthBloc>().add(AuthCompleteProfileRequested(
      rut: rutRaw(_rutCtrl.text),
      secondLastName: _secondLastNameCtrl.text.trim(),
      birthDate: _birthDate!,
    ));
  }

  InputDecoration _glassDecoration(String label) => InputDecoration(
        labelText: label,
        labelStyle: const TextStyle(color: GlassTokens.text2, fontSize: 13),
        filled: true,
        fillColor: GlassTokens.glassInput,
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
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: GlassTokens.pageBg,
      body: Container(
        decoration: const BoxDecoration(gradient: GlassTokens.pageGradient),
        child: SafeArea(
          child: BlocConsumer<AuthBloc, AuthState>(
            listener: (context, state) {
              if (state is AuthNeedsProfileCompletion && state.error != null) {
                setState(() => _rutServerError = state.error);
              }
            },
            builder: (context, state) {
              final isLoading = state is AuthLoading;
              final profileState = state is AuthNeedsProfileCompletion
                  ? state
                  : (state is AuthLoading &&
                          context.read<AuthBloc>().state
                              is AuthNeedsProfileCompletion
                      ? context.read<AuthBloc>().state
                          as AuthNeedsProfileCompletion
                      : null);

              return SingleChildScrollView(
                padding: const EdgeInsets.symmetric(
                    horizontal: 24, vertical: 32),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const SizedBox(height: 16),
                    GlassCard(
                      radius: GlassTokens.radiusXl,
                      level: 1,
                      padding: const EdgeInsets.all(28),
                      child: Form(
                        key: _formKey,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            const Icon(Icons.person_add_rounded,
                                size: 48, color: GlassTokens.green),
                            const SizedBox(height: 16),
                            Text(
                              'Hola${profileState != null && profileState.firstName.isNotEmpty ? ", ${profileState.firstName}" : ""}!',
                              style: const TextStyle(
                                fontSize: 22,
                                fontWeight: FontWeight.w800,
                                color: GlassTokens.text0,
                              ),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 8),
                            const Text(
                              'Necesitamos algunos datos adicionales para completar tu registro.',
                              style: TextStyle(
                                  fontSize: 13, color: GlassTokens.text2),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 28),
                            TextFormField(
                              controller: _secondLastNameCtrl,
                              style: const TextStyle(color: GlassTokens.text0),
                              decoration:
                                  _glassDecoration('Apellido materno'),
                              textCapitalization: TextCapitalization.words,
                              validator: (v) =>
                                  (v == null || v.trim().isEmpty)
                                      ? 'Ingresa tu apellido materno'
                                      : null,
                            ),
                            const SizedBox(height: 12),
                            TextFormField(
                              controller: _rutCtrl,
                              style: const TextStyle(color: GlassTokens.text0),
                              keyboardType: TextInputType.text,
                              inputFormatters: [RutInputFormatter()],
                              onChanged: (_) =>
                                  setState(() => _rutServerError = null),
                              decoration: _glassDecoration('RUT').copyWith(
                                hintText: 'ej: 12.345.678-9',
                                hintStyle: const TextStyle(
                                    color: GlassTokens.text2),
                                errorText: _rutServerError,
                              ),
                              validator: _validateRut,
                            ),
                            const SizedBox(height: 12),
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
                                        color: (_submitted && _birthDate == null)
                                            ? GlassTokens.red
                                            : GlassTokens.border2,
                                        width: 1.5,
                                      ),
                                    ),
                                    child: Row(
                                      children: [
                                        Icon(Icons.calendar_today,
                                            size: 16,
                                            color: (_submitted && _birthDate == null)
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
                                                : (_submitted
                                                    ? GlassTokens.red
                                                    : GlassTokens.text2),
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                  if (_submitted && _birthDate == null)
                                    const Padding(
                                      padding: EdgeInsets.only(left: 12, top: 4),
                                      child: Text(
                                        'Selecciona tu fecha de nacimiento',
                                        style: TextStyle(
                                            color: GlassTokens.red, fontSize: 12),
                                      ),
                                    ),
                                ],
                              ),
                            ),
                            const SizedBox(height: 28),
                            GlassButton(
                              label: isLoading
                                  ? 'Guardando...'
                                  : 'Completar registro',
                              onPressed: isLoading ? null : _submit,
                              width: double.infinity,
                            ),
                          ],
                        ),
                      ),
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
