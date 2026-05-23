import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/user_entity.dart';

abstract class AuthRepository {
  Future<Either<Failure, UserEntity>> signIn(String email, String password);
  Future<Either<Failure, UserEntity>> signUp({
    required String email,
    required String password,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  });
  Future<Either<Failure, UserEntity>> signInWithGoogle();
  Future<Either<Failure, UserEntity>> completeProfile({
    required String email,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  });
  Future<Either<Failure, void>> signOut();
  Future<Either<Failure, void>> sendPasswordResetEmail(String email);
  Future<Either<Failure, void>> changePassword({
    required String currentPassword,
    required String newPassword,
  });
  Future<Either<Failure, void>> requestLocalPasswordReset(String email);
  Future<Either<Failure, void>> confirmLocalPasswordReset({
    required String email,
    required String otp,
    required String newPassword,
  });
  Future<Either<Failure, UserEntity>> getCurrentUser();
}
