import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';
import '../../models/user_model.dart';

abstract class AuthRemoteDataSource {
  Future<UserModel> signIn(String email, String password);
  Future<UserModel> signUp({
    required String email,
    required String password,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  });
  Future<UserModel> signInWithGoogle();
  Future<UserModel> completeProfile({
    required String email,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  });
  Future<void> signOut();
  Future<void> sendPasswordResetEmail(String email);
  Future<UserModel> getCurrentUser();
  Future<UserModel> fetchFullProfile(String userId);
  Future<UserModel> updateProfile({
    required String firstName,
    String? middleName,
    required String lastName,
    String? secondLastName,
  });
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
  final FirebaseAuth firebaseAuth;
  final GoogleSignIn googleSignIn;
  final DioClient dioClient;

  static final _dateFmt = DateFormat('yyyy-MM-dd');

  static const _kAuthProvider = 'auth_provider';
  static const _kLocalJwt = 'local_jwt';
  static const _kLocalUserId = 'local_user_id';
  static const _kLocalUserEmail = 'local_user_email';
  static const _kLocalUserRole = 'local_user_role';
  static const _kLocalUserName = 'local_user_name';

  AuthRemoteDataSourceImpl(this.firebaseAuth, this.googleSignIn, this.dioClient);

  // ── Local auth ───────────────────────────────────────────────────────────────

  @override
  Future<UserModel> signIn(String email, String password) async {
    try {
      final response = await dioClient.post('/auth/login', data: {
        'email': email,
        'password': password,
      });
      final data = response.data as Map<String, dynamic>;
      await _persistLocalSession(data);
      return UserModel.fromLocalAuth(data);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<UserModel> signUp({
    required String email,
    required String password,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) async {
    try {
      final response = await dioClient.post('/auth/register', data: {
        'email': email,
        'password': password,
        'firstName': firstName,
        if (middleName != null && middleName.isNotEmpty) 'middleName': middleName,
        'lastName': lastName,
        'secondLastName': secondLastName,
        'rut': rut.trim(),
        'birthDate': _dateFmt.format(birthDate),
      });
      final data = response.data as Map<String, dynamic>;
      await _persistLocalSession(data);
      return UserModel.fromLocalAuth(data);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  // ── Google auth ──────────────────────────────────────────────────────────────

  @override
  Future<UserModel> signInWithGoogle() async {
    try {
      final googleUser = await googleSignIn.signIn();
      if (googleUser == null) {
        throw ServerException(message: 'Inicio de sesión cancelado');
      }

      final googleAuth = await googleUser.authentication;
      final credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      final userCredential = await firebaseAuth.signInWithCredential(credential);
      if (userCredential.user == null) {
        throw ServerException(message: 'Usuario nulo tras login con Google');
      }

      final isNew = userCredential.additionalUserInfo?.isNewUser ?? false;
      final me = isNew ? (userId: null, role: null) : await _fetchBackendMe();

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_kAuthProvider, 'FIREBASE');

      return UserModel.fromFirebaseUser(
        userCredential.user!,
        isNewGoogleUser: isNew,
        backendId: me.userId,
        role: me.role,
      );
    } catch (e) {
      final msg = e.toString();
      if (msg.contains('network-request-failed') || msg.contains('network_error')) {
        throw ServerException(
          message: 'Sin conexión a los servidores de Google. '
              'Verifica el internet del emulador o usa el registro con email y contraseña.');
      }
      throw ServerException(message: msg);
    }
  }

  @override
  Future<UserModel> completeProfile({
    required String email,
    required String firstName,
    String? middleName,
    required String lastName,
    required String secondLastName,
    required String rut,
    required DateTime birthDate,
  }) async {
    final payload = {
      'email': email,
      'firstName': firstName,
      if (middleName != null && middleName.isNotEmpty) 'middleName': middleName,
      'lastName': lastName,
      'secondLastName': secondLastName,
      'rut': rut.trim(),
      'birthDate': _dateFmt.format(birthDate),
      'roleId': 2,
    };
    try {
      await dioClient.post('/usuarios', data: payload);
    } catch (e) {
      if (e is DioException && e.response?.statusCode == 409) {
        try {
          await dioClient.patch('/usuarios/complete-profile', data: payload);
        } catch (patchErr) {
          throw ServerException(message: patchErr.toString());
        }
      } else {
        throw ServerException(message: e.toString());
      }
    }
    final user = firebaseAuth.currentUser;
    if (user == null) throw ServerException(message: 'No hay usuario autenticado');
    final me = await _fetchBackendMe();
    return UserModel.fromFirebaseUser(user, backendId: me.userId, role: me.role);
  }

  // ── Session management ───────────────────────────────────────────────────────

  @override
  Future<void> signOut() async {
    // Always sign out from Firebase/Google regardless of auth_provider.
    // GoRouter's refreshListenable listens to Firebase auth state — if Firebase
    // still has a current user, the router redirects back to home immediately.
    try { await googleSignIn.signOut(); } catch (_) {}
    try { await firebaseAuth.signOut(); } catch (_) {}

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_kAuthProvider);
    await prefs.remove(_kLocalJwt);
    await prefs.remove(_kLocalUserId);
    await prefs.remove(_kLocalUserEmail);
    await prefs.remove(_kLocalUserRole);
  }

  @override
  Future<void> sendPasswordResetEmail(String email) async {
    try {
      await firebaseAuth.sendPasswordResetEmail(email: email);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<UserModel> getCurrentUser() async {
    final prefs = await SharedPreferences.getInstance();
    final provider = prefs.getString(_kAuthProvider);

    if (provider == 'LOCAL') {
      final jwt = prefs.getString(_kLocalJwt);
      final userId = prefs.getString(_kLocalUserId);
      final email = prefs.getString(_kLocalUserEmail);
      if (jwt != null && userId != null && email != null) {
        return UserModel.fromLocalAuth({
          'token': jwt,
          'userId': userId,
          'email': email,
          'role': prefs.getString(_kLocalUserRole),
          'name': prefs.getString(_kLocalUserName),
        });
      }
      throw ServerException(message: 'No hay sesión local activa');
    }

    final user = firebaseAuth.currentUser;
    if (user != null) {
      // Fetch backendId so the BLoC can detect if this user has no backend account.
      final me = await _fetchBackendMe();
      return UserModel.fromFirebaseUser(user, backendId: me.userId, role: me.role);
    }
    throw ServerException(message: 'No hay usuario autenticado');
  }

  @override
  Future<UserModel> fetchFullProfile(String userId) async {
    try {
      final data = (await dioClient.get('/usuarios/$userId')).data as Map<String, dynamic>;
      final prefs = await SharedPreferences.getInstance();
      final provider = prefs.getString(_kAuthProvider) ?? 'LOCAL';
      final uid = provider == 'LOCAL'
          ? (prefs.getString(_kLocalUserId) ?? userId)
          : (firebaseAuth.currentUser?.uid ?? userId);
      return UserModel.fromUserResponse(
        data,
        uid: uid,
        authProvider: provider,
        photoUrl: firebaseAuth.currentUser?.photoURL,
      );
    } catch (e) {
      throw ServerException(message: 'No se pudo obtener el perfil: $e');
    }
  }

  @override
  Future<UserModel> updateProfile({
    required String firstName,
    String? middleName,
    required String lastName,
    String? secondLastName,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    final provider = prefs.getString(_kAuthProvider);

    final parts = [firstName, middleName, lastName, secondLastName]
        .where((s) => s != null && s.isNotEmpty)
        .toList();
    final fullName = parts.join(' ');

    final body = <String, dynamic>{
      'firstName': firstName,
      if (middleName != null && middleName.isNotEmpty) 'middleName': middleName,
      'lastName': lastName,
      if (secondLastName != null && secondLastName.isNotEmpty)
        'secondLastName': secondLastName,
    };

    if (provider == 'LOCAL') {
      final userId = prefs.getString(_kLocalUserId) ?? '';
      if (userId.isNotEmpty) {
        try {
          await dioClient.put('/usuarios/$userId', data: body);
        } catch (_) {}
      }
      await prefs.setString(_kLocalUserName, fullName);
      return UserModel.fromLocalAuth({
        'token': prefs.getString(_kLocalJwt) ?? '',
        'userId': userId,
        'email': prefs.getString(_kLocalUserEmail) ?? '',
        'role': prefs.getString(_kLocalUserRole),
        'name': fullName,
      });
    }

    try {
      final user = firebaseAuth.currentUser;
      if (user == null) {
        throw ServerException(message: 'No hay usuario autenticado para actualizar');
      }
      final me = await _fetchBackendMe();
      if (me.userId != null) {
        try {
          await dioClient.put('/usuarios/${me.userId}', data: body);
        } catch (_) {}
      }
      await user.updateDisplayName(fullName);
      await user.reload();
      return UserModel.fromFirebaseUser(firebaseAuth.currentUser!,
          backendId: me.userId, role: me.role);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  // ── Internals ────────────────────────────────────────────────────────────────

  Future<void> _persistLocalSession(Map<String, dynamic> data) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_kAuthProvider, 'LOCAL');
    await prefs.setString(_kLocalJwt, data['token'] as String);
    await prefs.setString(_kLocalUserId, data['userId'].toString());
    await prefs.setString(_kLocalUserEmail, data['email'] as String);
    if (data['role'] != null) {
      await prefs.setString(_kLocalUserRole, data['role'] as String);
    }
  }

  Future<({String? userId, String? role})> _fetchBackendMe() async {
    try {
      final data = (await dioClient.get('/auth/me')).data as Map<String, dynamic>;
      return (
        userId: data['userId']?.toString(),
        role: data['role']?.toString(),
      );
    } catch (_) {
      return (userId: null, role: null);
    }
  }
}
