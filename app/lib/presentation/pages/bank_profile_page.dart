import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_page_header.dart';
import '../../core/widgets/glass_scaffold.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';

class BankProfilePage extends StatelessWidget {
  const BankProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider.value(
      value: context.read<BankProfileCubit>()..load(),
      child: const _BankProfileView(),
    );
  }
}

class _BankProfileView extends StatelessWidget {
  const _BankProfileView();

  // Builds: brandName → List<DiscountEntity>, sorted alphabetically.
  Map<String, List<DiscountEntity>> _group(List<DiscountEntity> discounts) {
    final result = <String, List<DiscountEntity>>{};
    for (final d in discounts) {
      result.putIfAbsent(d.brandName, () => []).add(d);
    }
    final keys = result.keys.toList()..sort();
    return {for (final k in keys) k: result[k]!};
  }

  @override
  Widget build(BuildContext context) {
    return GlassScaffold(
      useSafeArea: false,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SafeArea(
            bottom: false,
            child: GlassPageHeader(
              title: 'Mis Descuentos',
              subtitle: 'Selecciona los descuentos que tienes',
            ),
          ),
          const SizedBox(height: 16),
          Expanded(
            child: BlocConsumer<BankProfileCubit, BankProfileState>(
              listener: (context, state) {
                if (state is BankProfileError) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(state.message)),
                  );
                }
              },
              builder: (context, state) {
                if (state is BankProfileLoading || state is BankProfileInitial) {
                  return const GlassLoadingIndicator();
                }
                if (state is! BankProfileLoaded) {
                  return const Center(child: Text('Error al cargar descuentos'));
                }

                final grouped = _group(state.allDiscounts);

                if (grouped.isEmpty) {
                  return const Center(
                    child: Text(
                      'No hay descuentos disponibles',
                      style: TextStyle(color: GlassTokens.text2),
                    ),
                  );
                }

                return Stack(
                  children: [
                    ListView(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      children: [
                        for (final brandEntry in grouped.entries) ...[
                          // ── Brand (bencinera) header ──────────────────────
                          Padding(
                            padding: const EdgeInsets.only(
                                top: 20, bottom: 10, left: 4),
                            child: Row(
                              children: [
                                Container(
                                  width: 6,
                                  height: 6,
                                  decoration: const BoxDecoration(
                                    color: GlassTokens.green,
                                    shape: BoxShape.circle,
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Text(
                                  brandEntry.key.toUpperCase(),
                                  style: const TextStyle(
                                    fontSize: 11,
                                    fontWeight: FontWeight.w700,
                                    letterSpacing: 0.6,
                                    color: GlassTokens.text1,
                                  ),
                                ),
                                const SizedBox(width: 6),
                                Text(
                                  '${brandEntry.value.length}',
                                  style: const TextStyle(
                                    fontSize: 11,
                                    color: GlassTokens.text2,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          // ── Discounts for this brand ──────────────────────
                          GlassCard(
                            radius: GlassTokens.radiusLg,
                            level: 1,
                            padding: EdgeInsets.zero,
                            child: Column(
                              children: brandEntry.value.map((d) {
                                return _DiscountTile(
                                  discount: d,
                                  isSelected: state.isSelected(d.id),
                                  isLast: d == brandEntry.value.last,
                                  onTap: () => context
                                      .read<BankProfileCubit>()
                                      .toggleDiscount(d.id),
                                );
                              }).toList(),
                            ),
                          ),
                          const SizedBox(height: 8),
                        ],
                        const SizedBox(height: 32),
                      ],
                    ),
                    if (state.saving)
                      const Positioned(
                        top: 8,
                        right: 16,
                        child: SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: GlassTokens.borderAcc,
                          ),
                        ),
                      ),
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _DiscountTile extends StatelessWidget {
  final DiscountEntity discount;
  final bool isSelected;
  final bool isLast;
  final VoidCallback onTap;

  const _DiscountTile({
    required this.discount,
    required this.isSelected,
    required this.isLast,
    required this.onTap,
  });

  String get _cardLabel {
    if (discount.cardProductId == null) return 'Sin tarjeta requerida';
    final bank = discount.bankName ?? '';
    final product = discount.cardProductName;
    if (bank.isNotEmpty) return '$bank – $product';
    return product;
  }

  String get _dayLabel {
    switch (discount.dayOfWeek) {
      case 1: return 'Lunes';
      case 2: return 'Martes';
      case 3: return 'Miércoles';
      case 4: return 'Jueves';
      case 5: return 'Viernes';
      case 6: return 'Sábado';
      case 7: return 'Domingo';
      default: return 'Todos los días';
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        decoration: BoxDecoration(
          border: isLast
              ? null
              : const Border(
                  bottom: BorderSide(
                      color: GlassTokens.border1, width: 0.5),
                ),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // Discount badge
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                gradient: GlassTokens.accentGradient,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Text(
                discount.valueLabel,
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w800,
                  color: Colors.white,
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Card product / bank name (brand is now the section header)
                  Text(
                    _cardLabel,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                      color: GlassTokens.text0,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      _Chip(label: _dayLabel),
                      if (discount.fuelTypeName != null) ...[
                        const SizedBox(width: 6),
                        _Chip(
                          label: discount.fuelTypeName!,
                          color: GlassTokens.cyan,
                        ),
                      ],
                    ],
                  ),
                  if (discount.description != null &&
                      discount.description!.isNotEmpty) ...[
                    const SizedBox(height: 3),
                    Text(
                      discount.description!,
                      style: const TextStyle(
                        fontSize: 11,
                        color: GlassTokens.text2,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ],
              ),
            ),
            const SizedBox(width: 12),
            AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              width: 24,
              height: 24,
              decoration: BoxDecoration(
                gradient: isSelected ? GlassTokens.accentGradient : null,
                color: isSelected ? null : Colors.transparent,
                borderRadius: BorderRadius.circular(6),
                border: Border.all(
                  color: isSelected
                      ? GlassTokens.borderAcc
                      : GlassTokens.border2,
                  width: 1.5,
                ),
              ),
              child: isSelected
                  ? const Icon(Icons.check, size: 14, color: Colors.white)
                  : null,
            ),
          ],
        ),
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  final String label;
  final Color color;

  const _Chip({required this.label, this.color = GlassTokens.text2});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withValues(alpha: 0.3)),
      ),
      child: Text(
        label,
        style: TextStyle(
          fontSize: 10,
          fontWeight: FontWeight.w600,
          color: color,
        ),
      ),
    );
  }
}
