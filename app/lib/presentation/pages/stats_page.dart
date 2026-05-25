import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';

import '../../core/theme/glass_tokens.dart';
import '../../core/widgets/glass_card.dart';
import '../../core/widgets/glass_pill.dart';
import '../../core/widgets/glass_avatar.dart';
import '../../core/widgets/glass_loading_indicator.dart';
import '../../core/widgets/glass_kpi_card.dart';
import '../blocs/stats/stats_cubit.dart';
import '../blocs/stats/stats_state.dart';
import '../../domain/entities/savings_summary_entity.dart';
import '../../domain/entities/transaction_entity.dart';

class StatsPage extends StatelessWidget {
  const StatsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return const _StatsView();
  }
}

class _StatsView extends StatefulWidget {
  const _StatsView();

  @override
  State<_StatsView> createState() => _StatsViewState();
}

class _StatsViewState extends State<_StatsView> {
  int _tabIndex = 0;
  String? _selectedMonth; // formato 'yyyy-MM', null = todos

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: BlocBuilder<StatsCubit, StatsState>(
        builder: (context, state) {
          return CustomScrollView(
            slivers: [
              SliverToBoxAdapter(
                child: SafeArea(
                  bottom: false,
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Recargas',
                          style: TextStyle(
                            fontSize: 26,
                            fontWeight: FontWeight.w900,
                            letterSpacing: -0.5,
                            color: GlassTokens.text0,
                          ),
                        ),
                        const SizedBox(height: 16),
                        GlassCard(
                          radius: 12,
                          level: 0,
                          padding: const EdgeInsets.all(4),
                          child: Row(
                            children: [
                              _TabBtn(
                                label: 'Resumen',
                                active: _tabIndex == 0,
                                onTap: () => setState(() => _tabIndex = 0),
                              ),
                              _TabBtn(
                                label: 'Transacciones',
                                active: _tabIndex == 1,
                                onTap: () => setState(() => _tabIndex = 1),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 20),
                      ],
                    ),
                  ),
                ),
              ),
              if (state is StatsLoading || state is StatsInitial)
                const SliverFillRemaining(
                  hasScrollBody: false,
                  child: GlassLoadingIndicator(),
                )
              else if (state is StatsError)
                SliverFillRemaining(
                  child: Center(
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
                            Text(state.message,
                                style: const TextStyle(
                                    color: GlassTokens.text1)),
                            const SizedBox(height: 16),
                            ElevatedButton(
                              onPressed: () =>
                                  context.read<StatsCubit>().load(),
                              child: const Text('Reintentar'),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                )
              else if (state is StatsLoaded)
                SliverPadding(
                  padding: const EdgeInsets.fromLTRB(20, 0, 20, 100),
                  sliver: SliverToBoxAdapter(
                    child: _tabIndex == 0
                        ? _ResumenTab(summary: state.summary)
                        : Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              _MonthFilter(
                                transactions: state.transactions,
                                selected: _selectedMonth,
                                onSelected: (m) => setState(() => _selectedMonth = m),
                              ),
                              const SizedBox(height: 12),
                              _TransaccionesTab(
                                transactions: _selectedMonth == null
                                    ? state.transactions
                                    : state.transactions.where((tx) {
                                        final key =
                                            '${tx.transactionDate.year}-${tx.transactionDate.month.toString().padLeft(2, '0')}';
                                        return key == _selectedMonth;
                                      }).toList(),
                              ),
                            ],
                          ),
                  ),
                )
              else
                const SliverToBoxAdapter(child: SizedBox.shrink()),
            ],
          );
        },
      ),
    );
  }
}


class _TabBtn extends StatelessWidget {
  final String label;
  final bool active;
  final VoidCallback onTap;

  const _TabBtn({required this.label, required this.active, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            gradient: active ? GlassTokens.accentGradient : null,
            borderRadius: BorderRadius.circular(8),
          ),
          alignment: Alignment.center,
          child: Text(
            label,
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.w700,
              color: active ? GlassTokens.text0 : GlassTokens.text2,
            ),
          ),
        ),
      ),
    );
  }
}

class _ResumenTab extends StatelessWidget {
  final SavingsSummaryEntity summary;
  const _ResumenTab({required this.summary});

  @override
  Widget build(BuildContext context) {
    final fmt = NumberFormat('#,##0', 'es_CL');

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        // Hero card
        GlassCard(
          radius: GlassTokens.radiusXl,
          level: 2,
          padding: EdgeInsets.zero,
          child: Container(
            decoration: const BoxDecoration(
              gradient: GlassTokens.heroGradient,
              borderRadius: BorderRadius.all(Radius.circular(GlassTokens.radiusXl)),
            ),
            padding: const EdgeInsets.all(22),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'TOTAL DESCUENTOS APLICADOS',
                  style: TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.w700,
                    letterSpacing: 0.5,
                    color: GlassTokens.green,
                  ),
                ),
                const SizedBox(height: 6),
                Text(
                  '\$${fmt.format(summary.totalSaved)}',
                  style: const TextStyle(
                    fontSize: 48,
                    fontWeight: FontWeight.w900,
                    color: GlassTokens.green,
                    letterSpacing: -1,
                  ),
                ),
                Text(
                  '${summary.transactionCount} transacciones completadas',
                  style: const TextStyle(
                    fontSize: 13,
                    color: GlassTokens.text2,
                  ),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        // 3 KPI mini-cards
        Row(
          children: [
            GlassKpiCard(
              label: 'Litros',
              value: '${summary.totalLiters.toStringAsFixed(1)} L',
              color: GlassTokens.cyan,
              valueFontSize: 15,
              valueMaxLines: 1,
            ),
            const SizedBox(width: 8),
            GlassKpiCard(
              label: 'Ahorrado',
              value: '\$${fmt.format(summary.totalSaved)}',
              color: GlassTokens.green,
              valueFontSize: 15,
              valueMaxLines: 1,
            ),
            const SizedBox(width: 8),
            GlassKpiCard(
              label: 'Cargas',
              value: '${summary.transactionCount}',
              color: GlassTokens.purple,
              valueFontSize: 15,
              valueMaxLines: 1,
            ),
          ],
        ),
        const SizedBox(height: 20),
        const Text(
          'Ahorro por mes',
          style: TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w700,
            color: GlassTokens.text0,
          ),
        ),
        const SizedBox(height: 12),
        _BarChart(data: summary.byMonth),
      ],
    );
  }
}

class _BarChart extends StatelessWidget {
  final List<MonthlyStatEntity> data;
  const _BarChart({required this.data});

  @override
  Widget build(BuildContext context) {
    if (data.isEmpty) return const SizedBox.shrink();

    final maxVal = data
        .map((e) => e.totalSaved)
        .reduce((a, b) => a > b ? a : b);
    final fmt = NumberFormat('#,##0', 'es_CL');

    return GlassCard(
      radius: GlassTokens.radiusLg,
      level: 1,
      padding: const EdgeInsets.all(16),
      child: SizedBox(
        height: 160,
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: List.generate(data.length, (i) {
            final item = data[i];
            final ratio = maxVal > 0 ? item.totalSaved / maxVal : 0.0;
            final isLast = i == data.length - 1;
            return Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 3),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Text(
                      '\$${fmt.format(item.totalSaved)}',
                      style: const TextStyle(fontSize: 8, color: GlassTokens.text2),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 4),
                    AnimatedContainer(
                      duration: const Duration(milliseconds: 600),
                      curve: Curves.easeOut,
                      height: 100 * ratio,
                      decoration: BoxDecoration(
                        color: GlassTokens.green,
                        borderRadius: const BorderRadius.vertical(
                            top: Radius.circular(4)),
                        boxShadow: isLast
                            ? const [
                                BoxShadow(
                                  color: GlassTokens.greenGlow,
                                  blurRadius: 8,
                                  offset: Offset(0, -2),
                                ),
                              ]
                            : null,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      item.month,
                      style: const TextStyle(
                          fontSize: 10, color: GlassTokens.text2),
                    ),
                  ],
                ),
              ),
            );
          }),
        ),
      ),
    );
  }
}

class _MonthFilter extends StatelessWidget {
  final List<TransactionEntity> transactions;
  final String? selected;
  final ValueChanged<String?> onSelected;

  const _MonthFilter({
    required this.transactions,
    required this.selected,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    final fmt = DateFormat('MMM yyyy', 'es_CL');

    final months = transactions
        .map((tx) =>
            '${tx.transactionDate.year}-${tx.transactionDate.month.toString().padLeft(2, '0')}')
        .toSet()
        .toList()
      ..sort((a, b) => b.compareTo(a));

    if (months.length <= 1) return const SizedBox.shrink();

    return SizedBox(
      height: 34,
      child: ListView(
        scrollDirection: Axis.horizontal,
        children: [
          _Chip(
            label: 'Todos',
            active: selected == null,
            onTap: () => onSelected(null),
          ),
          ...months.map((key) {
            final parts = key.split('-');
            final dt = DateTime(int.parse(parts[0]), int.parse(parts[1]));
            return _Chip(
              label: fmt.format(dt),
              active: selected == key,
              onTap: () => onSelected(key),
            );
          }),
        ],
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  final String label;
  final bool active;
  final VoidCallback onTap;

  const _Chip({required this.label, required this.active, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        margin: const EdgeInsets.only(right: 8),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
        decoration: BoxDecoration(
          gradient: active ? GlassTokens.accentGradient : null,
          color: active ? null : GlassTokens.glass2,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: active ? Colors.transparent : GlassTokens.border2,
            width: 1,
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w600,
            color: active ? GlassTokens.text0 : GlassTokens.text2,
          ),
        ),
      ),
    );
  }
}

class _TransaccionesTab extends StatelessWidget {
  final List<TransactionEntity> transactions;
  const _TransaccionesTab({required this.transactions});

  @override
  Widget build(BuildContext context) {
    if (transactions.isEmpty) {
      return const GlassCard(
        radius: GlassTokens.radiusLg,
        child: Center(
          child: Padding(
            padding: EdgeInsets.all(24),
            child: Text(
              'No hay transacciones registradas.',
              style: TextStyle(color: GlassTokens.text2),
            ),
          ),
        ),
      );
    }

    final fmt = NumberFormat('#,##0', 'es_CL');
    final dateFmt = DateFormat('dd MMM yyyy', 'es_CL');

    return ListView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: transactions.length,
      itemBuilder: (_, i) {
        final tx = transactions[i];
        final displayName = tx.stationBrand ?? tx.stationName;
        final initials = displayName.length >= 2
            ? displayName.substring(0, 2).toUpperCase()
            : displayName.toUpperCase();
        final precioUnit = tx.unitPrice > 0
            ? tx.unitPrice.toStringAsFixed(0)
            : (tx.liters > 0 ? (tx.finalAmount / tx.liters).toStringAsFixed(0) : null);

        return Padding(
          padding: const EdgeInsets.only(bottom: 10),
          child: GlassCard(
            radius: GlassTokens.radiusMd,
            level: 1,
            padding: const EdgeInsets.all(14),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                GlassAvatar(
                  initials: initials,
                  accent: GlassTokens.cyan,
                  size: 40,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        displayName,
                        style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w700,
                          color: GlassTokens.text0,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        '${dateFmt.format(tx.transactionDate)} · ${tx.liters.toStringAsFixed(1)} L',
                        style: const TextStyle(
                          fontSize: 11,
                          color: GlassTokens.text2,
                        ),
                      ),
                      const SizedBox(height: 6),
                      Wrap(
                        spacing: 4,
                        runSpacing: 4,
                        children: [
                          GlassPill(
                            label: tx.fuelTypeName,
                            accent: GlassTokens.cyan,
                          ),
                          if (precioUnit != null)
                            GlassPill(
                              label: '\$$precioUnit/L',
                              accent: GlassTokens.orange,
                            ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 8),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text(
                      '\$${fmt.format(tx.finalAmount)}',
                      style: const TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w800,
                        color: GlassTokens.text0,
                      ),
                    ),
                    Text(
                      '-\$${fmt.format(tx.discountAmount)}',
                      style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                        color: GlassTokens.green,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

