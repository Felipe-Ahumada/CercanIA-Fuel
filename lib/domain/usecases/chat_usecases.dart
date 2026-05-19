import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/chat_message_entity.dart';
import '../repositories/chat_repository.dart';

class SendChatMessageUseCase {
  final ChatRepository repository;
  SendChatMessageUseCase(this.repository);

  Future<Either<Failure, ChatMessageEntity>> call(
    String prompt, {
    double? latitude,
    double? longitude,
  }) =>
      repository.sendMessage(prompt, latitude: latitude, longitude: longitude);
}
