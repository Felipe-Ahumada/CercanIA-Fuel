import 'package:dartz/dartz.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/discount_calculation_entity.dart';
import '../../domain/entities/transaction_entity.dart';
import '../../domain/repositories/transaction_repository.dart';
import '../datasources/remote/transaction_remote_data_source.dart';

class TransactionRepositoryImpl implements TransactionRepository {
  final TransactionRemoteDataSource remoteDataSource;

  TransactionRepositoryImpl(this.remoteDataSource);

  @override
  Future<Either<Failure, TransactionEntity>> createTransaction({
    required String userId,
    required String vehicleId,
    required String stationId,
    required int fuelTypeId,
    required double unitPrice,
    required double liters,
    int? cardProductId,
    int? discountId,
    double? discountAmount,
    String? notes,
  }) async {
    try {
      final result = await remoteDataSource.createTransaction(
        userId: userId,
        vehicleId: vehicleId,
        stationId: stationId,
        fuelTypeId: fuelTypeId,
        unitPrice: unitPrice,
        liters: liters,
        cardProductId: cardProductId,
        discountId: discountId,
        discountAmount: discountAmount,
        notes: notes,
      );
      return Right(result);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al registrar la carga'));
    } catch (e) {
      return const Left(ServerFailure('Error inesperado al registrar la carga'));
    }
  }

  @override
  Future<Either<Failure, DiscountCalculationEntity>> calculateDiscount({
    required int brandId,
    required int fuelTypeId,
    required double grossAmount,
    required List<int> userCardIds,
    double? liters,
  }) async {
    try {
      final result = await remoteDataSource.calculateDiscount(
        brandId: brandId,
        fuelTypeId: fuelTypeId,
        grossAmount: grossAmount,
        userCardIds: userCardIds,
        liters: liters,
      );
      return Right(result);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al calcular descuento'));
    } catch (e) {
      return const Left(ServerFailure('Error al calcular descuento'));
    }
  }
}
