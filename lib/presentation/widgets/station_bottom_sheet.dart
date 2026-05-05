import 'package:flutter/material.dart';
import '../../domain/entities/station_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import 'package:go_router/go_router.dart';

class StationBottomSheet extends StatelessWidget {
  final StationEntity station;
  final Fuel? selectedFuel;
  final Function() onToggleFavorite;

  const StationBottomSheet({
    super.key,
    required this.station,
    required this.selectedFuel,
    required this.onToggleFavorite,
  });

  @override
  Widget build(BuildContext context) {
    final price = selectedFuel != null ? station.precios[selectedFuel] : null;

    return Container(
      padding: const EdgeInsets.all(16.0),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                child: Text(
                  station.nombre,
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                ),
              ),
              IconButton(
                icon: Icon(
                  station.esFavorita ? Icons.favorite : Icons.favorite_border,
                  color: station.esFavorita ? Colors.red : Colors.grey,
                ),
                onPressed: onToggleFavorite,
              )
            ],
          ),
          const SizedBox(height: 8),
          Text(station.marca, style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.grey[700])),
          const SizedBox(height: 16),
          if (selectedFuel != null)
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Precio filtrado (${selectedFuel!.displayName}):'),
                Text(
                  price != null ? '\$$price' : 'No disponible',
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                ),
              ],
            ),
          const SizedBox(height: 24),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                context.push('/station_detail', extra: station.id);
              },
              child: const Text('Ver detalle'),
            ),
          )
        ],
      ),
    );
  }
}
