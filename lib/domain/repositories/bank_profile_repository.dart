import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/bank_profile_entity.dart';

abstract class BankProfileRepository {
  Future<Either<Failure, BankProfileEntity>> getBankProfile();
  Future<Either<Failure, BankProfileEntity>> updateBankProfile(List<BankAgreement> agreements);
  Future<Either<Failure, List<CardProductEntity>>> getCardProducts();
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsByBrand(int brandId);
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsByCardProducts(List<int> cardProductIds);
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsCatalog();
  Future<Either<Failure, List<DiscountEntity>>> getSelectedDiscounts();
  Future<Either<Failure, List<DiscountEntity>>> updateSelectedDiscounts(List<int> discountIds);
}
