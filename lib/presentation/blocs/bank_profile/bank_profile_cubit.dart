import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../../domain/usecases/bank_profile_usecases.dart';
import 'bank_profile_state.dart';

class BankProfileCubit extends Cubit<BankProfileState> {
  final GetDiscountsCatalogUseCase _getCatalog;
  final GetSelectedDiscountsUseCase _getSelected;
  final UpdateSelectedDiscountsUseCase _updateSelected;

  BankProfileCubit({
    required GetDiscountsCatalogUseCase getCatalogUseCase,
    required GetSelectedDiscountsUseCase getSelectedUseCase,
    required UpdateSelectedDiscountsUseCase updateSelectedUseCase,
  })  : _getCatalog = getCatalogUseCase,
        _getSelected = getSelectedUseCase,
        _updateSelected = updateSelectedUseCase,
        super(BankProfileInitial());

  Future<void> load() async {
    emit(BankProfileLoading());

    final catalogResult = await _getCatalog();
    final selectedResult = await _getSelected();

    final catalog = catalogResult.getOrElse(() => []);
    final selected = selectedResult.getOrElse(() => []);
    final selectedIds = selected.map((d) => d.id).toSet();

    emit(BankProfileLoaded(allDiscounts: catalog, selectedIds: selectedIds));
  }

  Future<void> toggleDiscount(int discountId) async {
    final s = state;
    if (s is! BankProfileLoaded) return;

    final newIds = Set<int>.from(s.selectedIds);
    if (newIds.contains(discountId)) {
      newIds.remove(discountId);
    } else {
      newIds.add(discountId);
    }

    // Optimistic update
    emit(s.copyWith(selectedIds: newIds, saving: true));

    final result = await _updateSelected(newIds.toList());
    result.fold(
      (failure) {
        // Revert on error
        emit(s.copyWith(saving: false));
        emit(BankProfileError(failure.message));
        emit(s);
      },
      (_) => emit((state as BankProfileLoaded).copyWith(saving: false)),
    );
  }
}
