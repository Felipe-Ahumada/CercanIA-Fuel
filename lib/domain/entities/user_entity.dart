import 'package:equatable/equatable.dart';

class UserEntity extends Equatable {
  final String uid;
  final String email;
  final String? nombre;
  final String? fotoUrl;

  const UserEntity({
    required this.uid,
    required this.email,
    this.nombre,
    this.fotoUrl,
  });

  @override
  List<Object?> get props => [uid, email, nombre, fotoUrl];
}
