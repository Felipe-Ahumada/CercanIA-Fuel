import '../../../core/network/dio_client.dart';
import '../../../domain/entities/discount_calculation_entity.dart';
import '../../../domain/entities/transaction_entity.dart';
import '../../models/transaction_model.dart';
import 'transaction_remote_data_source.dart';

class TransactionRemoteDataSourceImpl implements TransactionRemoteDataSource {
  final DioClient dioClient;

  TransactionRemoteDataSourceImpl(this.dioClient);

  @override
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
  }) async {
    final body = <String, dynamic>{
      'userId': userId,
      'vehicleId': vehicleId,
      'stationId': stationId,
      'fuelTypeId': fuelTypeId,
      'unitPrice': unitPrice,
      'liters': liters,
    };
    if (cardProductId != null) body['cardProductId'] = cardProductId;
    if (discountId != null) body['discountId'] = discountId;
    if (discountAmount != null) body['discountAmount'] = discountAmount;
    if (notes != null) body['notes'] = notes;

    final response = await dioClient.dio.post('/transacciones', data: body);
    return TransactionModel.fromJson(response.data as Map<String, dynamic>);
  }

  @override
  Future<DiscountCalculationEntity> calculateDiscount({
    required int brandId,
    required int fuelTypeId,
    required double grossAmount,
    required List<int> userCardIds,
    double? liters,
  }) async {
    final body = <String, dynamic>{
      'brandId': brandId,
      'fuelTypeId': fuelTypeId,
      'grossAmount': grossAmount,
      'userCardIds': userCardIds,
    };
    if (liters != null) body['liters'] = liters;
    final response = await dioClient.dio.post('/descuentos/calculate', data: body);
    final json = response.data as Map<String, dynamic>;
    return DiscountCalculationEntity(
      discountId: json['discountId'] as int?,
      description: json['description'] as String?,
      grossAmount: (json['grossAmount'] as num).toDouble(),
      discountAmount: (json['discountAmount'] as num).toDouble(),
      finalAmount: (json['finalAmount'] as num).toDouble(),
    );
  }
}
