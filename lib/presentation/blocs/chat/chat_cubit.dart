import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';

import '../../../domain/entities/chat_message_entity.dart';
import '../../../domain/usecases/chat_usecases.dart';
import '../map/map_bloc.dart';
import '../map/map_state.dart';
import 'chat_state.dart';

class ChatCubit extends Cubit<ChatState> {
  final SendChatMessageUseCase sendChatMessageUseCase;
  final MapBloc mapBloc;

  ChatCubit({
    required this.sendChatMessageUseCase,
    required this.mapBloc,
  }) : super(const ChatState());

  Future<void> sendMessage(String prompt) async {
    if (prompt.trim().isEmpty || state.sending) return;

    final userMsg = ChatMessageEntity(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      role: ChatRole.user,
      text: prompt.trim(),
      sentAt: DateTime.now(),
    );

    emit(state.copyWith(
      messages: [...state.messages, userMsg],
      sending: true,
      clearError: true,
    ));

    final (lat, lng) = await _resolveLocation();

    final result = await sendChatMessageUseCase(
      prompt.trim(),
      latitude: lat,
      longitude: lng,
    );

    result.fold(
      (failure) => emit(state.copyWith(sending: false, error: failure.message)),
      (aiMsg) => emit(state.copyWith(
        messages: [...state.messages, aiMsg],
        sending: false,
        clearError: true,
      )),
    );
  }

  Future<(double?, double?)> _resolveLocation() async {
    // Prefer MapBloc location — already obtained and cached.
    final mapState = mapBloc.state;
    if (mapState is MapLoaded && mapState.userLocation != null) {
      return (mapState.userLocation!.latitude, mapState.userLocation!.longitude);
    }

    // Fallback: ask Geolocator directly (e.g. user opened chat without the map).
    try {
      final permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        return (null, null);
      }
      final pos = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.medium,
        timeLimit: const Duration(seconds: 5),
      );
      return (pos.latitude, pos.longitude);
    } catch (_) {
      return (null, null);
    }
  }
}
