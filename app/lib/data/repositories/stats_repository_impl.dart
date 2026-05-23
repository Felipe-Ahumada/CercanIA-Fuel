import 'package:dartz/dartz.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/savings_summary_entity.dart';
import '../../domain/entities/transaction_entity.dart';
import '../../domain/repositories/stats_repository.dart';
import '../datasources/remote/stats_remote_data_source.dart';

class StatsRepositoryImpl implements StatsRepository {
  final StatsRemoteDataSource remoteDataSource;

  StatsRepositoryImpl({required this.remoteDataSource});

  @override
  Future<Either<Failure, SavingsSummaryEntity>> getMonthlySummary() async {
    try {
      return Right(await remoteDataSource.getMonthlySummary());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener resumen'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al cargar stats'));
    }
  }

  @override
  Future<Either<Failure, List<TransactionEntity>>> getTransactions() async {
    try {
      return Right(await remoteDataSource.getTransactions());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener transacciones'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al cargar transacciones'));
    }
  }
}
