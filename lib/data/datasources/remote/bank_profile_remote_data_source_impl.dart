import 'package:dio/dio.dart';

import '../../../core/config/app_config.dart';
import '../../../core/network/dio_client.dart';
import '../../../domain/entities/bank_profile_entity.dart';
import '../../mock/mock_backend_data.dart';
import 'bank_profile_remote_data_source.dart';

class BankProfileRemoteDataSourceImpl implements BankProfileRemoteDataSource {
  final DioClient dioClient;

  BankProfileRemoteDataSourceImpl(this.dioClient);

  // ── Profile (backend) ─────────────────────────────────────────────────────

  @override
  Future<BankProfileEntity> getBankProfile() async {
    if (AppConfig.useMockData) return MockBackendData.bankProfile;

    final response = await dioClient.get('/users/me/bank-profile');
    final json = response.data as Map<String, dynamic>;
    final convenios = (json['convenios'] as List? ?? []);
    final agreements = convenios
        .map((e) => BankAgreement(
              cardProductId: (e['cardProductId'] as num?)?.toInt() ?? 0,
              cardProductName: e['cardType'] as String? ?? '',
              bankName: e['bank'] as String? ?? '',
            ))
        .where((a) => a.cardProductId > 0)
        .toList();
    return BankProfileEntity(
      userId: json['userId']?.toString() ?? '',
      agreements: agreements,
    );
  }

  @override
  Future<BankProfileEntity> updateBankProfile(List<BankAgreement> agreements) async {
    if (AppConfig.useMockData) {
      return BankProfileEntity(userId: '', agreements: agreements);
    }

    final body = {
      'convenios': agreements
          .map((a) => {
                'bank': a.bankName,
                'cardType': a.cardProductName,
                'cardProductId': a.cardProductId,
              })
          .toList(),
    };
    final response = await dioClient.post('/users/me/bank-profile', data: body);
    final json = response.data as Map<String, dynamic>;
    final convenios = (json['convenios'] as List? ?? []);
    final saved = convenios
        .map((e) => BankAgreement(
              cardProductId: (e['cardProductId'] as num?)?.toInt() ?? 0,
              cardProductName: e['cardType'] as String? ?? '',
              bankName: e['bank'] as String? ?? '',
            ))
        .where((a) => a.cardProductId > 0)
        .toList();
    return BankProfileEntity(
      userId: json['userId']?.toString() ?? '',
      agreements: saved,
    );
  }

  // ── Card products catalog ─────────────────────────────────────────────────

  @override
  Future<List<CardProductEntity>> getCardProducts() async {
    if (AppConfig.useMockData) return MockBackendData.cardProductsCatalog;

    final response = await dioClient.get('/tarjetas-producto');
    final items = response.data as List<dynamic>? ?? [];
    return items
        .map((e) => _parseCardProduct(e as Map<String, dynamic>))
        .where((c) => c.id > 0)
        .toList();
  }

  CardProductEntity _parseCardProduct(Map<String, dynamic> json) => CardProductEntity(
        id: (json['id'] as num).toInt(),
        bankName: json['bankName'] as String? ?? '',
        productName: json['name'] as String? ?? '',
        cardType: json['cardType'] as String? ?? '',
      );

  // ── Discounts by brand ────────────────────────────────────────────────────

  @override
  Future<List<DiscountEntity>> getDiscountsByBrand(int brandId) async {
    if (AppConfig.useMockData) return [];

    final response = await dioClient.get(
      '/descuentos',
      queryParameters: {'brandId': brandId},
    );
    final items = response.data as List<dynamic>? ?? [];
    return items
        .map((e) => _parseDiscount(e as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<List<DiscountEntity>> getDiscountsCatalog() async {
    if (AppConfig.useMockData) return [];
    final response = await dioClient.get('/descuentos/catalogo');
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<List<DiscountEntity>> getSelectedDiscounts() async {
    if (AppConfig.useMockData) return [];
    final response = await dioClient.get('/users/me/discounts');
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<List<DiscountEntity>> updateSelectedDiscounts(List<int> discountIds) async {
    if (AppConfig.useMockData) return [];
    final response = await dioClient.put(
      '/users/me/discounts',
      data: discountIds,
      options: Options(contentType: Headers.jsonContentType),
    );
    final items = response.data as List<dynamic>? ?? [];
    return items.map((e) => _parseDiscount(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<List<DiscountEntity>> getDiscountsByCardProducts(
      List<int> cardProductIds) async {
    if (AppConfig.useMockData) return [];
    if (cardProductIds.isEmpty) return [];

    final response = await dioClient.get(
      '/descuentos/por-tarjetas',
      queryParameters: {'cardProductIds': cardProductIds.join(',')},
    );
    final items = response.data as List<dynamic>? ?? [];
    return items
        .map((e) => _parseDiscount(e as Map<String, dynamic>))
        .toList();
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
