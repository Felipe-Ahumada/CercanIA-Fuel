import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/chat_message_entity.dart';

abstract class ChatRepository {
  Future<Either<Failure, ChatMessageEntity>> sendMessage(
    String prompt, {
    double? latitude,
    double? longitude,
  });
}
