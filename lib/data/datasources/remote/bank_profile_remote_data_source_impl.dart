import 'package:dio/dio.dart';

import '../../../core/network/dio_client.dart';
import '../../../domain/entities/bank_profile_entity.dart';
import 'bank_profile_remote_data_source.dart';

class BankProfileRemoteDataSourceImpl implements BankProfileRemoteDataSource {
  final DioClient dioClient;

  BankProfileRemoteDataSourceImpl(this.dioClient);

  @override
  Future<List<DiscountEntity>> getDiscountsCatalog() async {
    final response = await dioClient.get('/descuentos/catalogo');
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<List<DiscountEntity>> getSelectedDiscounts() async {
    final response = await dioClient.get('/users/me/discounts');
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<List<DiscountEntity>> updateSelectedDiscounts(List<int> discountIds) async {
    final response = await dioClient.put(
      '/users/me/discounts',
      data: discountIds,
      options: Options(contentType: Headers.jsonContentType),
    );
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  DiscountEntity _parseDiscount(Map<String, dynamic> json) => DiscountEntity(
        id: (json['id'] as num).toInt(),
        brandId: (json['brandId'] as num).toInt(),
        brandName: json['brandName'] as String? ?? '',
        cardProductId: json['cardProductId'] != null
            ? (json['cardProductId'] as num).toInt()
            : null,
        cardProductName: json['cardProductName'] as String? ?? '',
        bankName: json['bankName'] as String?,
        fuelTypeName: json['fuelTypeName'] as String?,
        dayOfWeek: json['dayOfWeek'] != null ? (json['dayOfWeek'] as num).toInt() : null,
        discountType: json['discountType'] as String? ?? 'PERCENTAGE',
        discountValue: (json['discountValue'] as num).toDouble(),
        maxCap: json['maxCap'] != null ? (json['maxCap'] as num).toDouble() : null,
        description: json['description'] as String?,
      );
}
