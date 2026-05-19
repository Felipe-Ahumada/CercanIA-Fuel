import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../../core/theme/glass_tokens.dart';
import '../blocs/stats/stats_cubit.dart';

class HomeShell extends StatefulWidget {
  final StatefulNavigationShell navigationShell;

  const HomeShell({super.key, required this.navigationShell});

  @override
  State<HomeShell> createState() => _HomeShellState();
}

class _HomeShellState extends State<HomeShell> {
  static const _statsTabIndex = 2;

  void _onTabSelected(int index) {
    if (index == _statsTabIndex) {
      context.read<StatsCubit>().load();
    }
    widget.navigationShell.goBranch(
      index,
      initialLocation: index == widget.navigationShell.currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      extendBody: true,
      body: Container(
        decoration: const BoxDecoration(gradient: GlassTokens.pageGradient),
        child: Stack(
          children: [
            Positioned(
              top: -80,
              right: -100,
              child: _Blob(
                size: 320,
                color: GlassTokens.green.withValues(alpha: 0.07),
              ),
            ),
            Positioned(
              top: 180,
              left: -120,
              child: _Blob(
                size: 360,
                color: GlassTokens.cyan.withValues(alpha: 0.06),
              ),
            ),
            Positioned(
              bottom: 80,
              right: -80,
              child: _Blob(
                size: 260,
                color: GlassTokens.purple.withValues(alpha: 0.05),
              ),
            ),
            widget.navigationShell,
          ],
        ),
      ),
      bottomNavigationBar: ClipRect(
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 20, sigmaY: 20),
          child: NavigationBar(
            height: 70,
            backgroundColor: GlassTokens.navBg,
            selectedIndex: widget.navigationShell.currentIndex,
            labelBehavior: NavigationDestinationLabelBehavior.alwaysShow,
            indicatorColor: GlassTokens.greenGlow,
            onDestinationSelected: _onTabSelected,
            destinations: const [
              NavigationDestination(
                icon: Icon(Icons.map_outlined),
                selectedIcon: Icon(Icons.map, color: GlassTokens.green),
                label: 'Mapa',
              ),
              NavigationDestination(
                icon: Icon(Icons.chat_bubble_outline),
                selectedIcon: Icon(Icons.chat_bubble, color: GlassTokens.green),
                label: 'Asistente',
              ),
              NavigationDestination(
                icon: Icon(Icons.local_gas_station_outlined),
                selectedIcon:
                    Icon(Icons.local_gas_station, color: GlassTokens.green),
                label: 'Recargas',
              ),
              NavigationDestination(
                icon: Icon(Icons.person_outline),
                selectedIcon: Icon(Icons.person, color: GlassTokens.green),
                label: 'Perfil',
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _Blob extends StatelessWidget {
  final double size;
  final Color color;

  const _Blob({required this.size, required this.color});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        gradient: RadialGradient(
          colors: [color, color.withValues(alpha: 0)],
        ),
      ),
    );
  }
}
