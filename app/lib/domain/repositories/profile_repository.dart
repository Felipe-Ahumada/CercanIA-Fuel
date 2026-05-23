import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/user_entity.dart';

abstract class ProfileRepository {
  Future<Either<Failure, UserEntity>> getUserProfile();
  Future<Either<Failure, UserEntity>> updateUserProfile({
    required String firstName,
    String? middleName,
    required String lastName,
    String? secondLastName,
  });
}
