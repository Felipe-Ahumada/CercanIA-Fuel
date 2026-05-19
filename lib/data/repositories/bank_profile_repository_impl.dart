import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/repositories/bank_profile_repository.dart';
import '../datasources/remote/bank_profile_remote_data_source.dart';

class BankProfileRepositoryImpl implements BankProfileRepository {
  final BankProfileRemoteDataSource remoteDataSource;

  BankProfileRepositoryImpl(this.remoteDataSource);

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
