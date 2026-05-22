import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../domain/entities/user_entity.dart';
import '../../../domain/usecases/auth_usecases.dart';

// ── Persistence key ───────────────────────────────────────────────────────────

const _kProfileComplete = 'profile_complete';

String _prefKey(String uid) => '${_kProfileComplete}_$uid';

// ── Events ────────────────────────────────────────────────────────────────────

abstract class AuthEvent extends Equatable {
  const AuthEvent();
  @override
  List<Object?> get props => [];
}

class AuthCheckRequested extends AuthEvent {}

class AuthSignInRequested extends AuthEvent {
  final String email;
  final String password;
  const AuthSignInRequested(this.email, this.password);
  @override
  List<Object?> get props => [email, password];
}

class AuthSignUpRequested extends AuthEvent {
  final String email;
  final String password;
  final String firstName;
  final String? middleName;
  final String lastName;
  final String secondLastName;
  final String rut;
  final DateTime birthDate;

  const AuthSignUpRequested({
    required this.email,
    required this.password,
    required this.firstName,
    this.middleName,
    required this.lastName,
    required this.secondLastName,
    required this.rut,
    required this.birthDate,
  });

  @override
  List<Object?> get props =>
      [email, password, firstName, middleName, lastName, secondLastName, rut, birthDate];
}

class AuthGoogleSignInRequested extends AuthEvent {}

class AuthCompleteProfileRequested extends AuthEvent {
  final String rut;
  final String? middleName;
  final String secondLastName;
  final DateTime birthDate;

  const AuthCompleteProfileRequested({
    required this.rut,
    this.middleName,
    required this.secondLastName,
    required this.birthDate,
  });

  @override
  List<Object?> get props => [rut, middleName, secondLastName, birthDate];
}

class AuthSignOutRequested extends AuthEvent {}

// ── States ────────────────────────────────────────────────────────────────────

abstract class AuthState extends Equatable {
  const AuthState();
  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class AuthAuthenticated extends AuthState {
  final UserEntity user;
  final bool fromOnboarding;
  const AuthAuthenticated(this.user, {this.fromOnboarding = false});
  @override
  List<Object?> get props => [user, fromOnboarding];
}

class AuthNeedsProfileCompletion extends AuthState {
  final String email;
  final String firstName;
  final String lastName;
  final String? error;

  const AuthNeedsProfileCompletion({
    required this.email,
    required this.firstName,
    required this.lastName,
    this.error,
  });

  AuthNeedsProfileCompletion copyWithError(String err) =>
      AuthNeedsProfileCompletion(
        email: email,
        firstName: firstName,
        lastName: lastName,
        error: err,
      );

  @override
  List<Object?> get props => [email, firstName, lastName, error];
}

class AuthUnauthenticated extends AuthState {}

class AuthError extends AuthState {
  final String message;
  const AuthError(this.message);
  @override
  List<Object?> get props => [message];
}

// ── BLoC ──────────────────────────────────────────────────────────────────────

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final SignInUseCase signInUseCase;
  final SignUpUseCase signUpUseCase;
  final SignInWithGoogleUseCase signInWithGoogleUseCase;
  final CompleteProfileUseCase completeProfileUseCase;
  final SignOutUseCase signOutUseCase;
  final GetCurrentUserUseCase getCurrentUserUseCase;

  AuthBloc({
    required this.signInUseCase,
    required this.signUpUseCase,
    required this.signInWithGoogleUseCase,
    required this.completeProfileUseCase,
    required this.signOutUseCase,
    required this.getCurrentUserUseCase,
  }) : super(AuthInitial()) {
    on<AuthCheckRequested>(_onCheckRequested);
    on<AuthSignInRequested>(_onSignInRequested);
    on<AuthSignUpRequested>(_onSignUpRequested);
    on<AuthGoogleSignInRequested>(_onGoogleSignInRequested);
    on<AuthCompleteProfileRequested>(_onCompleteProfileRequested);
    on<AuthSignOutRequested>(_onSignOutRequested);
  }

  // ── Handlers ──────────────────────────────────────────────────────────────

  Future<void> _onCheckRequested(
      AuthCheckRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    final result = await getCurrentUserUseCase();
    await result.fold(
      (_) async => emit(AuthUnauthenticated()),
      (user) async {
        if (user.authProvider == 'FIREBASE' && user.backendId == null) {
          // Clear any stale profile_complete flag so the router also stays on
          // /complete_profile (handles the case where flag exists from a prior
          // session but the backend user was deleted).
          final prefs = await SharedPreferences.getInstance();
          await prefs.remove(_prefKey(user.uid));
          emit(_needsCompletionFromUser(user));
          return;
        }
        // FIREBASE user with valid backend account: ensure the flag is set
        // (handles new-device installs where SharedPreferences was wiped).
        if (user.authProvider == 'FIREBASE') {
          await _markProfileComplete(user.uid);
        }
        emit(AuthAuthenticated(user));
      },
    );
  }

  Future<void> _onSignInRequested(
      AuthSignInRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    final result = await signInUseCase(event.email, event.password);
    await result.fold(
      (failure) async => emit(AuthError(failure.message)),
      (user) async {
        // signIn = credentials verified = profile is complete
        await _markProfileComplete(user.uid);
        emit(AuthAuthenticated(user));
      },
    );
  }

  Future<void> _onSignUpRequested(
      AuthSignUpRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    final result = await signUpUseCase(
      email: event.email,
      password: event.password,
      firstName: event.firstName,
      middleName: event.middleName,
      lastName: event.lastName,
      secondLastName: event.secondLastName,
      rut: event.rut,
      birthDate: event.birthDate,
    );
    await result.fold(
      (failure) async => emit(AuthError(failure.message)),
      (user) async {
        await _markProfileComplete(user.uid);
        emit(AuthAuthenticated(user));
      },
    );
  }

  Future<void> _onGoogleSignInRequested(
      AuthGoogleSignInRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    final result = await signInWithGoogleUseCase();
    await result.fold(
      (failure) async => emit(AuthError(failure.message)),
      (user) async {
        if (user.backendId == null) {
          // No backend account (new user, or stale flag from a deleted account).
          // Clear any stale profile_complete flag so the router stays on
          // /complete_profile instead of bouncing back to /home/map.
          final prefs = await SharedPreferences.getInstance();
          await prefs.remove(_prefKey(user.uid));
          emit(_needsCompletionFromUser(user));
        } else {
          // Backend account exists — mark complete and authenticate.
          // This also covers new-device installs where the flag was wiped.
          await _markProfileComplete(user.uid);
          emit(AuthAuthenticated(user));
        }
      },
    );
  }

  Future<void> _onCompleteProfileRequested(
      AuthCompleteProfileRequested event, Emitter<AuthState> emit) async {
    if (state is! AuthNeedsProfileCompletion) return;
    final profileState = state as AuthNeedsProfileCompletion;

    emit(AuthLoading());
    final result = await completeProfileUseCase(
      email: profileState.email,
      firstName: profileState.firstName,
      middleName: event.middleName,
      lastName: profileState.lastName,
      secondLastName: event.secondLastName,
      rut: event.rut,
      birthDate: event.birthDate,
    );
    await result.fold(
      (failure) async => emit(profileState.copyWithError(failure.message)),
      (user) async {
        await _markProfileComplete(user.uid);
        emit(AuthAuthenticated(user, fromOnboarding: true));
      },
    );
  }

  Future<void> _onSignOutRequested(
      AuthSignOutRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    final result = await signOutUseCase();
    await result.fold(
      (_) async => emit(const AuthError('Error al cerrar sesión')),
      (_) async => emit(AuthUnauthenticated()),
    );
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  AuthNeedsProfileCompletion _needsCompletionFromUser(UserEntity user) {
    final parts = (user.name ?? '').trim().split(' ');
    return AuthNeedsProfileCompletion(
      email: user.email,
      firstName: parts.isNotEmpty ? parts[0] : '',
      lastName: parts.length > 1 ? parts[1] : '',
    );
  }

  Future<void> _markProfileComplete(String uid) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_prefKey(uid), true);
  }
}
