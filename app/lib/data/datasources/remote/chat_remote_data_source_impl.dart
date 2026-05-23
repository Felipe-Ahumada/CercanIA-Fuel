import 'package:dio/dio.dart';

import '../../../core/network/dio_client.dart';
import '../../../domain/entities/chat_message_entity.dart';
import '../../models/chat_message_model.dart';
import 'chat_remote_data_source.dart';

class ChatRemoteDataSourceImpl implements ChatRemoteDataSource {
  final DioClient dioClient;

  ChatRemoteDataSourceImpl(this.dioClient);

  @override
  Future<ChatMessageEntity> sendMessage(
    String prompt, {
    double? latitude,
    double? longitude,
  }) async {
    final body = <String, dynamic>{'prompt': prompt};
    if (latitude != null) body['latitude'] = latitude;
    if (longitude != null) body['longitude'] = longitude;

    // Gemini cold-start can take >10s on first call; override the global 10s timeout.
    final response = await dioClient.dio.post(
      '/chat',
      data: body,
      options: Options(receiveTimeout: const Duration(seconds: 60)),
    );
    return ChatMessageModel.fromJson(response.data as Map<String, dynamic>);
  }
}
