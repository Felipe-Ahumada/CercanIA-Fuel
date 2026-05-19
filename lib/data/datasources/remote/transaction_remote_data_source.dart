import '../../../domain/entities/discount_calculation_entity.dart';
import '../../../domain/entities/transaction_entity.dart';

abstract class TransactionRemoteDataSource {
  Future<TransactionEntity> createTransaction({
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

  Future<DiscountCalculationEntity> calculateDiscount({
    required int brandId,
    required int fuelTypeId,
    required double grossAmount,
    required List<int> userCardIds,
    double? liters,
  });
}
