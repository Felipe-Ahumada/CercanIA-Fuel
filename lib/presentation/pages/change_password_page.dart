import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_input.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_page_header.dart';
import '../../core/widgets/glass_scaffold.dart';
import '../../domain/usecases/auth_usecases.dart';
import '../../injection_container.dart';

class ChangePasswordPage extends StatefulWidget {
  const ChangePasswordPage({super.key});

  @override
  State<ChangePasswordPage> createState() => _ChangePasswordPageState();
}

class _ChangePasswordPageState extends State<ChangePasswordPage> {
  final _currentCtrl = TextEditingController();
  final _newCtrl     = TextEditingController();
  final _confirmCtrl = TextEditingController();

  bool _loading      = false;
  bool _isLocal      = false;
  bool _checked      = false;

  @override
  void initState() {
    super.initState();
    _detectProvider();
  }

  @override
  void dispose() {
    _currentCtrl.dispose();
    _newCtrl.dispose();
    _confirmCtrl.dispose();
    super.dispose();
  }

  Future<void> _detectProvider() async {
    final prefs = await SharedPreferences.getInstance();
    if (mounted) {
      setState(() {
        _isLocal = prefs.getString('auth_provider') == 'LOCAL';
        _checked = true;
      });
    }
  }

  Future<void> _submit() async {
    final current = _currentCtrl.text;
    final next    = _newCtrl.text;
    final confirm = _confirmCtrl.text;

    if (current.isEmpty || next.isEmpty) {
      _snack('Completa todos los campos.');
      return;
    }
    if (next.length < 8) {
      _snack('La nueva contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (next != confirm) {
      _snack('Las contraseñas no coinciden.');
      return;
    }

    setState(() => _loading = true);
    final result = await sl<ChangePasswordUseCase>()(
      currentPassword: current,
      newPassword: next,
    );
    if (!mounted) return;
    setState(() => _loading = false);

    result.fold(
      (f) => _snack(f.message),
      (_) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Contraseña actualizada exitosamente'),
            backgroundColor: GlassTokens.green,
          ),
        );
        context.pop();
      },
    );
  }

  void _snack(String msg) => ScaffoldMessenger.of(context)
      .showSnackBar(SnackBar(content: Text(msg)));

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
              title: 'Cambiar contraseña',
              subtitle: 'Solo disponible para cuentas locales',
              onBack: () => context.pop(),
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: !_checked
                ? const GlassLoadingIndicator()
                : !_isLocal
                    ? _NotAvailableView()
                    : SingleChildScrollView(
                        padding: const EdgeInsets.symmetric(horizontal: 20),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            const Text('CONTRASEÑA ACTUAL',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 8),
                            GlassInput(
                              controller: _currentCtrl,
                              hintText: 'Ingresa tu contraseña actual',
                              obscureText: true,
                              textInputAction: TextInputAction.next,
                            ),
                            const SizedBox(height: 20),
                            const Text('NUEVA CONTRASEÑA',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 8),
                            GlassInput(
                              controller: _newCtrl,
                              hintText: 'Mínimo 8 caracteres',
                              obscureText: true,
                              textInputAction: TextInputAction.next,
                            ),
                            const SizedBox(height: 20),
                            const Text('CONFIRMAR CONTRASEÑA',
                                style: GlassTokens.sectionLabelStyle),
                            const SizedBox(height: 8),
                            GlassInput(
                              controller: _confirmCtrl,
                              hintText: 'Repite la nueva contraseña',
                              obscureText: true,
                              textInputAction: TextInputAction.done,
                              onSubmitted: (_) => _submit(),
                            ),
                            const SizedBox(height: 32),
                            GlassButton(
                              label: _loading ? 'Guardando...' : 'Cambiar contraseña',
                              width: double.infinity,
                              onPressed: _loading ? null : _submit,
                            ),
                          ],
                        ),
                      ),
          ),
        ],
      ),
    );
  }
}

class _NotAvailableView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.all(32),
      child: Column(
        children: [
          Icon(Icons.lock_outline, size: 48, color: GlassTokens.text2),
          SizedBox(height: 16),
          Text(
            'No disponible',
            style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.w700,
                color: GlassTokens.text0),
            textAlign: TextAlign.center,
          ),
          SizedBox(height: 8),
          Text(
            'El cambio de contraseña solo está disponible para cuentas con correo y contraseña. Las cuentas de Google se gestionan desde tu cuenta de Google.',
            style: TextStyle(fontSize: 13, color: GlassTokens.text2),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}
