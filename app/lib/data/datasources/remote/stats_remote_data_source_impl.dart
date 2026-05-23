import '../../../core/config/app_config.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';
import '../../../domain/entities/savings_summary_entity.dart';
import '../../../domain/entities/transaction_entity.dart';
import '../../models/savings_summary_model.dart';
import '../../models/transaction_model.dart';
import 'stats_remote_data_source.dart';

class StatsRemoteDataSourceImpl implements StatsRemoteDataSource {
  final DioClient dioClient;

  String? _cachedUserId;

  StatsRemoteDataSourceImpl(this.dioClient);

  @override
  void clearUserCache() => _cachedUserId = null;

  Future<String> _resolveUserId() async {
    if (_cachedUserId != null) return _cachedUserId!;
    final response = await dioClient.dio.get('/auth/me');
    final id = (response.data as Map<String, dynamic>)['userId']?.toString();
    if (id == null || id.isEmpty) {
      throw ServerException(message: 'Backend did not return a valid user ID');
    }
    _cachedUserId = id;
    return _cachedUserId!;
  }

  @override
  Future<SavingsSummaryEntity> getMonthlySummary() async {
    if (AppConfig.useMockData) {
      await Future.delayed(const Duration(milliseconds: 400));
      return const SavingsSummaryModel(
        totalSaved: 18420,
        totalLiters: 312.5,
        transactionCount: 14,
        byMonth: [
          MonthlyStatModel(month: 'Feb', totalSaved: 2100, totalLiters: 45.0),
          MonthlyStatModel(month: 'Mar', totalSaved: 3800, totalLiters: 62.5),
          MonthlyStatModel(month: 'Abr', totalSaved: 5200, totalLiters: 88.0),
          MonthlyStatModel(month: 'May', totalSaved: 7320, totalLiters: 117.0),
        ],
      );
    }

    final userId = await _resolveUserId();
    final to = DateTime.now();
    final from = to.subtract(const Duration(days: 180));

    final response = await dioClient.dio.get(
      '/transacciones/summary',
      queryParameters: {
        'userId': userId,
        'desde': _fmtDate(from),
        'hasta': _fmtDate(to),
      },
    );
    return SavingsSummaryModel.fromJson(response.data as Map<String, dynamic>);
  }

  @override
  Future<List<TransactionEntity>> getTransactions() async {
    if (AppConfig.useMockData) {
      await Future.delayed(const Duration(milliseconds: 400));
      return [];
    }

    final userId = await _resolveUserId();
    final response = await dioClient.dio.get(
      '/transacciones',
      queryParameters: {'userId': userId, 'size': 50, 'sort': 'transactionDate,desc'},
    );

    final data = response.data;
    final list = (data is Map ? data['content'] : data) as List<dynamic>;
    return list
        .map((e) => TransactionModel.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  String _fmtDate(DateTime d) =>
      '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';
}
