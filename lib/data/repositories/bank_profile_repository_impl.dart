import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/repositories/bank_profile_repository.dart';
import '../datasources/remote/bank_profile_remote_data_source.dart';
import '../../core/errors/exceptions.dart';

class BankProfileRepositoryImpl implements BankProfileRepository {
  final BankProfileRemoteDataSource remoteDataSource;

  BankProfileRepositoryImpl(this.remoteDataSource);

  @override
  Future<Either<Failure, BankProfileEntity>> getBankProfile() async {
    try {
      final profile = await remoteDataSource.getBankProfile();
      return Right(profile);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener perfil bancario'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al obtener perfil bancario'));
    }
  }

  @override
  Future<Either<Failure, BankProfileEntity>> updateBankProfile(List<BankConvenio> convenios) async {
    try {
      final profile = await remoteDataSource.updateBankProfile(convenios);
      return Right(profile);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al actualizar perfil bancario'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al actualizar perfil bancario'));
    }
  }

  @override
  Future<Either<Failure, List<String>>> getBanksCatalog() async {
    try {
      final catalog = await remoteDataSource.getBanksCatalog();
      return Right(catalog);
    } catch (e) {
      return const Left(ServerFailure('Error al cargar catálogo de bancos'));
    }
  }

  @override
  Future<Either<Failure, List<String>>> getCardTypesCatalog() async {
    try {
      final catalog = await remoteDataSource.getCardTypesCatalog();
      return Right(catalog);
    } catch (e) {
      return const Left(ServerFailure('Error al cargar catálogo de tarjetas'));
    }
  }
}
