import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/bank_profile_entity.dart';
import '../repositories/bank_profile_repository.dart';

class GetDiscountsCatalogUseCase {
  final BankProfileRepository repository;
  GetDiscountsCatalogUseCase(this.repository);
  Future<Either<Failure, List<DiscountEntity>>> call() =>
      repository.getDiscountsCatalog();
}

class GetSelectedDiscountsUseCase {
  final BankProfileRepository repository;
  GetSelectedDiscountsUseCase(this.repository);
  Future<Either<Failure, List<DiscountEntity>>> call() =>
      repository.getSelectedDiscounts();
}

class UpdateSelectedDiscountsUseCase {
  final BankProfileRepository repository;
  UpdateSelectedDiscountsUseCase(this.repository);
  Future<Either<Failure, List<DiscountEntity>>> call(List<int> discountIds) =>
      repository.updateSelectedDiscounts(discountIds);
}
