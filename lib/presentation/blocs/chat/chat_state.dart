import '../../../domain/entities/chat_message_entity.dart';

class ChatState {
  final List<ChatMessageEntity> messages;
  final bool sending;
  final String? error;

  const ChatState({
    this.messages = const [],
    this.sending = false,
    this.error,
  });

  ChatState copyWith({
    List<ChatMessageEntity>? messages,
    bool? sending,
    String? error,
    bool clearError = false,
  }) {
    return ChatState(
      messages: messages ?? this.messages,
      sending: sending ?? this.sending,
      error: clearError ? null : (error ?? this.error),
    );
  }
}
