import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/bank_profile_entity.dart';

abstract class BankProfileRepository {
  Future<Either<Failure, BankProfileEntity>> getBankProfile();
  Future<Either<Failure, BankProfileEntity>> updateBankProfile(List<BankConvenio> convenios);
  Future<Either<Failure, List<String>>> getBanksCatalog();
  Future<Either<Failure, List<String>>> getCardTypesCatalog();
}
