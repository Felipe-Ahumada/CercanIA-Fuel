import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/utils/brand_colors.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_page_header.dart';
import '../widgets/register_visit_bottom_sheet.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../injection_container.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';
import '../blocs/station_detail/station_detail_cubit.dart';
import '../blocs/station_detail/station_detail_state.dart';

class StationDetailPage extends StatelessWidget {
  final String stationId;

  const StationDetailPage({super.key, required this.stationId});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => sl<StationDetailCubit>()..load(stationId),
      child: const _StationDetailView(),
    );
  }
}

class _StationDetailView extends StatelessWidget {
  const _StationDetailView();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: BlocBuilder<StationDetailCubit, StationDetailState>(
        builder: (context, state) {
          if (state is StationDetailLoading || state is StationDetailInitial) {
            return const GlassLoadingIndicator();
          }
          if (state is StationDetailError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(32),
                child: GlassCard(
                  radius: GlassTokens.radiusLg,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.error_outline,
                          size: 48, color: GlassTokens.red),
                      const SizedBox(height: 12),
                      Text(
                        state.message,
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                            fontSize: 13, color: GlassTokens.text1),
                      ),
                      const SizedBox(height: 16),
                      GestureDetector(
                        onTap: () => context
                            .read<StationDetailCubit>()
                            .load(context
                                .findAncestorWidgetOfExactType<
                                    StationDetailPage>()!
                                .stationId),
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 20, vertical: 10),
                          decoration: BoxDecoration(
                            gradient: GlassTokens.accentGradient,
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: const Text(
                            'Reintentar',
                            style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.w700),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          }
          if (state is StationDetailLoaded) {
            return _StationDetailContent(station: state.station);
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}

class _StationDetailContent extends StatelessWidget {
  final StationEntity station;

  const _StationDetailContent({required this.station});

  static const _fuelColors = {
    Fuel.gasoline93: GlassTokens.green,
    Fuel.gasoline95: GlassTokens.cyan,
    Fuel.gasoline97: GlassTokens.purple,
    Fuel.diesel: GlassTokens.orange,
    Fuel.naturalGas: GlassTokens.yellow,
  };

  @override
  Widget build(BuildContext context) {
    final priceFormatter = NumberFormat.currency(
      locale: 'es_CL',
      symbol: '\$',
      decimalDigits: 0,
    );

    final syncText = station.lastSync != null
        ? DateFormat('dd/MM/yyyy HH:mm').format(station.lastSync!)
        : 'No disponible';

    final brandColor = BrandColors.of(station.brand);
    final initials = station.brand.length >= 2
        ? station.brand.substring(0, 2).toUpperCase()
        : station.brand.toUpperCase();

    return Column(
      children: [
        SafeArea(
          bottom: false,
          child: GlassPageHeader(
            title: station.brand,
            subtitle: station.name,
          ),
        ),
        // Scrollable content
        Expanded(
          child: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 32),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Station identity card
                GlassCard(
                  radius: GlassTokens.radiusLg,
                  level: 1,
                  child: Row(
                    children: [
                      Container(
                        width: 52,
                        height: 52,
                        decoration: BoxDecoration(
                          color: brandColor.withValues(alpha: 0.12),
                          borderRadius: BorderRadius.circular(14),
                          border: Border.all(
                              color: brandColor.withValues(alpha: 0.25)),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          initials,
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w800,
                            color: brandColor,
                          ),
                        ),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              station.name,
                              style: const TextStyle(
                                fontSize: 15,
                                fontWeight: FontWeight.w700,
                                color: GlassTokens.text0,
                              ),
                            ),
                            const SizedBox(height: 2),
                            Text(
                              station.brand,
                              style: const TextStyle(
                                  fontSize: 12, color: GlassTokens.text2),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                const Text(
                  'PRECIOS ACTUALES',
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.w700,
                    letterSpacing: 0.5,
                    color: GlassTokens.text2,
                  ),
                ),
                const SizedBox(height: 8),
                if (station.prices.isEmpty)
                  const GlassCard(
                    radius: GlassTokens.radiusMd,
                    level: 1,
                    child: Center(
                      child: Text(
                        'Sin precios disponibles',
                        style: TextStyle(
                            fontSize: 13, color: GlassTokens.text2),
                      ),
                    ),
                  )
                else
                  BlocBuilder<BankProfileCubit, BankProfileState>(
                    builder: (context, bankState) {
                      final discounts = bankState is BankProfileLoaded &&
                              station.brandId != null
                          ? bankState.discountsForStation(
                              station.brandId!, DateTime.now().weekday)
                          : <DiscountEntity>[];

                      return Column(
                        children: station.prices.entries.map((entry) {
                          final color =
                              _fuelColors[entry.key] ?? GlassTokens.text1;
                          final unitPrice = entry.value;

                          // Mejor descuento aplicable a este combustible
                          DiscountEntity? bestDiscount;
                          double bestSaving = 0;
                          for (final d in discounts) {
                            if (d.fuelTypeName != null &&
                                !entry.key.displayName
                                    .toLowerCase()
                                    .contains(
                                        d.fuelTypeName!.toLowerCase())) {
                              continue;
                            }
                            final saving =
                                d.discountType == 'FIXED_PER_LITER'
                                    ? d.discountValue
                                    : d.discountType == 'PERCENTAGE'
                                        ? unitPrice * d.discountValue / 100
                                        : d.discountValue;
                            if (saving > bestSaving) {
                              bestSaving = saving;
                              bestDiscount = d;
                            }
                          }
                          final hasDiscount =
                              bestDiscount != null && bestSaving > 0;
                          final discountedPrice =
                              (unitPrice - bestSaving).clamp(0, double.infinity);

                          return Padding(
                            padding: const EdgeInsets.only(bottom: 8),
                            child: GlassCard(
                              radius: GlassTokens.radiusMd,
                              level: 1,
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 14, vertical: 12),
                              child: Row(
                                children: [
                                  Container(
                                    width: 36,
                                    height: 36,
                                    decoration: BoxDecoration(
                                      color: color.withValues(alpha: 0.10),
                                      borderRadius:
                                          BorderRadius.circular(10),
                                      border: Border.all(
                                          color:
                                              color.withValues(alpha: 0.20)),
                                    ),
                                    child: Icon(Icons.oil_barrel,
                                        color: color, size: 18),
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          entry.key.displayName,
                                          style: const TextStyle(
                                            fontSize: 14,
                                            fontWeight: FontWeight.w600,
                                            color: GlassTokens.text0,
                                          ),
                                        ),
                                        if (hasDiscount) ...[
                                          const SizedBox(height: 4),
                                          Row(
                                            children: [
                                              Container(
                                                padding:
                                                    const EdgeInsets.symmetric(
                                                        horizontal: 7,
                                                        vertical: 2),
                                                decoration: BoxDecoration(
                                                  color: GlassTokens.green
                                                      .withValues(alpha: 0.10),
                                                  borderRadius:
                                                      BorderRadius.circular(
                                                          20),
                                                  border: Border.all(
                                                      color: GlassTokens.green
                                                          .withValues(
                                                              alpha: 0.25)),
                                                ),
                                                child: Text(
                                                  'Con descuento: ${priceFormatter.format(discountedPrice)}/L',
                                                  style: const TextStyle(
                                                    fontSize: 10,
                                                    fontWeight: FontWeight.w700,
                                                    color: GlassTokens.green,
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ],
                                      ],
                                    ),
                                  ),
                                  Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.end,
                                    children: [
                                      Text(
                                        priceFormatter.format(unitPrice),
                                        style: TextStyle(
                                          fontSize: 18,
                                          fontWeight: FontWeight.w800,
                                          color: hasDiscount
                                              ? GlassTokens.text2
                                              : color,
                                          decoration: hasDiscount
                                              ? TextDecoration.lineThrough
                                              : null,
                                          decorationColor: GlassTokens.text2,
                                        ),
                                      ),
                                      if (hasDiscount)
                                        Text(
                                          priceFormatter
                                              .format(discountedPrice),
                                          style: const TextStyle(
                                            fontSize: 18,
                                            fontWeight: FontWeight.w800,
                                            color: GlassTokens.green,
                                          ),
                                        ),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                          );
                        }).toList(),
                      );
                    },
                  ),
                const SizedBox(height: 20),
                GlassButton(
                  label: 'Registrar carga',
                  width: double.infinity,
                  onPressed: () => RegisterVisitBottomSheet.show(context, station),
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    const Icon(Icons.sync, size: 14, color: GlassTokens.text2),
                    const SizedBox(width: 6),
                    Text(
                      'Última sincronización CNE: $syncText',
                      style: const TextStyle(
                          fontSize: 11, color: GlassTokens.text2),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
