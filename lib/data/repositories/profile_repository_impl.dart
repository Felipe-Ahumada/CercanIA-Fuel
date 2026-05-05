import 'package:dartz/dartz.dart';
import '../../domain/entities/user_entity.dart';
import '../../domain/repositories/profile_repository.dart';
import '../../core/errors/failure.dart';
import '../../core/errors/exceptions.dart';
import '../datasources/remote/auth_remote_data_source.dart';

class ProfileRepositoryImpl implements ProfileRepository {
  final AuthRemoteDataSource authRemoteDataSource;

  ProfileRepositoryImpl(this.authRemoteDataSource);

  @override
  Future<Either<Failure, UserEntity>> getUserProfile() async {
    try {
      final user = await authRemoteDataSource.getCurrentUser();
      return Right(user);
    } on ServerException {
      return const Left(ServerFailure('No se pudo obtener el perfil del usuario.'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> updateUserProfile(String name, String? photoUrl) async {
    try {
      final user = await authRemoteDataSource.updateProfile(name, photoUrl);
      return Right(user);
    } on ServerException {
      return const Left(ServerFailure('No se pudo actualizar el perfil.'));
    }
  }
}
