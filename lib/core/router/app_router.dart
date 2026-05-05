import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:firebase_auth/firebase_auth.dart';

import '../../presentation/pages/login_page.dart';
import '../../presentation/pages/register_page.dart';
import '../../presentation/pages/forgot_password_page.dart';
import '../../presentation/screens/map_screen.dart';
import '../../presentation/pages/profile_page.dart';
import '../../presentation/pages/vehicles_page.dart';
import '../../presentation/pages/bank_profile_page.dart';
import '../../presentation/pages/station_detail_page.dart';

class AppRouter {
  final FirebaseAuth firebaseAuth;

  AppRouter(this.firebaseAuth);

  late final GoRouter router = GoRouter(
    initialLocation: '/login',
    redirect: (BuildContext context, GoRouterState state) {
      final bool isAuthenticated = firebaseAuth.currentUser != null;
      final bool isAuthRoute = state.matchedLocation == '/login' || 
                               state.matchedLocation == '/register' ||
                               state.matchedLocation == '/forgot_password';

      if (!isAuthenticated && !isAuthRoute) {
        return '/login';
      }

      if (isAuthenticated && isAuthRoute) {
        return '/map';
      }

      return null; // No redirigir
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterPage(),
      ),
      GoRoute(
        path: '/forgot_password',
        builder: (context, state) => const ForgotPasswordPage(),
      ),
      GoRoute(
        path: '/map',
        builder: (context, state) => const MapScreen(),
      ),
      GoRoute(
        path: '/profile',
        builder: (context, state) => const ProfilePage(),
      ),
      GoRoute(
        path: '/vehicles',
        builder: (context, state) => const VehiclesPage(),
      ),
      GoRoute(
        path: '/bank_profile',
        builder: (context, state) => const BankProfilePage(),
      ),
      GoRoute(
        path: '/station_detail',
        builder: (context, state) => StationDetailPage(stationId: state.extra as String),
      ),
    ],
  );
}
