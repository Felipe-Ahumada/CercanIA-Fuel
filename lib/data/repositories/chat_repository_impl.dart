import 'package:dartz/dartz.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/chat_message_entity.dart';
import '../../domain/repositories/chat_repository.dart';
import '../datasources/remote/chat_remote_data_source.dart';

class ChatRepositoryImpl implements ChatRepository {
  final ChatRemoteDataSource remoteDataSource;

  ChatRepositoryImpl({required this.remoteDataSource});

  @override
  Future<Either<Failure, ChatMessageEntity>> sendMessage(
    String prompt, {
    double? latitude,
    double? longitude,
  }) async {
    try {
      return Right(await remoteDataSource.sendMessage(
        prompt,
        latitude: latitude,
        longitude: longitude,
      ));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al enviar mensaje'));
    } catch (e) {
      return const Left(ServerFailure('Error interno del asistente'));
    }
  }
}
