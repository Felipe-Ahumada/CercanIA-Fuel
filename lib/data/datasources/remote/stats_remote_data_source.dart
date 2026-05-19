import '../../../domain/entities/savings_summary_entity.dart';
import '../../../domain/entities/transaction_entity.dart';

abstract class StatsRemoteDataSource {
  Future<SavingsSummaryEntity> getMonthlySummary();
  Future<List<TransactionEntity>> getTransactions();
  void clearUserCache();
}
