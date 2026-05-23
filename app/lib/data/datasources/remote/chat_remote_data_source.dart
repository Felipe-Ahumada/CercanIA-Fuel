import '../../../domain/entities/chat_message_entity.dart';

abstract class ChatRemoteDataSource {
  Future<ChatMessageEntity> sendMessage(String prompt, {double? latitude, double? longitude});
}
