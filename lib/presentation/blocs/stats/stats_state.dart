import '../../../domain/entities/savings_summary_entity.dart';
import '../../../domain/entities/transaction_entity.dart';

abstract class StatsState {}

class StatsInitial extends StatsState {}

class StatsLoading extends StatsState {}

class StatsLoaded extends StatsState {
  final SavingsSummaryEntity summary;
  final List<TransactionEntity> transactions;

  StatsLoaded({required this.summary, required this.transactions});
}

class StatsError extends StatsState {
  final String message;
  StatsError(this.message);
}
