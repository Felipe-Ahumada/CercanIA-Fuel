enum ChatRole { user, ai }

class ChatMessageEntity {
  final String id;
  final ChatRole role;
  final String text;
  final DateTime sentAt;

  const ChatMessageEntity({
    required this.id,
    required this.role,
    required this.text,
    required this.sentAt,
  });
}
