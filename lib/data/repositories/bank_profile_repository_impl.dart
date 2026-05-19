import 'package:dartz/dartz.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/repositories/bank_profile_repository.dart';
import '../datasources/remote/bank_profile_remote_data_source.dart';

class BankProfileRepositoryImpl implements BankProfileRepository {
  final BankProfileRemoteDataSource remoteDataSource;

  BankProfileRepositoryImpl(this.remoteDataSource);

  @override
  Future<Either<Failure, BankProfileEntity>> getBankProfile() async {
    try {
      return Right(await remoteDataSource.getBankProfile());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener perfil bancario'));
    } catch (_) {
      return const Left(ServerFailure('Error interno al obtener perfil bancario'));
    }
  }

  @override
  Future<Either<Failure, BankProfileEntity>> updateBankProfile(
      List<BankAgreement> agreements) async {
    try {
      return Right(await remoteDataSource.updateBankProfile(agreements));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al actualizar perfil bancario'));
    } catch (_) {
      return const Left(ServerFailure('Error interno al actualizar perfil bancario'));
    }
  }

  @override
  Future<Either<Failure, List<CardProductEntity>>> getCardProducts() async {
    try {
      return Right(await remoteDataSource.getCardProducts());
    } catch (_) {
      return const Left(ServerFailure('Error al cargar catálogo de tarjetas'));
    }
  }

  @override
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsByBrand(
      int brandId) async {
    try {
      return Right(await remoteDataSource.getDiscountsByBrand(brandId));
    } catch (_) {
      return const Left(ServerFailure('Error al cargar descuentos'));
    }
  }

  @override
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsByCardProducts(
      List<int> cardProductIds) async {
    try {
      return Right(
          await remoteDataSource.getDiscountsByCardProducts(cardProductIds));
    } catch (_) {
      return const Left(ServerFailure('Error al cargar descuentos'));
    }
  }

  @override
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsCatalog() async {
    try {
      return Right(await remoteDataSource.getDiscountsCatalog());
    } catch (_) {
      return const Left(ServerFailure('Error al cargar catálogo de descuentos'));
    }
  }

  @override
  Future<Either<Failure, List<DiscountEntity>>> getSelectedDiscounts() async {
    try {
      return Right(await remoteDataSource.getSelectedDiscounts());
    } catch (_) {
      return const Left(ServerFailure('Error al cargar descuentos seleccionados'));
    }
  }

  @override
  Future<Either<Failure, List<DiscountEntity>>> updateSelectedDiscounts(
      List<int> discountIds) async {
    try {
      return Right(await remoteDataSource.updateSelectedDiscounts(discountIds));
    } catch (_) {
      return const Left(ServerFailure('Error al guardar descuentos'));
    }
  }
}
