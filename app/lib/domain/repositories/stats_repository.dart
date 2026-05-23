import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/savings_summary_entity.dart';
import '../entities/transaction_entity.dart';

abstract class StatsRepository {
  Future<Either<Failure, SavingsSummaryEntity>> getMonthlySummary();
  Future<Either<Failure, List<TransactionEntity>>> getTransactions();
}
