import 'package:equatable/equatable.dart';

class UserEntity extends Equatable {
  final String uid;         // Firebase UID for Google users; backend UUID for local users
  final String? backendId;  // Backend UUID from /api/v1/auth/me → userId
  final String email;
  final String? name;
  final String? firstName;
  final String? middleName;
  final String? lastName;
  final String? secondLastName;
  final String? photoUrl;
  final String? role;       // "USER" | "ADMIN"
  final String? authProvider; // "LOCAL" | "FIREBASE"
  final String? rut;
  final DateTime? birthDate;

  const UserEntity({
    required this.uid,
    required this.email,
    this.backendId,
    this.name,
    this.firstName,
    this.middleName,
    this.lastName,
    this.secondLastName,
    this.photoUrl,
    this.role,
    this.authProvider,
    this.rut,
    this.birthDate,
  });

  bool get isAdmin => role == 'ADMIN';
  bool get isLocalAuth => authProvider == 'LOCAL';

  @override
  List<Object?> get props => [uid, backendId, email, name, firstName, middleName, lastName, secondLastName, photoUrl, role, authProvider, rut, birthDate];
}
