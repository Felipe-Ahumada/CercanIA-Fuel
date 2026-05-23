import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/bank_profile_entity.dart';

abstract class BankProfileRepository {
  Future<Either<Failure, List<DiscountEntity>>> getDiscountsCatalog();
  Future<Either<Failure, List<DiscountEntity>>> getSelectedDiscounts();
  Future<Either<Failure, List<DiscountEntity>>> updateSelectedDiscounts(List<int> discountIds);
}
