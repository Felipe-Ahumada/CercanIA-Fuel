import 'dart:io';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../core/theme/glass_tokens.dart';
import '../../core/utils/price_calculator.dart';
import '../../core/widgets/brand_logo.dart';
import '../../core/widgets/glass_button.dart';
import '../../core/widgets/glass_card.dart';
import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../blocs/bank_profile/bank_profile_cubit.dart';
import '../blocs/bank_profile/bank_profile_state.dart';
import 'register_visit_bottom_sheet.dart';

const _relevantFuels = [
  Fuel.gasoline93,
  Fuel.gasoline95,
  Fuel.gasoline97,
  Fuel.diesel,
];

const _fuelColors = {
  Fuel.gasoline93: GlassTokens.green,
  Fuel.gasoline95: GlassTokens.cyan,
  Fuel.gasoline97: GlassTokens.purple,
  Fuel.diesel: GlassTokens.orange,
};

Future<void> _openDirections(double lat, double lng, BuildContext ctx) async {
  final candidates = Platform.isIOS
      ? [
          'comgooglemaps://?daddr=$lat,$lng&directionsmode=driving',
          'maps://?daddr=$lat,$lng',
          'https://maps.apple.com/?daddr=$lat,$lng',
        ]
      : [
          'geo:$lat,$lng?q=$lat,$lng',
          'https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=driving',
        ];

  for (final url in candidates) {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
      return;
    }
  }

  if (ctx.mounted) {
    ScaffoldMessenger.of(ctx).showSnackBar(
      const SnackBar(content: Text('No se pudo abrir la app de navegación')),
    );
  }
}

String _fmt(double price) {
  final p = price.round();
  if (p >= 1000) return '\$${p ~/ 1000}.${(p % 1000).toString().padLeft(3, '0')}';
  return '\$$p';
}

class StationBottomSheet extends StatelessWidget {
  final StationEntity station;
  final Fuel? selectedFuel;

  const StationBottomSheet({
    super.key,
    required this.station,
    required this.selectedFuel,
  });

  @override
  Widget build(BuildContext context) {
    final availableFuels = _relevantFuels
        .where((f) => station.prices.containsKey(f))
        .toList();

    // Get user's applicable discounts for this station
    final bankState = context.read<BankProfileCubit>().state;
    final dayOfWeek = DateTime.now().weekday;
    List<DiscountEntity> stationDiscounts = [];
    if (bankState is BankProfileLoaded && station.brandId != null) {
      stationDiscounts = bankState.discountsForStation(
        station.brandId!,
        dayOfWeek,
      );
    }

    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(26)),
      child: BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 40, sigmaY: 40),
        child: Container(
          padding: const EdgeInsets.fromLTRB(20, 12, 20, 38),
          decoration: const BoxDecoration(
            color: GlassTokens.glass3,
            borderRadius: BorderRadius.vertical(top: Radius.circular(26)),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 36,
                  height: 4,
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: GlassTokens.border2,
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              // Station identity
              Row(
                children: [
                  BrandLogo(marca: station.brand, size: 44),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          station.brand,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w800,
                            color: GlassTokens.text0,
                          ),
                        ),
                        if (station.address != null)
                          Text(
                            station.address!,
                            style: const TextStyle(fontSize: 12, color: GlassTokens.text2),
                            overflow: TextOverflow.ellipsis,
                          )
                        else
                          Text(
                            station.brand,
                            style: const TextStyle(fontSize: 12, color: GlassTokens.text2),
                          ),
                      ],
                    ),
                  ),
                ],
              ),
              if (availableFuels.isNotEmpty) ...[
                const SizedBox(height: 14),
                for (int i = 0; i < availableFuels.length; i += 2) ...[
                  Row(
                    children: [
                      Expanded(
                        child: _PriceCell(
                          fuel: availableFuels[i],
                          station: station,
                          isActive: availableFuels[i] == selectedFuel,
                          discounts: stationDiscounts,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: i + 1 < availableFuels.length
                            ? _PriceCell(
                                fuel: availableFuels[i + 1],
                                station: station,
                                isActive: availableFuels[i + 1] == selectedFuel,
                                discounts: stationDiscounts,
                              )
                            : const SizedBox.shrink(),
                      ),
                    ],
                  ),
                  if (i + 2 < availableFuels.length) const SizedBox(height: 8),
                ],
              ] else ...[
                const SizedBox(height: 12),
                const Text(
                  'Sin precios disponibles',
                  style: TextStyle(fontSize: 13, color: GlassTokens.text2),
                ),
              ],
              // Other available discounts section
              if (stationDiscounts.isNotEmpty) ...[
                const SizedBox(height: 14),
                _DiscountsSection(discounts: stationDiscounts),
              ],
              const SizedBox(height: 16),
              // Fix #8 — CTA primario
              GlassButton(
                label: 'Registrar carga',
                width: double.infinity,
                onPressed: () {
                  Navigator.of(context, rootNavigator: true).pop();
                  RegisterVisitBottomSheet.show(context, station);
                },
              ),
              const SizedBox(height: 10),
              // Fix #4 — CTA secundario ghost
              GlassButtonSecondary(
                label: 'Cómo llegar',
                icon: Icons.directions_outlined,
                width: double.infinity,
                onPressed: () => _openDirections(station.lat, station.lng, context),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _PriceCell extends StatelessWidget {
  final Fuel fuel;
  final StationEntity station;
  final bool isActive;
  final List<DiscountEntity> discounts;

  const _PriceCell({
    required this.fuel,
    required this.station,
    required this.isActive,
    required this.discounts,
  });

  @override
  Widget build(BuildContext context) {
    final color = _fuelColors[fuel]!;
    final resolved = PriceCalculator.resolveWithDiscount(
      station,
      fuel,
      discounts,
    );
    final hasDiscount = resolved.hasDiscount;

    return GlassCard(
      radius: GlassTokens.radiusMd,
      level: isActive ? 2 : 0,
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
      child: Row(
        children: [
          // Fix #6 — barra lateral coloreada en lugar de punto 8x8
          Container(
            width: 3,
            height: 34,
            decoration: BoxDecoration(
              color: color,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  fuel.displayName,
                  style: TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.w700,
                    color: color,
                  ),
                ),
                if (hasDiscount) ...[
                  // Original price struck through
                  Text(
                    _fmt(resolved.basePrice),
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w500,
                      color: GlassTokens.text2,
                      decoration: TextDecoration.lineThrough,
                      decorationColor: GlassTokens.text2,
                    ),
                  ),
                  // Discounted price
                  Text(
                    _fmt(resolved.displayPrice),
                    style: const TextStyle(
                      fontSize: 17,
                      fontWeight: FontWeight.w800,
                      color: Color(0xFF059669),
                    ),
                  ),
                ] else
                  Text(
                    _fmt(resolved.basePrice),
                    style: const TextStyle(
                      fontSize: 17,
                      fontWeight: FontWeight.w800,
                      color: GlassTokens.text0,
                    ),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// Shows the user's applicable discounts for this station as compact chips.
class _DiscountsSection extends StatelessWidget {
  final List<DiscountEntity> discounts;

  const _DiscountsSection({required this.discounts});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(
              Icons.discount_outlined,
              size: 14,
              color: Color(0xFF059669),
            ),
            const SizedBox(width: 5),
            Text(
              discounts.length == 1
                  ? 'Tu descuento activo'
                  : 'Tus descuentos activos',
              style: const TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w700,
                color: Color(0xFF059669),
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Wrap(
          spacing: 6,
          runSpacing: 6,
          children: discounts.map((d) => _DiscountChip(discount: d)).toList(),
        ),
      ],
    );
  }
}

class _DiscountChip extends StatelessWidget {
  final DiscountEntity discount;

  const _DiscountChip({required this.discount});

  @override
  Widget build(BuildContext context) {
    final label = discount.cardProductName.isNotEmpty
        ? '${discount.cardProductName} · ${discount.valueLabel}'
        : discount.valueLabel;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: const Color(0xFFECFDF5),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0x3010B981)),
      ),
      child: Text(
        label,
        style: const TextStyle(
          fontSize: 11,
          fontWeight: FontWeight.w600,
          color: Color(0xFF059669),
        ),
      ),
    );
  }
}
