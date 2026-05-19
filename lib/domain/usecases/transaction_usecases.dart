import 'package:dartz/dartz.dart';
import '../entities/discount_calculation_entity.dart';
import '../entities/transaction_entity.dart';
import '../repositories/transaction_repository.dart';
import '../../core/errors/failure.dart';

class CreateTransactionUseCase {
  final TransactionRepository repository;
  CreateTransactionUseCase(this.repository);

  Future<Either<Failure, TransactionEntity>> call({
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
  }) =>
      repository.createTransaction(
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
}

class CalculateDiscountUseCase {
  final TransactionRepository repository;
  CalculateDiscountUseCase(this.repository);

  Future<Either<Failure, DiscountCalculationEntity>> call({
    required int brandId,
    required int fuelTypeId,
    required double grossAmount,
    required List<int> userCardIds,
    double? liters,
  }) =>
      repository.calculateDiscount(
        brandId: brandId,
        fuelTypeId: fuelTypeId,
        grossAmount: grossAmount,
        userCardIds: userCardIds,
        liters: liters,
      );
}
