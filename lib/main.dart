import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:firebase_core/firebase_core.dart';
import 'injection_container.dart' as di;

import 'presentation/blocs/auth/auth_bloc.dart';
import 'presentation/blocs/profile/profile_cubit.dart';
import 'presentation/blocs/vehicle/vehicle_bloc.dart';
import 'presentation/blocs/bank_profile/bank_profile_cubit.dart';
import 'presentation/blocs/map/map_bloc.dart';
import 'core/router/app_router.dart';
import 'presentation/theme/app_theme.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Inicializamos Firebase
  await Firebase.initializeApp();

  // Inicializamos la inyección de dependencias
  await di.init();
  runApp(const CercaniaFuelApp());
}

class CercaniaFuelApp extends StatelessWidget {
  const CercaniaFuelApp({super.key});

  @override
  Widget build(BuildContext context) {
    final appRouter = di.sl<AppRouter>().router;

    return MultiBlocProvider(
      providers: [
        BlocProvider(
          create: (_) => di.sl<AuthBloc>()..add(AuthCheckRequested()),
        ),
        BlocProvider(
          create: (_) => di.sl<ProfileCubit>(),
        ),
        BlocProvider(
          create: (_) => di.sl<VehicleBloc>(),
        ),
        BlocProvider(
          create: (_) => di.sl<BankProfileCubit>(),
        ),
        BlocProvider(
          create: (_) => di.sl<MapBloc>(),
        ),
      ],
      child: MaterialApp.router(
        title: 'CercanIA Fuel',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.lightTheme,
        routerConfig: appRouter,
      ),
    );
  }
}
