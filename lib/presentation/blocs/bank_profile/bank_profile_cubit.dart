import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../domain/usecases/bank_profile_usecases.dart';
import '../../../../domain/entities/bank_profile_entity.dart';
import 'bank_profile_state.dart';

class BankProfileCubit extends Cubit<BankProfileState> {
  final GetBankProfileUseCase getBankProfileUseCase;
  final UpdateBankProfileUseCase updateBankProfileUseCase;
  final GetBanksCatalogUseCase getBanksCatalogUseCase;
  final GetCardTypesCatalogUseCase getCardTypesCatalogUseCase;

  BankProfileCubit({
    required this.getBankProfileUseCase,
    required this.updateBankProfileUseCase,
    required this.getBanksCatalogUseCase,
    required this.getCardTypesCatalogUseCase,
  }) : super(BankProfileInitial());

  Future<void> fetchProfileAndCatalogs() async {
    emit(BankProfileLoading());
    
    final profileResult = await getBankProfileUseCase();
    final banksResult = await getBanksCatalogUseCase();
    final cardsResult = await getCardTypesCatalogUseCase();

    String? error;
    BankProfileEntity? profile;
    List<String> banks = [];
    List<String> cards = [];

    profileResult.fold((l) => error = l.message, (r) => profile = r);
    banksResult.fold((l) => error ??= l.message, (r) => banks = r);
    cardsResult.fold((l) => error ??= l.message, (r) => cards = r);

    if (error != null && profile == null) {
      emit(BankProfileError(error!));
    } else {
      emit(BankProfileLoaded(
        profile: profile ?? BankProfileEntity(userId: '', convenios: []),
        banksCatalog: banks,
        cardsCatalog: cards,
      ));
    }
  }

  Future<void> addConvenio(BankConvenio convenio) async {
    if (state is BankProfileLoaded) {
      final currentState = state as BankProfileLoaded;
      final newConvenios = List<BankConvenio>.from(currentState.profile.convenios)..add(convenio);
      _update(currentState, newConvenios);
    }
  }

  Future<void> removeConvenio(BankConvenio convenio) async {
     if (state is BankProfileLoaded) {
      final currentState = state as BankProfileLoaded;
      final newConvenios = currentState.profile.convenios.where((c) => 
        c.banco != convenio.banco || c.tipoTarjeta != convenio.tipoTarjeta
      ).toList();
      _update(currentState, newConvenios);
    }
  }

  Future<void> _update(BankProfileLoaded currentState, List<BankConvenio> newConvenios) async {
    emit(BankProfileLoading());
    final result = await updateBankProfileUseCase(newConvenios);
    result.fold(
      (failure) {
        emit(BankProfileError(failure.message));
        emit(currentState); // Restore previous state
      },
      (profile) {
        emit(currentState.copyWith(profile: profile));
      }
    );
  }
}
