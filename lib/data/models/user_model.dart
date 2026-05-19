import 'package:firebase_auth/firebase_auth.dart';
import '../../../domain/entities/user_entity.dart';

class UserModel extends UserEntity {
  final bool isNewGoogleUser;

  const UserModel({
    required super.uid,
    required super.email,
    super.backendId,
    super.name,
    super.firstName,
    super.middleName,
    super.lastName,
    super.secondLastName,
    super.photoUrl,
    super.role,
    super.authProvider,
    super.rut,
    super.birthDate,
    this.isNewGoogleUser = false,
  });

  factory UserModel.fromFirebaseUser(
    User user, {
    bool isNewGoogleUser = false,
    String? backendId,
    String? role,
  }) {
    return UserModel(
      uid: user.uid,
      email: user.email ?? '',
      backendId: backendId,
      name: user.displayName,
      photoUrl: user.photoURL,
      role: role,
      authProvider: 'FIREBASE',
      isNewGoogleUser: isNewGoogleUser,
    );
  }

  factory UserModel.fromLocalAuth(Map<String, dynamic> data) {
    final userId = data['userId'].toString();
    return UserModel(
      uid: userId,
      email: data['email'] as String,
      backendId: userId,
      name: data['name'] as String?,
      role: data['role'] as String?,
      authProvider: 'LOCAL',
    );
  }

  factory UserModel.fromUserResponse(
    Map<String, dynamic> data, {
    required String uid,
    required String authProvider,
    String? photoUrl,
    bool isNewGoogleUser = false,
  }) {
    final firstName = data['firstName'] as String?;
    final middleName = data['middleName'] as String?;
    final lastName = data['lastName'] as String?;
    final secondLastName = data['secondLastName'] as String?;
    final parts = [firstName, middleName, lastName, secondLastName]
        .where((s) => s != null && s.isNotEmpty)
        .toList();
    final fullName = parts.isNotEmpty ? parts.join(' ') : null;

    DateTime? birthDate;
    final bd = data['birthDate'];
    if (bd != null) {
      try {
        birthDate = DateTime.parse(bd as String);
      } catch (_) {}
    }

    return UserModel(
      uid: uid,
      email: data['email'] as String,
      backendId: data['id']?.toString(),
      name: fullName,
      firstName: firstName,
      middleName: middleName,
      lastName: lastName,
      secondLastName: secondLastName,
      photoUrl: photoUrl,
      role: data['roleName'] as String?,
      authProvider: authProvider,
      rut: data['rut'] as String?,
      birthDate: birthDate,
    );
  }

  UserModel withBackendId(String id) => UserModel(
    uid: uid,
    email: email,
    backendId: id,
    name: name,
    firstName: firstName,
    middleName: middleName,
    lastName: lastName,
    secondLastName: secondLastName,
    photoUrl: photoUrl,
    role: role,
    authProvider: authProvider,
    rut: rut,
    birthDate: birthDate,
    isNewGoogleUser: isNewGoogleUser,
  );
}
