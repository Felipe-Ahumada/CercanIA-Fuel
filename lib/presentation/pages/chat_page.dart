import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../injection_container.dart' as di;
import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_card.dart';
import '../blocs/chat/chat_cubit.dart';
import '../blocs/chat/chat_state.dart';
import '../../domain/entities/chat_message_entity.dart';

class ChatPage extends StatelessWidget {
  const ChatPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => di.sl<ChatCubit>(),
      child: const _ChatView(),
    );
  }
}

class _ChatView extends StatefulWidget {
  const _ChatView();

  @override
  State<_ChatView> createState() => _ChatViewState();
}

class _ChatViewState extends State<_ChatView> {
  final _ctrl = TextEditingController();
  final _scrollCtrl = ScrollController();

  @override
  void dispose() {
    _ctrl.dispose();
    _scrollCtrl.dispose();
    super.dispose();
  }

  void _send() {
    final text = _ctrl.text.trim();
    if (text.isEmpty) return;
    context.read<ChatCubit>().sendMessage(text);
    _ctrl.clear();
    Future.delayed(const Duration(milliseconds: 100), _scrollToBottom);
  }

  void _scrollToBottom() {
    if (_scrollCtrl.hasClients) {
      _scrollCtrl.animateTo(
        _scrollCtrl.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final bottomPad = MediaQuery.of(context).viewInsets.bottom;

    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Column(
        children: [
          // Glass header
          ClipRect(
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 40, sigmaY: 40),
              child: Container(
                color: GlassTokens.headerBg,
                child: SafeArea(
                  bottom: false,
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(20, 12, 20, 14),
                    child: Row(
                      children: [
                        Container(
                          width: 42,
                          height: 42,
                          decoration: const BoxDecoration(
                            gradient: GlassTokens.accentGradient,
                            shape: BoxShape.circle,
                          ),
                          child: const Icon(Icons.auto_awesome,
                              color: Colors.white, size: 20),
                        ),
                        const SizedBox(width: 12),
                        const Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Asistente CercanIA',
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w800,
                                  color: GlassTokens.text0,
                                ),
                              ),
                              SizedBox(height: 2),
                              Row(
                                children: [
                                  _StatusDot(),
                                  SizedBox(width: 5),
                                  Text(
                                    'En línea',
                                    style: TextStyle(
                                      fontSize: 11,
                                      color: GlassTokens.green,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
          // Message list
          Expanded(
            child: BlocConsumer<ChatCubit, ChatState>(
              listener: (context, state) {
                if (!state.sending) _scrollToBottom();
                if (state.error != null) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: const Text(
                          'El asistente no está disponible ahora. Intenta de nuevo.'),
                      backgroundColor: GlassTokens.red.withValues(alpha: 0.85),
                      behavior: SnackBarBehavior.floating,
                    ),
                  );
                }
              },
              builder: (context, state) {
                if (state.messages.isEmpty) {
                  return _EmptyState(
                    onQuickReply: (text) {
                      context.read<ChatCubit>().sendMessage(text);
                      Future.delayed(
                          const Duration(milliseconds: 100), _scrollToBottom);
                    },
                  );
                }

                return ListView.builder(
                  controller: _scrollCtrl,
                  padding: EdgeInsets.fromLTRB(16, 16, 16, bottomPad + 16),
                  itemCount: state.messages.length + (state.sending ? 1 : 0),
                  itemBuilder: (context, i) {
                    if (i == state.messages.length && state.sending) {
                      return const _TypingBubble();
                    }
                    return _MessageBubble(message: state.messages[i]);
                  },
                );
              },
            ),
          ),
          // Glass input bar
          _InputBar(controller: _ctrl, onSend: _send),
        ],
      ),
    );
  }
}

// ── Status dot animado ────────────────────────────────────────────────────────

class _StatusDot extends StatefulWidget {
  const _StatusDot();

  @override
  State<_StatusDot> createState() => _StatusDotState();
}

class _StatusDotState extends State<_StatusDot>
    with SingleTickerProviderStateMixin {
  late final AnimationController _anim = AnimationController(
    vsync: this,
    duration: const Duration(seconds: 2),
  )..repeat(reverse: true);

  @override
  void dispose() {
    _anim.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _anim,
      builder: (_, __) => Container(
        width: 7,
        height: 7,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: GlassTokens.green.withValues(alpha: 0.5 + 0.5 * _anim.value),
          boxShadow: [
            BoxShadow(
              color: GlassTokens.greenGlow,
              blurRadius: 4 + 4 * _anim.value,
            ),
          ],
        ),
      ),
    );
  }
}

// ── Empty state con sugerencias ───────────────────────────────────────────────

class _EmptyState extends StatelessWidget {
  final ValueChanged<String> onQuickReply;

  const _EmptyState({required this.onQuickReply});

  static const _suggestions = [
    '¿Dónde está la bencinera más cercana?',
    '¿Cuánto he ahorrado este mes?',
    '¿Qué combustible usa mi auto?',
  ];

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.fromLTRB(20, 32, 20, 20),
      child: Column(
        children: [
          Container(
            width: 64,
            height: 64,
            decoration: const BoxDecoration(
              gradient: GlassTokens.accentGradientSoft,
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.chat_bubble_outline,
                size: 28, color: GlassTokens.green),
          ),
          const SizedBox(height: 16),
          const Text(
            '¿En qué te puedo ayudar?',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w800,
              color: GlassTokens.text0,
            ),
          ),
          const SizedBox(height: 6),
          const Text(
            'Pregúntame sobre precios, ahorros o estaciones.',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 13, color: GlassTokens.text2),
          ),
          const SizedBox(height: 28),
          ..._suggestions.map((s) => Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: GestureDetector(
                  onTap: () => onQuickReply(s),
                  child: GlassCard(
                    radius: 12,
                    level: 1,
                    padding: const EdgeInsets.symmetric(
                        horizontal: 16, vertical: 12),
                    child: Row(
                      children: [
                        Expanded(
                          child: Text(s,
                              style: const TextStyle(
                                fontSize: 13,
                                color: GlassTokens.text1,
                              )),
                        ),
                        const SizedBox(width: 8),
                        const Icon(Icons.arrow_forward_ios_rounded,
                            size: 13, color: GlassTokens.green),
                      ],
                    ),
                  ),
                ),
              )),
        ],
      ),
    );
  }
}

// ── Burbuja de mensaje ────────────────────────────────────────────────────────

class _MessageBubble extends StatelessWidget {
  final ChatMessageEntity message;
  const _MessageBubble({required this.message});

  @override
  Widget build(BuildContext context) {
    final isUser = message.role == ChatRole.user;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment:
            isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          if (!isUser) ...[
            _AiAvatar(),
            const SizedBox(width: 8),
          ],
          Flexible(
            child: Container(
              padding:
                  const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
              constraints: BoxConstraints(
                  maxWidth: MediaQuery.of(context).size.width * 0.72),
              decoration: BoxDecoration(
                gradient: isUser ? GlassTokens.accentGradient : null,
                color: isUser ? null : GlassTokens.glass1,
                borderRadius: BorderRadius.only(
                  topLeft: const Radius.circular(18),
                  topRight: const Radius.circular(18),
                  bottomLeft: Radius.circular(isUser ? 18 : 4),
                  bottomRight: Radius.circular(isUser ? 4 : 18),
                ),
                border: isUser
                    ? null
                    : Border.all(color: GlassTokens.border1),
                boxShadow: isUser
                    ? const [
                        BoxShadow(
                          color: GlassTokens.greenGlow,
                          blurRadius: 12,
                          offset: Offset(0, 3),
                        ),
                      ]
                    : null,
              ),
              child: Text(
                message.text,
                style: TextStyle(
                  fontSize: 14,
                  // Fix #1 — texto blanco en burbuja del usuario sobre gradiente verde
                  color: isUser ? GlassTokens.onAccent : GlassTokens.text0,
                  fontWeight: isUser ? FontWeight.w600 : FontWeight.w400,
                ),
              ),
            ),
          ),
          if (isUser) const SizedBox(width: 38),
        ],
      ),
    );
  }
}

// ── Avatar del AI ─────────────────────────────────────────────────────────────

class _AiAvatar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      width: 30,
      height: 30,
      margin: const EdgeInsets.only(bottom: 2),
      decoration: const BoxDecoration(
        gradient: GlassTokens.accentGradient,
        shape: BoxShape.circle,
      ),
      child: const Icon(Icons.auto_awesome, color: Colors.white, size: 14),
    );
  }
}

// ── Fix #5 — Typing bubble con tres puntos animados ───────────────────────────

class _TypingBubble extends StatefulWidget {
  const _TypingBubble();

  @override
  State<_TypingBubble> createState() => _TypingBubbleState();
}

class _TypingBubbleState extends State<_TypingBubble>
    with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl = AnimationController(
    vsync: this,
    duration: const Duration(milliseconds: 1200),
  )..repeat();

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          _AiAvatar(),
          const SizedBox(width: 8),
          Container(
            padding:
                const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            decoration: BoxDecoration(
              color: GlassTokens.glass1,
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(18),
                topRight: Radius.circular(18),
                bottomRight: Radius.circular(18),
                bottomLeft: Radius.circular(4),
              ),
              border: Border.all(color: GlassTokens.border1),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: List.generate(3, (i) => _Dot(index: i, ctrl: _ctrl)),
            ),
          ),
        ],
      ),
    );
  }
}

class _Dot extends StatelessWidget {
  final int index;
  final AnimationController ctrl;

  const _Dot({required this.index, required this.ctrl});

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: ctrl,
      builder: (_, __) {
        final offset = index * 0.25;
        final raw = (ctrl.value - offset) % 1.0;
        final t = raw < 0.5 ? raw * 2 : (1.0 - raw) * 2;
        return Container(
          width: 7,
          height: 7,
          margin: const EdgeInsets.symmetric(horizontal: 2.5),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: GlassTokens.green.withValues(alpha: 0.3 + 0.7 * t),
          ),
        );
      },
    );
  }
}

// ── Barra de input ────────────────────────────────────────────────────────────

class _InputBar extends StatelessWidget {
  final TextEditingController controller;
  final VoidCallback onSend;

  const _InputBar({required this.controller, required this.onSend});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 6, 16, 10),
        child: GlassCard(
          radius: 28,
          level: 1,
          padding: const EdgeInsets.fromLTRB(16, 8, 8, 8),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: controller,
                  decoration: const InputDecoration(
                    hintText: 'Escribe tu pregunta...',
                    hintStyle:
                        TextStyle(color: GlassTokens.text2, fontSize: 14),
                    border: InputBorder.none,
                    enabledBorder: InputBorder.none,
                    focusedBorder: InputBorder.none,
                    filled: false,
                    contentPadding: EdgeInsets.zero,
                    isDense: true,
                  ),
                  style: const TextStyle(
                      color: GlassTokens.text0, fontSize: 14),
                  textInputAction: TextInputAction.send,
                  onSubmitted: (_) => onSend(),
                  maxLines: null,
                ),
              ),
              const SizedBox(width: 8),
              // Fix #8 — 44×44 para área táctil mínima (era 38×38)
              BlocBuilder<ChatCubit, ChatState>(
                builder: (context, state) => GestureDetector(
                  onTap: state.sending ? null : onSend,
                  child: Container(
                    width: 44,
                    height: 44,
                    decoration: BoxDecoration(
                      gradient: !state.sending
                          ? GlassTokens.accentGradient
                          : null,
                      color: state.sending ? GlassTokens.glass1 : null,
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      Icons.send_rounded,
                      size: 18,
                      color: !state.sending
                          ? GlassTokens.onAccent
                          : GlassTokens.text3,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
