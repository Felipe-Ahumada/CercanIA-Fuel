import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/savings_summary_entity.dart';
import '../entities/transaction_entity.dart';
import '../repositories/stats_repository.dart';

class GetSavingsSummaryUseCase {
  final StatsRepository repository;
  GetSavingsSummaryUseCase(this.repository);

  Future<Either<Failure, SavingsSummaryEntity>> call() =>
      repository.getMonthlySummary();
}

class GetUserTransactionsUseCase {
  final StatsRepository repository;
  GetUserTransactionsUseCase(this.repository);

  Future<Either<Failure, List<TransactionEntity>>> call() =>
      repository.getTransactions();
}
