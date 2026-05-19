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
  final List<DiscountEntity> allDiscounts;
  final Set<int> selectedIds;
  final bool saving;

  const BankProfileLoaded({
    required this.allDiscounts,
    required this.selectedIds,
    this.saving = false,
  });

  List<DiscountEntity> get selectedDiscounts =>
      allDiscounts.where((d) => selectedIds.contains(d.id)).toList();

  List<int> get selectedDiscountIds => selectedIds.toList();

  List<DiscountEntity> discountsForStation(int brandId, int dayOfWeek) =>
      allDiscounts
          .where((d) =>
              selectedIds.contains(d.id) &&
              d.brandId == brandId &&
              (d.dayOfWeek == null || d.dayOfWeek == dayOfWeek))
          .toList();

  bool isSelected(int discountId) => selectedIds.contains(discountId);

  @override
  List<Object?> get props => [allDiscounts, selectedIds, saving];

  BankProfileLoaded copyWith({
    List<DiscountEntity>? allDiscounts,
    Set<int>? selectedIds,
    bool? saving,
  }) =>
      BankProfileLoaded(
        allDiscounts: allDiscounts ?? this.allDiscounts,
        selectedIds: selectedIds ?? this.selectedIds,
        saving: saving ?? this.saving,
      );
}

class BankProfileError extends BankProfileState {
  final String message;
  const BankProfileError(this.message);
  @override
  List<Object?> get props => [message];
}
