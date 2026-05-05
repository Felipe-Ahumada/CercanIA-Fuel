import 'package:equatable/equatable.dart';
import '../../../../domain/entities/bank_profile_entity.dart';

abstract class BankProfileState extends Equatable {
  const BankProfileState();

  @override
  List<Object?> get props => [];
}

class BankProfileInitial extends BankProfileState {}

class BankProfileLoading extends BankProfileState {}

class BankProfileLoaded extends BankProfileState {
  final BankProfileEntity profile;
  final List<String> banksCatalog;
  final List<String> cardsCatalog;

  const BankProfileLoaded({
    required this.profile,
    this.banksCatalog = const [],
    this.cardsCatalog = const [],
  });

  @override
  List<Object?> get props => [profile, banksCatalog, cardsCatalog];

  BankProfileLoaded copyWith({
    BankProfileEntity? profile,
    List<String>? banksCatalog,
    List<String>? cardsCatalog,
  }) {
    return BankProfileLoaded(
      profile: profile ?? this.profile,
      banksCatalog: banksCatalog ?? this.banksCatalog,
      cardsCatalog: cardsCatalog ?? this.cardsCatalog,
    );
  }
}

class BankProfileError extends BankProfileState {
  final String message;

  const BankProfileError(this.message);

  @override
  List<Object?> get props => [message];
}