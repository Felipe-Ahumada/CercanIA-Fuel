import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_button.dart' show GlassButtonSecondary;
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_input.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_page_header.dart';
import '../../core/widgets/glass_scaffold.dart';
import '../blocs/profile/profile_cubit.dart';
import '../blocs/profile/profile_state.dart';

String _formatRut(String raw) {
  final clean = raw.replaceAll('.', '').replaceAll('-', '').toUpperCase();
  if (clean.length < 2) return raw;
  final dv = clean[clean.length - 1];
  final digits = clean.substring(0, clean.length - 1);
  final buf = StringBuffer();
  for (var i = 0; i < digits.length; i++) {
    if (i > 0 && (digits.length - i) % 3 == 0) buf.write('.');
    buf.write(digits[i]);
  }
  return '${buf.toString()}-$dv';
}

class EditProfilePage extends StatefulWidget {
  const EditProfilePage({super.key});

  @override
  State<EditProfilePage> createState() => _EditProfilePageState();
}

class _EditProfilePageState extends State<EditProfilePage> {
  final _firstNameCtrl      = TextEditingController();
  final _middleNameCtrl     = TextEditingController();
  final _lastNameCtrl       = TextEditingController();
  final _secondLastNameCtrl = TextEditingController();
  bool _initialized = false;
  bool _saving = false;

  @override
  void dispose() {
    _firstNameCtrl.dispose();
    _middleNameCtrl.dispose();
    _lastNameCtrl.dispose();
    _secondLastNameCtrl.dispose();
    super.dispose();
  }

  void _initFromState(ProfileState state) {
    if (_initialized || state is! ProfileLoaded) return;
    _initialized = true;
    final user = state.user;
    _firstNameCtrl.text      = user.firstName ?? '';
    _middleNameCtrl.text     = user.middleName ?? '';
    _lastNameCtrl.text       = user.lastName ?? '';
    _secondLastNameCtrl.text = user.secondLastName ?? '';
  }

  void _save(BuildContext context) {
    final firstName      = _firstNameCtrl.text.trim();
    final middleName     = _middleNameCtrl.text.trim();
    final lastName       = _lastNameCtrl.text.trim();
    final secondLastName = _secondLastNameCtrl.text.trim();
    if (firstName.isEmpty || lastName.isEmpty) return;
    _saving = true;
    context.read<ProfileCubit>().updateProfile(
      firstName: firstName,
      middleName: middleName.isNotEmpty ? middleName : null,
      lastName: lastName,
      secondLastName: secondLastName.isNotEmpty ? secondLastName : null,
    );
  }

  @override
  Widget build(BuildContext context) {
    return GlassScaffold(
      useSafeArea: false,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SafeArea(
            bottom: false,
            child: GlassPageHeader(
              title: 'Editar Perfil',
              subtitle: 'Actualiza tu nombre',
              onBack: () => context.pop(),
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: BlocConsumer<ProfileCubit, ProfileState>(
              listener: (context, state) {
                _initFromState(state);
                if (state is ProfileLoaded && _saving) {
                  _saving = false;
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Perfil actualizado'),
                      backgroundColor: GlassTokens.green,
                    ),
                  );
                  context.pop();
                }
                if (state is ProfileError) {
                  _saving = false;
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(state.message)),
                  );
                }
              },
              builder: (context, state) {
                _initFromState(state);

                if (state is ProfileLoading || state is ProfileInitial) {
                  return const GlassLoadingIndicator();
                }

                final user = state is ProfileLoaded ? state.user : null;

                return SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      // ── Read-only account info ───────────────────────────
                      GlassCard(
                        radius: 16,
                        level: 1,
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('INFORMACIÓN DE CUENTA',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 12),
                            if (user?.email != null) ...[
                              _InfoRow(
                                icon: Icons.email_outlined,
                                label: 'Correo electrónico',
                                value: user!.email,
                              ),
                              const _RowDivider(),
                            ],
                            if (user?.rut != null) ...[
                              _InfoRow(
                                icon: Icons.badge_outlined,
                                label: 'RUT',
                                value: _formatRut(user!.rut!),
                              ),
                              const _RowDivider(),
                            ],
                            if (user?.birthDate != null)
                              _InfoRow(
                                icon: Icons.cake_outlined,
                                label: 'Fecha de nacimiento',
                                value: DateFormat(
                                  "d 'de' MMMM 'de' y",
                                  'es_CL',
                                ).format(user!.birthDate!),
                              ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 24),

                      // ── Editable name fields ─────────────────────────────
                      const Text('PRIMER NOMBRE',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      GlassInput(
                        controller: _firstNameCtrl,
                        hintText: 'Ingresa tu primer nombre',
                        keyboardType: TextInputType.name,
                      ),
                      const SizedBox(height: 20),

                      const Text('SEGUNDO NOMBRE  •  opcional',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      GlassInput(
                        controller: _middleNameCtrl,
                        hintText: 'Ingresa tu segundo nombre',
                        keyboardType: TextInputType.name,
                      ),
                      const SizedBox(height: 20),

                      const Text('PRIMER APELLIDO',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      GlassInput(
                        controller: _lastNameCtrl,
                        hintText: 'Ingresa tu primer apellido',
                        keyboardType: TextInputType.name,
                      ),
                      const SizedBox(height: 20),

                      const Text('SEGUNDO APELLIDO  •  opcional',
                          style: GlassTokens.sectionLabelStyle),
                      const SizedBox(height: 8),
                      GlassInput(
                        controller: _secondLastNameCtrl,
                        hintText: 'Ingresa tu segundo apellido',
                        keyboardType: TextInputType.name,
                      ),
                      const SizedBox(height: 32),

                      GlassButton(
                        label: 'Guardar cambios',
                        width: double.infinity,
                        onPressed: () => _save(context),
                      ),
                      if (user?.authProvider == 'LOCAL') ...[
                        const SizedBox(height: 12),
                        GlassButtonSecondary(
                          label: 'Cambiar contraseña',
                          width: double.infinity,
                          icon: Icons.lock_outline,
                          onPressed: () => context.push('/change_password'),
                        ),
                      ],
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _InfoRow({
    required this.icon,
    required this.label,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: Row(
        children: [
          Icon(icon, size: 18, color: GlassTokens.text2),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: const TextStyle(
                    fontSize: 11,
                    color: GlassTokens.text2,
                    fontWeight: FontWeight.w500,
                    letterSpacing: 0.3,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  value,
                  style: const TextStyle(
                    fontSize: 14,
                    color: GlassTokens.text0,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.lock_outline, size: 14, color: GlassTokens.text3),
        ],
      ),
    );
  }
}

class _RowDivider extends StatelessWidget {
  const _RowDivider();

  @override
  Widget build(BuildContext context) {
    return const Divider(
      height: 1,
      thickness: 0.5,
      color: GlassTokens.border1,
    );
  }
}
