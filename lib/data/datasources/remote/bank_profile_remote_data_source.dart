import 'package:flutter_application_1/domain/entities/bank_profile_entity.dart';

abstract class BankProfileRemoteDataSource {
  Future<BankProfileEntity> getBankProfile();
  Future<BankProfileEntity> updateBankProfile(List<BankConvenio> convenios);
  Future<List<String>> getBanksCatalog();
  Future<List<String>> getCardTypesCatalog();
}