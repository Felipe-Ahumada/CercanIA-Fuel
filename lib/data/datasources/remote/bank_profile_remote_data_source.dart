import '../../../domain/entities/bank_profile_entity.dart';

abstract class BankProfileRemoteDataSource {
  Future<BankProfileEntity> getBankProfile();
  Future<BankProfileEntity> updateBankProfile(List<BankAgreement> agreements);
  Future<List<CardProductEntity>> getCardProducts();
  Future<List<DiscountEntity>> getDiscountsByBrand(int brandId);
  Future<List<DiscountEntity>> getDiscountsByCardProducts(List<int> cardProductIds);
  // Nuevo flujo: selección directa de descuentos
  Future<List<DiscountEntity>> getDiscountsCatalog();
  Future<List<DiscountEntity>> getSelectedDiscounts();
  Future<List<DiscountEntity>> updateSelectedDiscounts(List<int> discountIds);
}
