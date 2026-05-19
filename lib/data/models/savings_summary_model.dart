import '../../domain/entities/savings_summary_entity.dart';

class MonthlyStatModel extends MonthlyStatEntity {
  const MonthlyStatModel({
    required super.month,
    required super.totalSaved,
    required super.totalLiters,
  });

  factory MonthlyStatModel.fromJson(Map<String, dynamic> json) =>
      MonthlyStatModel(
        month: json['month'] as String,
        totalSaved:  ((json['totalSaved']  ?? json['totalAhorrado']  ?? 0) as num).toDouble(),
        totalLiters: ((json['totalLiters'] ?? json['totalLitros']    ?? 0) as num).toDouble(),
      );
}

class SavingsSummaryModel extends SavingsSummaryEntity {
  const SavingsSummaryModel({
    required super.totalSaved,
    super.totalSpent,
    required super.totalLiters,
    required super.transactionCount,
    super.from,
    super.to,
    super.byMonth,
  });

  factory SavingsSummaryModel.fromJson(Map<String, dynamic> json) {
    DateTime? from;
    DateTime? to;
    final rawFrom = json['desde'];
    final rawTo = json['hasta'];
    if (rawFrom != null) from = DateTime.tryParse(rawFrom.toString());
    if (rawTo != null) to = DateTime.tryParse(rawTo.toString());

    return SavingsSummaryModel(
      totalSaved:       ((json['totalSaved']  ?? json['totalAhorrado']  ?? 0) as num).toDouble(),
      totalSpent:       ((json['totalSpent']  ?? json['totalGastado']  ?? 0) as num).toDouble(),
      totalLiters:      ((json['totalLiters'] ?? json['totalLitros']   ?? 0) as num).toDouble(),
      transactionCount: (json['fillCount']   ?? json['transactionCount'] ?? 0) as int,
      from: from,
      to: to,
      byMonth: json['byMonth'] != null
          ? (json['byMonth'] as List)
              .map((e) => MonthlyStatModel.fromJson(e as Map<String, dynamic>))
              .toList()
          : const [],
    );
  }
}
