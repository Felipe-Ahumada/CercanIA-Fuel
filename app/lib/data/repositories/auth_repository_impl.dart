import 'package:dartz/dartz.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/errors/failure.dart';
import '../../../domain/entities/user_entity.dart';
import '../../../domain/repositories/auth_repository.dart';
import '../datasources/remote/auth_remote_data_source.dart';
import '../datasources/remote/stats_remote_data_source.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;
  final StatsRemoteDataSource statsRemoteDataSource;

  AuthRepositoryImpl(this.remoteDataSource, this.statsRemoteDataSource);

  @override
  Future<Either<Failure, UserEntity>> signIn(String email, String password) async {
    try {
      return Right(await remoteDataSource.signIn(email, password));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al iniciar sesión.'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> signUp({
    required String email,
    required String password,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) async {
    try {
      return Right(await remoteDataSource.signUp(
        email: email,
        password: password,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        secondLastName: secondLastName,
        rut: rut,
        birthDate: birthDate,
      ));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al registrar.'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> signInWithGoogle() async {
    try {
      return Right(await remoteDataSource.signInWithGoogle());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al iniciar sesión con Google.'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> completeProfile({
    required String email,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) async {
    try {
      return Right(await remoteDataSource.completeProfile(
        email: email,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        secondLastName: secondLastName,
        rut: rut,
        birthDate: birthDate,
      ));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al completar perfil.'));
    }
  }

  @override
  Future<Either<Failure, void>> signOut() async {
    try {
      await remoteDataSource.signOut();
      statsRemoteDataSource.clearUserCache();
      return const Right(null);
    } on ServerException {
      return const Left(ServerFailure('Error al cerrar sesión.'));
    }
  }

  @override
  Future<Either<Failure, void>> sendPasswordResetEmail(String email) async {
    try {
      await remoteDataSource.sendPasswordResetEmail(email);
      return const Right(null);
    } on ServerException {
      return const Left(ServerFailure('Error al enviar correo de recuperación.'));
    }
  }

  @override
  Future<Either<Failure, void>> changePassword({
    required String currentPassword,
    required String newPassword,
  }) async {
    try {
      await remoteDataSource.changePassword(
        currentPassword: currentPassword,
        newPassword: newPassword,
      );
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al cambiar contraseña.'));
    }
  }

  @override
  Future<Either<Failure, void>> requestLocalPasswordReset(String email) async {
    try {
      await remoteDataSource.requestLocalPasswordReset(email);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al solicitar recuperación.'));
    }
  }

  @override
  Future<Either<Failure, void>> confirmLocalPasswordReset({
    required String email,
    required String otp,
    required String newPassword,
  }) async {
    try {
      await remoteDataSource.confirmLocalPasswordReset(
        email: email,
        otp: otp,
        newPassword: newPassword,
      );
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Código inválido o expirado.'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> getCurrentUser() async {
    try {
      return Right(await remoteDataSource.getCurrentUser());
    } on ServerException {
      return const Left(ServerFailure('Usuario no autenticado.'));
    }
  }
}
