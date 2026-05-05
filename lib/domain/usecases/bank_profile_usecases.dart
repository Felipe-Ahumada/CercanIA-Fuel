import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/bank_profile_entity.dart';
import '../repositories/bank_profile_repository.dart';

class GetBankProfileUseCase {
  final BankProfileRepository repository;
  GetBankProfileUseCase(this.repository);

  Future<Either<Failure, BankProfileEntity>> call() async {
    return await repository.getBankProfile();
  }
}

class UpdateBankProfileUseCase {
  final BankProfileRepository repository;
  UpdateBankProfileUseCase(this.repository);

  Future<Either<Failure, BankProfileEntity>> call(List<BankConvenio> convenios) async {
    return await repository.updateBankProfile(convenios);
  }
}

class GetBanksCatalogUseCase {
  final BankProfileRepository repository;
  GetBanksCatalogUseCase(this.repository);

  Future<Either<Failure, List<String>>> call() async {
    return await repository.getBanksCatalog();
  }
}

class GetCardTypesCatalogUseCase {
  final BankProfileRepository repository;
  GetCardTypesCatalogUseCase(this.repository);

  Future<Either<Failure, List<String>>> call() async {
    return await repository.getCardTypesCatalog();
  }
}
