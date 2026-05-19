class MonthlyStatEntity {
  final String month;
  final double totalSaved;
  final double totalLiters;

  const MonthlyStatEntity({
    required this.month,
    required this.totalSaved,
    required this.totalLiters,
  });
}

class SavingsSummaryEntity {
  final double totalSaved;
  final double totalSpent;
  final double totalLiters;
  final int transactionCount;
  final DateTime? from;
  final DateTime? to;
  // NOTE: byMonth is not returned by the backend — populated only with mock data.
  final List<MonthlyStatEntity> byMonth;

  const SavingsSummaryEntity({
    required this.totalSaved,
    this.totalSpent = 0,
    required this.totalLiters,
    required this.transactionCount,
    this.from,
    this.to,
    this.byMonth = const [],
  });
}
