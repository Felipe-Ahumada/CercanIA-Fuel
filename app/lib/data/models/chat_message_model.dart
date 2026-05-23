import '../../domain/entities/chat_message_entity.dart';

class ChatMessageModel extends ChatMessageEntity {
  const ChatMessageModel({
    required super.id,
    required super.role,
    required super.text,
    required super.sentAt,
  });

  factory ChatMessageModel.fromJson(Map<String, dynamic> json) {
    return ChatMessageModel(
      id: json['id'] as String,
      role: json['role'] == 'ai' ? ChatRole.ai : ChatRole.user,
      text: json['text'] as String,
      sentAt: DateTime.parse(json['sentAt'] as String),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'role': role == ChatRole.ai ? 'ai' : 'user',
        'text': text,
        'sentAt': sentAt.toIso8601String(),
      };
}
