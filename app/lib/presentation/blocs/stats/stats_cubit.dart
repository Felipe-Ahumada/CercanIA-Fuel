import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../../../data/models/savings_summary_model.dart';
import '../../../domain/entities/savings_summary_entity.dart';
import '../../../domain/entities/transaction_entity.dart';
import '../../../domain/usecases/stats_usecases.dart';
import 'stats_state.dart';

class StatsCubit extends Cubit<StatsState> {
  final GetSavingsSummaryUseCase getSavingsSummaryUseCase;
  final GetUserTransactionsUseCase getUserTransactionsUseCase;

  StatsCubit({
    required this.getSavingsSummaryUseCase,
    required this.getUserTransactionsUseCase,
  }) : super(StatsInitial());

  Future<void> load() async {
    emit(StatsLoading());

    final summaryResult = await getSavingsSummaryUseCase();
    if (summaryResult.isLeft()) {
      emit(StatsError(summaryResult.fold((l) => l.message, (_) => '')));
      return;
    }

    final txResult = await getUserTransactionsUseCase();
    final summary = summaryResult.getOrElse(() => throw StateError(''));
    final transactions = txResult.getOrElse(() => <TransactionEntity>[]);

    // Prefer server-side byMonth (full history); fall back to local computation
    // from the 50-transaction page only if the backend returns nothing.
    final byMonth = summary.byMonth.isNotEmpty
        ? summary.byMonth
        : _buildByMonth(transactions);

    final summaryWithMonths = SavingsSummaryModel(
      totalSaved: summary.totalSaved,
      totalSpent: summary.totalSpent,
      totalLiters: summary.totalLiters,
      transactionCount: summary.transactionCount,
      from: summary.from,
      to: summary.to,
      byMonth: byMonth,
    );

    emit(StatsLoaded(summary: summaryWithMonths, transactions: transactions));
  }

  static List<MonthlyStatEntity> _buildByMonth(List<TransactionEntity> txs) {
    if (txs.isEmpty) return [];

    final monthFmt = DateFormat('MMM', 'es_CL');
    final map = <String, _MonthAcc>{};

    for (final tx in txs) {
      final key =
          '${tx.transactionDate.year}-${tx.transactionDate.month.toString().padLeft(2, '0')}';
      map.putIfAbsent(key, () => _MonthAcc(monthFmt.format(tx.transactionDate)));
      map[key]!
        ..totalSaved += tx.discountAmount
        ..totalLiters += tx.liters;
    }

    final sorted = map.entries.toList()..sort((a, b) => a.key.compareTo(b.key));
    return sorted
        .map((e) => MonthlyStatModel(
              month: e.value.label,
              totalSaved: e.value.totalSaved,
              totalLiters: e.value.totalLiters,
            ))
        .toList();
  }
}

class _MonthAcc {
  final String label;
  double totalSaved = 0;
  double totalLiters = 0;
  _MonthAcc(this.label);
}
