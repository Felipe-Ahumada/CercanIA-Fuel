import 'package:get_it/get_it.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'data/datasources/remote/fuel_station_remote_data_source.dart';
import 'data/datasources/remote/auth_remote_data_source.dart';
import 'data/repositories/fuel_station_repository_impl.dart';
import 'data/repositories/auth_repository_impl.dart';
import 'domain/repositories/fuel_station_repository.dart';
import 'domain/repositories/auth_repository.dart';
import 'domain/usecases/get_fuel_stations.dart';
import 'domain/usecases/auth_usecases.dart';
import 'presentation/blocs/fuel_station_bloc.dart';
import 'presentation/blocs/auth/auth_bloc.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // Blocs
  sl.registerFactory(
    () => FuelStationBloc(getFuelStations: sl()),
  );
  sl.registerFactory(
    () => AuthBloc(
      signInUseCase: sl(),
      signUpUseCase: sl(),
      signOutUseCase: sl(),
      getCurrentUserUseCase: sl(),
    ),
  );

  // Use Cases
  sl.registerLazySingleton(() => GetFuelStations(sl()));
  sl.registerLazySingleton(() => SignInUseCase(sl()));
  sl.registerLazySingleton(() => SignUpUseCase(sl()));
  sl.registerLazySingleton(() => SignOutUseCase(sl()));
  sl.registerLazySingleton(() => ResetPasswordUseCase(sl()));
  sl.registerLazySingleton(() => GetCurrentUserUseCase(sl()));

  // Repository
  sl.registerLazySingleton<FuelStationRepository>(
    () => FuelStationRepositoryImpl(remoteDataSource: sl()),
  );
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryImpl(sl()),
  );

  // Data sources
  sl.registerLazySingleton<FuelStationRemoteDataSource>(
    () => FuelStationRemoteDataSourceImpl(),
  );
  sl.registerLazySingleton<AuthRemoteDataSource>(
    () => AuthRemoteDataSourceImpl(sl()),
  );

  // Core & External
  sl.registerLazySingleton(() => FirebaseAuth.instance);
}
