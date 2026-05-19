import 'package:dartz/dartz.dart';
import '../entities/discount_calculation_entity.dart';
import '../entities/transaction_entity.dart';
import '../../core/errors/failure.dart';

abstract class TransactionRepository {
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
  });

  Future<Either<Failure, DiscountCalculationEntity>> calculateDiscount({
    required int brandId,
    required int fuelTypeId,
    required double grossAmount,
    required List<int> userCardIds,
    double? liters,
  });
}
