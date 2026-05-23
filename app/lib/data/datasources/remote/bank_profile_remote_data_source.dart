import '../../../domain/entities/bank_profile_entity.dart';

abstract class BankProfileRemoteDataSource {
  Future<List<DiscountEntity>> getDiscountsCatalog();
  Future<List<DiscountEntity>> getSelectedDiscounts();
  Future<List<DiscountEntity>> updateSelectedDiscounts(List<int> discountIds);
}
