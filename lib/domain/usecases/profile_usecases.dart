import 'package:dartz/dartz.dart';
import '../entities/user_entity.dart';
import '../repositories/profile_repository.dart';
import '../../core/errors/failure.dart';

class GetUserProfileUseCase {
  final ProfileRepository repository;

  GetUserProfileUseCase(this.repository);

  Future<Either<Failure, UserEntity>> call() async {
    return await repository.getUserProfile();
  }
}

class UpdateUserProfileUseCase {
  final ProfileRepository repository;

  UpdateUserProfileUseCase(this.repository);

  Future<Either<Failure, UserEntity>> call(String name, {String? photoUrl}) async {
    return await repository.updateUserProfile(name, photoUrl);
  }
}
