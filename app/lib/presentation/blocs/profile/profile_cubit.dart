import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../domain/usecases/profile_usecases.dart';
import 'profile_state.dart';

class ProfileCubit extends Cubit<ProfileState> {
  final GetUserProfileUseCase getUserProfileUseCase;
  final UpdateUserProfileUseCase updateUserProfileUseCase;

  ProfileCubit({
    required this.getUserProfileUseCase,
    required this.updateUserProfileUseCase,
  }) : super(ProfileInitial());

  Future<void> fetchProfile() async {
    emit(ProfileLoading());
    final result = await getUserProfileUseCase();
    result.fold(
      (failure) => emit(ProfileError(failure.message)),
      (user) => emit(ProfileLoaded(user)),
    );
  }

  Future<void> updateProfile({
    required String firstName,
    String? middleName,
    required String lastName,
    String? secondLastName,
  }) async {
    emit(ProfileLoading());
    final result = await updateUserProfileUseCase(
      firstName: firstName,
      middleName: middleName,
      lastName: lastName,
      secondLastName: secondLastName,
    );
    result.fold(
      (failure) => emit(ProfileError(failure.message)),
      (user) => emit(ProfileLoaded(user)),
    );
  }
}
