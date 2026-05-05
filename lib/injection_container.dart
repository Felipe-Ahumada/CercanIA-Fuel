import 'package:get_it/get_it.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'data/datasources/remote/auth_remote_data_source.dart';
import 'data/repositories/auth_repository_impl.dart';
import 'data/repositories/profile_repository_impl.dart';
import 'data/repositories/bank_profile_repository_impl.dart';
import 'data/repositories/vehicle_repository_impl.dart';
import 'data/datasources/remote/vehicle_remote_data_source.dart';
import 'data/datasources/remote/vehicle_remote_data_source_impl.dart';
import 'data/datasources/remote/bank_profile_remote_data_source.dart';
import 'data/datasources/remote/bank_profile_remote_data_source_impl.dart';
import 'domain/repositories/vehicle_repository.dart';
import 'domain/repositories/bank_profile_repository.dart';
import 'domain/repositories/station_repository.dart';
import 'data/repositories/station_repository_impl.dart';
import 'data/datasources/remote/station_remote_data_source.dart';
import 'domain/usecases/station_usecases.dart';
import 'domain/repositories/auth_repository.dart';
import 'domain/repositories/profile_repository.dart';
import 'domain/usecases/profile_usecases.dart';
import 'domain/usecases/vehicle_usecases.dart';
import 'domain/usecases/bank_profile_usecases.dart';
import 'package:dio/dio.dart';
import 'package:internet_connection_checker/internet_connection_checker.dart';
import 'core/network/network_info.dart';
import 'core/network/auth_interceptor.dart';
import 'core/network/dio_client.dart';
import 'core/router/app_router.dart';
import 'domain/usecases/auth_usecases.dart';
import 'presentation/blocs/auth/auth_bloc.dart';
import 'presentation/blocs/profile/profile_cubit.dart';
import 'presentation/blocs/vehicle/vehicle_bloc.dart';
import 'presentation/blocs/bank_profile/bank_profile_cubit.dart';
import 'presentation/blocs/map/map_bloc.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // Blocs
  sl.registerFactory(
    () => AuthBloc(
      signInUseCase: sl(),
      signUpUseCase: sl(),
      signInWithGoogleUseCase: sl(),
      signOutUseCase: sl(),
      getCurrentUserUseCase: sl(),
    ),
  );
  sl.registerFactory(
    () => ProfileCubit(
      getUserProfileUseCase: sl(),
      updateUserProfileUseCase: sl(),
    ),
  );
  sl.registerFactory(
    () => VehicleBloc(
      getVehiclesUseCase: sl(),
      addVehicleUseCase: sl(),
      deleteVehicleUseCase: sl(),
      setActiveVehicleUseCase: sl(),
    ),
  );

  sl.registerFactory(() => BankProfileCubit(
    getBankProfileUseCase: sl(),
    updateBankProfileUseCase: sl(),
    getBanksCatalogUseCase: sl(),
    getCardTypesCatalogUseCase: sl(),
  ));

  sl.registerFactory(() => MapBloc(
    getNearbyStationsUseCase: sl(),
    toggleFavoriteUseCase: sl(),
  ));

  // Use Cases
  sl.registerLazySingleton(() => SignInUseCase(sl()));
  sl.registerLazySingleton(() => SignUpUseCase(sl()));
  sl.registerLazySingleton(() => SignInWithGoogleUseCase(sl()));
  sl.registerLazySingleton(() => SignOutUseCase(sl()));
  sl.registerLazySingleton(() => ResetPasswordUseCase(sl()));
  sl.registerLazySingleton(() => GetCurrentUserUseCase(sl()));
  sl.registerLazySingleton(() => GetUserProfileUseCase(sl()));
  sl.registerLazySingleton(() => UpdateUserProfileUseCase(sl()));
  sl.registerLazySingleton(() => GetVehiclesUseCase(sl()));
  sl.registerLazySingleton(() => AddVehicleUseCase(sl()));
  sl.registerLazySingleton(() => DeleteVehicleUseCase(sl()));
  sl.registerLazySingleton(() => SetActiveVehicleUseCase(sl()));
  sl.registerLazySingleton(() => GetBankProfileUseCase(sl()));
  sl.registerLazySingleton(() => UpdateBankProfileUseCase(sl()));
  sl.registerLazySingleton(() => GetBanksCatalogUseCase(sl()));
  sl.registerLazySingleton(() => GetCardTypesCatalogUseCase(sl()));
  sl.registerLazySingleton(() => GetNearbyStationsUseCase(sl()));
  sl.registerLazySingleton(() => GetStationDetailUseCase(sl()));
  sl.registerLazySingleton(() => ToggleFavoriteUseCase(sl()));

  // Repository
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryImpl(sl()),
  );
  sl.registerLazySingleton<ProfileRepository>(
    () => ProfileRepositoryImpl(sl()),
  );
  sl.registerLazySingleton<VehicleRepository>(
    () => VehicleRepositoryImpl(sl()),
  );
  sl.registerLazySingleton<BankProfileRepository>(
    () => BankProfileRepositoryImpl(sl()),
  );
  sl.registerLazySingleton<StationRepository>(
    () => StationRepositoryImpl(remoteDataSource: sl()),
  );

  // Data sources
  sl.registerLazySingleton<AuthRemoteDataSource>(
    () => AuthRemoteDataSourceImpl(sl(), sl()),
  );
  sl.registerLazySingleton<VehicleRemoteDataSource>(
    () => VehicleRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<BankProfileRemoteDataSource>(
    () => BankProfileRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<StationRemoteDataSource>(
    () => StationRemoteDataSourceImpl(sl()),
  );

  // Core & External
  sl.registerLazySingleton<NetworkInfo>(
    () => NetworkInfoImpl(sl()),
  );
  sl.registerLazySingleton(() => InternetConnectionChecker.createInstance());

  sl.registerLazySingleton(() => AuthInterceptor(sl()));
  sl.registerLazySingleton(() => Dio());
  sl.registerLazySingleton(
    () => DioClient(dio: sl(), authInterceptor: sl()),
  );

  sl.registerLazySingleton(() => AppRouter(sl()));

  sl.registerLazySingleton(() => FirebaseAuth.instance);
  sl.registerLazySingleton(() => GoogleSignIn());
}