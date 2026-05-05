import 'package:flutter/material.dart';

class StationDetailPage extends StatelessWidget {
  final String stationId;
  // esto vendría de un BLoC o pasaríamos la StationEntity como extra.
  
  const StationDetailPage({super.key, required this.stationId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalle de Estación'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Mostrando detalle de estación: $stationId'),
            const SizedBox(height: 16),
            const Text('Precios (ejemplo):'),
            // Fix: Escapamos el signo de dólar para que se lea como moneda y no como variable.
            const Text('Bencina 93: \$1200'),
            const Text('Bencina 95: \$1250'),
            const Text('Bencina 97: \$1300'),
            const Text('Diésel: \$1050'),
            const SizedBox(height: 16),
            // Fix: Escapamos el signo de dólar en $20.
            const Text('Convenios aplicables: Banco Chile (-\$20/lt)'),
            const SizedBox(height: 16),
            const Text('Última sincronización CNE: hace 2 horas', style: TextStyle(color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}
