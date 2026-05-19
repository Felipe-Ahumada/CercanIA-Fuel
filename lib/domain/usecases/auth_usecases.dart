import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/user_entity.dart';
import '../repositories/auth_repository.dart';

class SignInUseCase {
  final AuthRepository repository;
  SignInUseCase(this.repository);
  Future<Either<Failure, UserEntity>> call(String email, String password) =>
      repository.signIn(email, password);
}

class SignUpUseCase {
  final AuthRepository repository;
  SignUpUseCase(this.repository);
  Future<Either<Failure, UserEntity>> call({
    required String email,
    required String password,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) =>
      repository.signUp(
        email: email,
        password: password,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        secondLastName: secondLastName,
        rut: rut,
        birthDate: birthDate,
      );
}

class SignInWithGoogleUseCase {
  final AuthRepository repository;
  SignInWithGoogleUseCase(this.repository);
  Future<Either<Failure, UserEntity>> call() => repository.signInWithGoogle();
}

class CompleteProfileUseCase {
  final AuthRepository repository;
  CompleteProfileUseCase(this.repository);
  Future<Either<Failure, UserEntity>> call({
    required String email,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) =>
      repository.completeProfile(
        email: email,
        firstName: firstName,
        middleName: middleName,
        lastName: lastName,
        secondLastName: secondLastName,
        rut: rut,
        birthDate: birthDate,
      );
}

class SignOutUseCase {
  final AuthRepository repository;
  SignOutUseCase(this.repository);
  Future<Either<Failure, void>> call() => repository.signOut();
}

class ResetPasswordUseCase {
  final AuthRepository repository;
  ResetPasswordUseCase(this.repository);
  Future<Either<Failure, void>> call(String email) =>
      repository.sendPasswordResetEmail(email);
}

class GetCurrentUserUseCase {
  final AuthRepository repository;
  GetCurrentUserUseCase(this.repository);
  Future<Either<Failure, UserEntity>> call() => repository.getCurrentUser();
}
