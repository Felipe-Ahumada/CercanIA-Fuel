import 'dart:async';
import 'package:flutter/foundation.dart';

/// Stream que emite cuando el JWT LOCAL expira (401 del backend).
/// GoRouter lo escucha vía [AuthTokenEvents.listenable] para forzar
/// un re-check del redirect sin depender de Firebase auth state changes.
class AuthTokenEvents {
  AuthTokenEvents._();

  static final _controller = StreamController<void>.broadcast();

  /// Emite un evento de expiración — llamado por [AuthInterceptor].
  static void notifyExpired() => _controller.add(null);

  /// [Listenable] para combinar con GoRouterRefreshStream.
  static final listenable = _AuthTokenListenable._(_controller.stream);
}

class _AuthTokenListenable extends ChangeNotifier {
  late final StreamSubscription<void> _sub;

  _AuthTokenListenable._(Stream<void> stream) {
    _sub = stream.listen((_) => notifyListeners());
  }

  @override
  void dispose() {
    _sub.cancel();
    super.dispose();
  }
}
