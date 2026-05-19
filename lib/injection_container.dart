import 'package:get_it/get_it.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'data/datasources/remote/auth_remote_data_source.dart';
import 'data/datasources/remote/chat_remote_data_source.dart';
import 'data/datasources/remote/chat_remote_data_source_impl.dart';
import 'data/datasources/remote/stats_remote_data_source.dart';
import 'data/datasources/remote/stats_remote_data_source_impl.dart';
import 'data/repositories/auth_repository_impl.dart';
import 'data/repositories/chat_repository_impl.dart';
import 'data/repositories/profile_repository_impl.dart';
import 'data/repositories/bank_profile_repository_impl.dart';
import 'data/repositories/stats_repository_impl.dart';
import 'data/repositories/vehicle_repository_impl.dart';
import 'data/datasources/remote/vehicle_remote_data_source.dart';
import 'data/datasources/remote/vehicle_remote_data_source_impl.dart';
import 'data/datasources/remote/bank_profile_remote_data_source.dart';
import 'data/datasources/remote/bank_profile_remote_data_source_impl.dart';
import 'domain/repositories/chat_repository.dart';
import 'domain/repositories/stats_repository.dart';
import 'domain/repositories/vehicle_repository.dart';
import 'domain/repositories/bank_profile_repository.dart';
import 'domain/repositories/station_repository.dart';
import 'data/repositories/station_repository_impl.dart';
import 'data/datasources/remote/station_remote_data_source.dart';
import 'domain/usecases/chat_usecases.dart';
import 'domain/usecases/stats_usecases.dart';
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
import 'data/datasources/remote/transaction_remote_data_source.dart';
import 'data/datasources/remote/transaction_remote_data_source_impl.dart';
import 'data/repositories/transaction_repository_impl.dart';
import 'domain/repositories/transaction_repository.dart';
import 'domain/usecases/transaction_usecases.dart';
import 'domain/usecases/auth_usecases.dart';
import 'presentation/blocs/auth/auth_bloc.dart';
import 'presentation/blocs/chat/chat_cubit.dart';
import 'presentation/blocs/profile/profile_cubit.dart';
import 'presentation/blocs/stats/stats_cubit.dart';
import 'presentation/blocs/vehicle/vehicle_bloc.dart';
import 'presentation/blocs/bank_profile/bank_profile_cubit.dart';
import 'presentation/blocs/map/map_bloc.dart';
import 'presentation/blocs/station_detail/station_detail_cubit.dart';
import 'presentation/blocs/register_visit/register_visit_cubit.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // Blocs
  sl.registerFactory(
    () => AuthBloc(
      signInUseCase: sl(),
      signUpUseCase: sl(),
      signInWithGoogleUseCase: sl(),
      completeProfileUseCase: sl(),
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
      getBrandsUseCase: sl(),
      getModelsByBrandUseCase: sl(),
      getFuelTypesUseCase: sl(),
      getVehiclesUseCase: sl(),
      addVehicleUseCase: sl(),
      deleteVehicleUseCase: sl(),
    ),
  );

  sl.registerFactory(() => BankProfileCubit(
    getCatalogUseCase: sl(),
    getSelectedUseCase: sl(),
    updateSelectedUseCase: sl(),
  ));

  sl.registerFactory(() => MapBloc(
    getNearbyStationsUseCase: sl(),
  ));

  sl.registerFactory(() => StationDetailCubit(sl()));
  sl.registerFactory(() => RegisterVisitCubit(createTransactionUseCase: sl()));
  sl.registerFactory(() => ChatCubit(sendChatMessageUseCase: sl(), mapBloc: sl()));
  sl.registerFactory(() => StatsCubit(
        getSavingsSummaryUseCase: sl(),
        getUserTransactionsUseCase: sl(),
      ));

  // Use Cases
  sl.registerLazySingleton(() => SignInUseCase(sl()));
  sl.registerLazySingleton(() => SignUpUseCase(sl()));
  sl.registerLazySingleton(() => SignInWithGoogleUseCase(sl()));
  sl.registerLazySingleton(() => CompleteProfileUseCase(sl()));
  sl.registerLazySingleton(() => SignOutUseCase(sl()));
  sl.registerLazySingleton(() => ResetPasswordUseCase(sl()));
  sl.registerLazySingleton(() => GetCurrentUserUseCase(sl()));
  sl.registerLazySingleton(() => GetUserProfileUseCase(sl()));
  sl.registerLazySingleton(() => UpdateUserProfileUseCase(sl()));
  sl.registerLazySingleton(() => GetVehicleBrandsUseCase(sl()));
  sl.registerLazySingleton(() => GetVehicleModelsByBrandUseCase(sl()));
  sl.registerLazySingleton(() => GetFuelTypesUseCase(sl()));
  sl.registerLazySingleton(() => GetVehiclesUseCase(sl()));
  sl.registerLazySingleton(() => AddVehicleUseCase(sl()));
  sl.registerLazySingleton(() => DeleteVehicleUseCase(sl()));
  sl.registerLazySingleton(() => GetDiscountsCatalogUseCase(sl()));
  sl.registerLazySingleton(() => GetSelectedDiscountsUseCase(sl()));
  sl.registerLazySingleton(() => UpdateSelectedDiscountsUseCase(sl()));
  sl.registerLazySingleton(() => GetNearbyStationsUseCase(sl()));
  sl.registerLazySingleton(() => GetStationDetailUseCase(sl()));
  sl.registerLazySingleton(() => SendChatMessageUseCase(sl()));
  sl.registerLazySingleton(() => GetSavingsSummaryUseCase(sl()));
  sl.registerLazySingleton(() => GetUserTransactionsUseCase(sl()));
  sl.registerLazySingleton(() => CreateTransactionUseCase(sl()));
  sl.registerLazySingleton(() => CalculateDiscountUseCase(sl()));

  // Repository
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryImpl(sl(), sl()),
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
  sl.registerLazySingleton<ChatRepository>(
    () => ChatRepositoryImpl(remoteDataSource: sl()),
  );
  sl.registerLazySingleton<StatsRepository>(
    () => StatsRepositoryImpl(remoteDataSource: sl()),
  );
  sl.registerLazySingleton<TransactionRepository>(
    () => TransactionRepositoryImpl(sl()),
  );

  // Data sources
  sl.registerLazySingleton<AuthRemoteDataSource>(
    () => AuthRemoteDataSourceImpl(sl(), sl(), sl()),
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
  sl.registerLazySingleton<ChatRemoteDataSource>(
    () => ChatRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<StatsRemoteDataSource>(
    () => StatsRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<TransactionRemoteDataSource>(
    () => TransactionRemoteDataSourceImpl(sl()),
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