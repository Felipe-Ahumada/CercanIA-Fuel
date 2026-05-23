import 'package:dartz/dartz.dart';
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
}
