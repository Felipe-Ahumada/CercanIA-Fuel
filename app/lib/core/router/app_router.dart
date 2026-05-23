import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../util/go_router_refresh_stream.dart';
import '../network/auth_token_events.dart';
import '../../presentation/pages/home_shell.dart';
import '../../presentation/pages/login_page.dart';
import '../../presentation/pages/register_page.dart';
import '../../presentation/pages/forgot_password_page.dart';
import '../../presentation/pages/complete_profile_page.dart';
import '../../presentation/pages/chat_page.dart';
import '../../presentation/pages/stats_page.dart';
import '../../presentation/screens/map_screen.dart';
import '../../presentation/pages/profile_page.dart';
import '../../presentation/pages/vehicles_page.dart';
import '../../presentation/pages/bank_profile_page.dart';
import '../../presentation/pages/station_detail_page.dart';
import '../../presentation/pages/edit_profile_page.dart';
import '../../presentation/pages/change_password_page.dart';

class AppRouter {
  final FirebaseAuth firebaseAuth;

  AppRouter(this.firebaseAuth);

  late final GoRouter router = GoRouter(
    initialLocation: '/login',
    // Combina Firebase auth changes (usuarios FIREBASE) con AuthTokenEvents
    // (usuarios LOCAL cuyo JWT expiró). Ambas fuentes disparan el redirect check.
    refreshListenable: Listenable.merge([
      GoRouterRefreshStream(firebaseAuth.authStateChanges()),
      AuthTokenEvents.listenable,
    ]),
    redirect: (BuildContext context, GoRouterState state) async {
      final prefs = await SharedPreferences.getInstance();
      final provider = prefs.getString('auth_provider');
      final bool isLocalAuth =
          provider == 'LOCAL' && prefs.getString('local_jwt') != null;
      final firebaseUser = firebaseAuth.currentUser;
      final bool isAuthenticated = firebaseUser != null || isLocalAuth;

      final bool isAuthRoute = const {
        '/login', '/register', '/forgot_password', '/complete_profile'
      }.contains(state.matchedLocation);

      if (!isAuthenticated && !isAuthRoute) return '/login';

      if (isAuthenticated) {
        // FIREBASE users: gate on profile_complete flag to prevent sending
        // a user without a backend account straight to /home/map.
        if (firebaseUser != null) {
          final complete =
              prefs.getBool('profile_complete_${firebaseUser.uid}') ?? false;
          if (!complete) {
            // Needs profile completion — go there (or stay if already there).
            return state.matchedLocation == '/complete_profile'
                ? null
                : '/complete_profile';
          }
        }
        // Profile complete (or LOCAL auth) + on an auth route → go home.
        if (isAuthRoute) return '/home/map';
      }

      return null;
    },
    routes: [
      // ── Auth routes ───────────────────────────────────────────────────────
      GoRoute(path: '/login',           builder: (_, __) => const LoginPage()),
      GoRoute(path: '/register',        builder: (_, __) => const RegisterPage()),
      GoRoute(path: '/forgot_password', builder: (_, __) => const ForgotPasswordPage()),
      GoRoute(path: '/complete_profile',builder: (_, __) => const CompleteProfilePage()),

      // ── Shell con BottomNavigationBar ─────────────────────────────────────
      StatefulShellRoute.indexedStack(
        builder: (ctx, state, shell) => HomeShell(navigationShell: shell),
        branches: [
          StatefulShellBranch(routes: [
            GoRoute(path: '/home/map',   builder: (_, __) => const MapScreen()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/home/chat',  builder: (_, __) => const ChatPage()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/home/stats', builder: (_, __) => const StatsPage()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/home/profile', builder: (_, __) => const ProfilePage()),
          ]),
        ],
      ),

      // ── Rutas auxiliares fuera del shell ──────────────────────────────────
      GoRoute(path: '/vehicles',    builder: (_, __) => const VehiclesPage()),
      GoRoute(path: '/bank_profile',builder: (_, __) => const BankProfilePage()),
      GoRoute(path: '/edit_profile', builder: (_, __) => const EditProfilePage()),
      GoRoute(path: '/change_password', builder: (_, __) => const ChangePasswordPage()),
      GoRoute(
        path: '/station_detail',
        redirect: (_, state) => state.extra == null ? '/home/map' : null,
        builder: (_, state) => StationDetailPage(stationId: state.extra as String),
      ),
    ],
  );
}
