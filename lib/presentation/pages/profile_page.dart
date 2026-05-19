import 'dart:math';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../blocs/profile/profile_cubit.dart';
import '../blocs/profile/profile_state.dart';
import '../blocs/auth/auth_bloc.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  @override
  void initState() {
    super.initState();
    context.read<ProfileCubit>().fetchProfile();
  }

  String _initials(String? name, String email) {
    if (name != null && name.isNotEmpty) {
      final parts = name.trim().split(' ');
      if (parts.length >= 2) {
        return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
      }
      return name.substring(0, min(2, name.length)).toUpperCase();
    }
    return email.substring(0, min(2, email.length)).toUpperCase();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: BlocConsumer<ProfileCubit, ProfileState>(
        listener: (context, state) {
          if (state is ProfileError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message)),
            );
          }
        },
        builder: (context, state) {
          if (state is ProfileLoading || state is ProfileInitial) {
            return const GlassLoadingIndicator();
          }

          if (state is ProfileLoaded) {
            final user = state.user;
            final initials = _initials(user.name, user.email);
            final displayName = (user.name != null && user.name!.isNotEmpty)
                ? user.name!
                : user.email.split('@').first;

            return SingleChildScrollView(
              padding: const EdgeInsets.only(bottom: 100),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Hero header
                  ClipRect(
                    child: BackdropFilter(
                      filter: ImageFilter.blur(sigmaX: 40, sigmaY: 40),
                      child: Container(
                        color: GlassTokens.headerBg,
                        child: SafeArea(
                          bottom: false,
                          child: Padding(
                            padding: const EdgeInsets.fromLTRB(20, 20, 20, 24),
                            child: Row(
                              children: [
                                Container(
                                  width: 56,
                                  height: 56,
                                  decoration: const BoxDecoration(
                                    gradient: GlassTokens.accentGradient,
                                    shape: BoxShape.circle,
                                  ),
                                  alignment: Alignment.center,
                                  child: Text(
                                    initials,
                                    style: const TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.w800,
                                      color: GlassTokens.text0,
                                    ),
                                  ),
                                ),
                                const SizedBox(width: 14),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        displayName,
                                        style: const TextStyle(
                                          fontSize: 18,
                                          fontWeight: FontWeight.w800,
                                          color: GlassTokens.text0,
                                        ),
                                      ),
                                      const SizedBox(height: 2),
                                      Text(
                                        user.email,
                                        style: const TextStyle(
                                          fontSize: 12,
                                          color: GlassTokens.text2,
                                        ),
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  // Navigation sections
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: Column(
                      children: [
                        _NavSection(
                          icon: Icons.person_outline,
                          iconColor: GlassTokens.purple,
                          title: 'Editar Perfil',
                          subtitle: 'Nombre y apellidos',
                          onTap: () => context.push('/edit_profile'),
                        ),
                        const SizedBox(height: 10),
                        _NavSection(
                          icon: Icons.directions_car,
                          iconColor: GlassTokens.green,
                          title: 'Mis Vehículos',
                          subtitle: 'Administra tu flota',
                          onTap: () => context.push('/vehicles'),
                        ),
                        const SizedBox(height: 10),
                        _NavSection(
                          icon: Icons.local_offer_outlined,
                          iconColor: GlassTokens.cyan,
                          title: 'Mis Descuentos',
                          subtitle: 'Selecciona tus beneficios activos',
                          onTap: () => context.push('/bank_profile'),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 28),
                  // Logout
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: GestureDetector(
                      onTap: () {
                        context.read<AuthBloc>().add(AuthSignOutRequested());
                        context.go('/login');
                      },
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(16),
                        child: BackdropFilter(
                          filter: ImageFilter.blur(sigmaX: 20, sigmaY: 20),
                          child: Container(
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            decoration: BoxDecoration(
                              color: GlassTokens.red.withValues(alpha: 0.10),
                              borderRadius: BorderRadius.circular(16),
                              border: Border.all(
                                color: GlassTokens.red.withValues(alpha: 0.22),
                              ),
                            ),
                            alignment: Alignment.center,
                            child: const Text(
                              'Cerrar sesión',
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.w700,
                                color: GlassTokens.red,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            );
          }

          return const Center(child: Text('Error al cargar perfil'));
        },
      ),
    );
  }
}

class _NavSection extends StatelessWidget {
  final IconData icon;
  final Color iconColor;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  const _NavSection({
    required this.icon,
    required this.iconColor,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: GlassCard(
        radius: 16,
        level: 1,
        padding: const EdgeInsets.all(14),
        child: Row(
          children: [
            Container(
              width: 42,
              height: 42,
              decoration: BoxDecoration(
                color: GlassTokens.glass2,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: iconColor.withValues(alpha: 0.20)),
              ),
              child: Icon(icon, color: iconColor, size: 20),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w700,
                      color: GlassTokens.text0,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: const TextStyle(
                      fontSize: 11,
                      color: GlassTokens.text2,
                    ),
                  ),
                ],
              ),
            ),
            const Text(
              '›',
              style: TextStyle(
                fontSize: 20,
                color: GlassTokens.text2,
                fontWeight: FontWeight.w300,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
