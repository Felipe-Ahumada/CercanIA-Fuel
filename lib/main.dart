import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'injection_container.dart' as di;

import 'presentation/blocs/auth/auth_bloc.dart';
import 'presentation/blocs/profile/profile_cubit.dart';
import 'presentation/blocs/vehicle/vehicle_bloc.dart';
import 'presentation/blocs/vehicle/vehicle_event.dart';
import 'presentation/blocs/bank_profile/bank_profile_cubit.dart';
import 'presentation/blocs/map/map_bloc.dart';
import 'presentation/blocs/stats/stats_cubit.dart';
import 'core/router/app_router.dart';
import 'presentation/theme/app_theme.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initializeDateFormatting('es_CL');
  await Firebase.initializeApp();
  await di.init();
  runApp(const CercaniaFuelApp());
}

class CercaniaFuelApp extends StatelessWidget {
  const CercaniaFuelApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(
          create: (_) => di.sl<AuthBloc>()..add(AuthCheckRequested()),
        ),
        BlocProvider(create: (_) => di.sl<ProfileCubit>()),
        BlocProvider(create: (_) => di.sl<VehicleBloc>()),
        BlocProvider(create: (_) => di.sl<BankProfileCubit>()),
        BlocProvider(create: (_) => di.sl<MapBloc>()),
        BlocProvider(create: (_) => di.sl<StatsCubit>()),
      ],
      child: BlocListener<AuthBloc, AuthState>(
        listener: (context, state) {
          final router = di.sl<AppRouter>().router;
          if (state is AuthNeedsProfileCompletion) {
            router.go('/complete_profile');
          } else if (state is AuthAuthenticated) {
            context.read<VehicleBloc>().add(LoadVehiclesEvent());
            final current = router.routerDelegate.currentConfiguration
                .matches.last.matchedLocation;
            // Navigate to home after login, register, or profile completion.
            // GoRouterRefreshStream won't trigger for LOCAL auth users.
            const authScreens = {'/login', '/register', '/complete_profile'};
            if (authScreens.contains(current)) {
              router.go('/home/map');
            }
          } else if (state is AuthUnauthenticated) {
            router.go('/login');
          }
        },
        child: MaterialApp.router(
          title: 'CercanIA Fuel',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.lightTheme,
          routerConfig: di.sl<AppRouter>().router,
        ),
      ),
    );
  }
}
