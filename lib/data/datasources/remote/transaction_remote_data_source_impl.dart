import '../../../core/network/dio_client.dart';
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
}
