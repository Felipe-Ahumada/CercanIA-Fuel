import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'auth_token_events.dart';

const _kAuthProvider   = 'auth_provider';
const _kLocalJwt       = 'local_jwt';
const _kLocalUserId    = 'local_user_id';
const _kLocalUserEmail = 'local_user_email';
const _kLocalUserRole  = 'local_user_role';
const _kLocalUserName  = 'local_user_name';

class AuthInterceptor extends Interceptor {
  final FirebaseAuth firebaseAuth;

  AuthInterceptor(this.firebaseAuth);

  bool _handlingLogout = false;

  // ── onRequest ─────────────────────────────────────────────────────────────

  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    if (_handlingLogout) {
      handler.reject(DioException(
        requestOptions: options,
        type: DioExceptionType.cancel,
        message: 'Session expired',
      ));
      return;
    }

    final prefs = await SharedPreferences.getInstance();
    final provider = prefs.getString(_kAuthProvider);

    if (provider == 'LOCAL') {
      final jwt = prefs.getString(_kLocalJwt);

      // ── Check proactivo: detectar JWT vencido antes de enviarlo ─────────
      if (jwt == null || _isJwtExpired(jwt)) {
        _handlingLogout = true;
        await _clearSession(prefs);
        _handlingLogout = false;
        handler.reject(DioException(
          requestOptions: options,
          type: DioExceptionType.cancel,
          message: 'JWT expired',
        ));
        return;
      }

      options.headers['Authorization'] = 'Bearer $jwt';
    } else {
      // 'FIREBASE', 'GOOGLE' (legacy), or null
      final user = firebaseAuth.currentUser;
      if (user != null) {
        try {
          final token = await user.getIdToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
            if (provider == null || provider == 'GOOGLE') {
              await prefs.setString(_kAuthProvider, 'FIREBASE');
            }
          }
        } catch (_) {
          // Token refresh failed — request proceeds without header.
        }
      }
    }

    return super.onRequest(options, handler);
  }

  // ── onError ───────────────────────────────────────────────────────────────

  @override
  Future<void> onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode == 401 && !_handlingLogout) {
      _handlingLogout = true;
      final prefs = await SharedPreferences.getInstance();
      await _clearSession(prefs);
      _handlingLogout = false;
    }
    handler.next(err);
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  Future<void> _clearSession(SharedPreferences prefs) async {
    await Future.wait([
      prefs.remove(_kAuthProvider),
      prefs.remove(_kLocalJwt),
      prefs.remove(_kLocalUserId),
      prefs.remove(_kLocalUserEmail),
      prefs.remove(_kLocalUserRole),
      prefs.remove(_kLocalUserName),
    ]);
    try { await firebaseAuth.signOut(); } catch (_) {}
    // Notifica al router para usuarios LOCAL (sin Firebase auth state changes).
    AuthTokenEvents.notifyExpired();
  }

  /// Decodifica el payload del JWT y verifica si el claim `exp` ya venció.
  /// Retorna `true` (vencido) ante cualquier error de parseo para ser conservador.
  static bool _isJwtExpired(String jwt) {
    try {
      final parts = jwt.split('.');
      if (parts.length != 3) return true;

      // Base64Url decode del payload (segunda parte)
      final normalized = base64Url.normalize(parts[1]);
      final payload = utf8.decode(base64Url.decode(normalized));
      final claims = jsonDecode(payload) as Map<String, dynamic>;

      final exp = claims['exp'];
      if (exp == null) return false; // sin claim exp → no expira

      final expiry = DateTime.fromMillisecondsSinceEpoch((exp as int) * 1000);
      // Margen de 30 s para evitar race conditions en requests lentos
      return DateTime.now().isAfter(expiry.subtract(const Duration(seconds: 30)));
    } catch (_) {
      return false; // no se puede parsear → dejar pasar, el backend devolverá 401
    }
  }
}
