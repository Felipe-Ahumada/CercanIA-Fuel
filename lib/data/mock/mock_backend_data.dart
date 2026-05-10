import '../../domain/entities/bank_profile_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../models/station_model.dart';

class MockBackendData {
  static final DateTime lastSync = DateTime(2026, 4, 23, 8, 38, 29);

  static final List<StationModel> stations = [
    StationModel(
      id: '99484d32-ab42-4798-9fb6-5fc3baf06135',
      nombre: 'EMPRESAS COPEC S.A.',
      marca: 'COPEC',
      lat: -33.4500,
      lng: -70.6700,
      precios: {
        Fuel.bencina93: 1289,
        Fuel.bencina95: 1338,
        Fuel.bencina97: 1392,
        Fuel.diesel: 1175,
      },
      esFavorita: true,
      ultimaSincronizacion: lastSync,
    ),
    StationModel(
      id: '5d8f626c-5ef5-4b47-9181-95df5a1b32b1',
      nombre: 'Shell Apoquindo',
      marca: 'SHELL',
      lat: -33.4150,
      lng: -70.5700,
      precios: {
        Fuel.bencina93: 1295,
        Fuel.bencina95: 1341,
        Fuel.bencina97: 1401,
        Fuel.diesel: 1189,
      },
      esFavorita: false,
      ultimaSincronizacion: lastSync,
    ),
    StationModel(
      id: 'a8aa9d2b-f1d3-4e48-8f26-88a38e316212',
      nombre: 'Petrobras Providencia',
      marca: 'PETROBRAS',
      lat: -33.4268,
      lng: -70.6166,
      precios: {
        Fuel.bencina93: 1278,
        Fuel.bencina95: 1327,
        Fuel.bencina97: 1385,
        Fuel.diesel: 1168,
      },
      esFavorita: false,
      ultimaSincronizacion: lastSync,
    ),
    StationModel(
      id: 'cf476748-9212-4f43-bfdc-a8fe2e17440b',
      nombre: 'Aramco Las Condes',
      marca: 'ARAMCO',
      lat: -33.4076,
      lng: -70.5675,
      precios: {
        Fuel.bencina93: 1302,
        Fuel.bencina95: 1350,
        Fuel.bencina97: 1412,
        Fuel.diesel: 1197,
      },
      esFavorita: false,
      ultimaSincronizacion: lastSync,
    ),
    StationModel(
      id: 'f59f6c47-d090-4701-ad07-f4eb1691096f',
      nombre: 'JLC Nunoa',
      marca: 'JLC',
      lat: -33.4569,
      lng: -70.5970,
      precios: {
        Fuel.bencina93: 1269,
        Fuel.bencina95: 1318,
        Fuel.bencina97: 1379,
      },
      esFavorita: false,
      ultimaSincronizacion: lastSync,
    ),
  ];

  static final List<Map<String, dynamic>> bencinerasCercanas = [
    {
      'id': '99484d32-ab42-4798-9fb6-5fc3baf06135',
      'codigoApi': 'co131401',
      'nombre': 'EMPRESAS COPEC S.A.',
      'marcaNombre': 'COPEC',
      'comunaNombre': 'Santiago',
      'direccion': 'Alameda 1234',
      'latitud': -33.4500,
      'longitud': -70.6700,
      'distanciaKm': 0.13,
      'enMantenimiento': false,
    },
    {
      'id': '5d8f626c-5ef5-4b47-9181-95df5a1b32b1',
      'codigoApi': 'sh131401',
      'nombre': 'Shell Apoquindo',
      'marcaNombre': 'SHELL',
      'comunaNombre': 'Las Condes',
      'direccion': 'Av. Apoquindo 4500',
      'latitud': -33.4150,
      'longitud': -70.5700,
      'distanciaKm': 0.84,
      'enMantenimiento': false,
    },
  ];

  static List<Map<String, dynamic>> preciosVigentes(String stationId) {
    final station = findStation(stationId);
    return station.precios.entries
        .map(
          (entry) => {
            'tipoCombustibleId': fuelBackendId(entry.key),
            'tipoCombustibleNombre': entry.key.displayName,
            'precio': entry.value,
            'unidadCobro': 'LT',
            'tipoAtencion': 'FULL',
            'apiTimestamp': lastSync.toIso8601String(),
          },
        )
        .toList();
  }

  static StationModel findStation(String stationId) {
    return stations.firstWhere(
      (station) => station.id == stationId,
      orElse: () => stations.first,
    );
  }

  static final List<VehicleEntity> vehicles = [
    VehicleEntity(
      id: '660e8400-e29b-41d4-a716-446655440001',
      marca: 'Toyota',
      modelo: 'Yaris',
      tipoCombustible: Fuel.bencina95,
      activo: true,
    ),
    VehicleEntity(
      id: '660e8400-e29b-41d4-a716-446655440002',
      marca: 'Hyundai',
      modelo: 'Accent',
      tipoCombustible: Fuel.bencina93,
    ),
  ];

  static final BankProfileEntity bankProfile = BankProfileEntity(
    userId: '3ee2b0ad-7985-4275-a902-ed00bdb34cc7',
    convenios: [
      BankConvenio(banco: 'Banco Scotiabank', tipoTarjeta: 'Visa Platinum'),
      BankConvenio(banco: 'Banco Estado', tipoTarjeta: 'Debito'),
    ],
  );

  static const List<String> banksCatalog = [
    'Banco de Chile',
    'Banco Santander',
    'Banco Estado',
    'Banco Scotiabank',
    'BCI',
    'Itau',
    'Banco Falabella',
  ];

  static const List<String> cardTypesCatalog = [
    'Credito',
    'Debito',
    'Prepago',
    'Visa Platinum',
    'Mastercard Black',
  ];

  static final Map<String, dynamic> discountCalculation = {
    'descuentoId': 5,
    'etiqueta': 'Banco Scotiabank - Visa Platinum (12%)',
    'montoBruto': 30000.0,
    'montoDescuento': 1500.0,
    'montoFinal': 28500.0,
  };

  static final Map<String, dynamic> transactionsSummary = {
    'desde': '2026-01-01',
    'hasta': '2026-12-31',
    'totalGastado': 158420.0,
    'totalAhorrado': 12380.0,
    'totalLitros': 118.5,
    'cantidadCargas': 6,
  };

  static final Map<String, dynamic> unreadAlerts = {
    'content': [
      {
        'id': 42,
        'usuarioId': '3ee2b0ad-...',
        'bencineraId': '99484d32-ab42-4798-9fb6-5fc3baf06135',
        'bencineraNombre': 'Copec Apoquindo',
        'tipoAlerta': 'PRECIO_BAJO',
        'titulo': 'Precio bajo cerca tuyo',
        'mensaje': 'La 95 bajo a \$1.289 en Copec Apoquindo (a 1.2 km)',
        'leida': false,
        'leidaAt': null,
        'createdAt': '2026-05-06T20:30:00',
      },
    ],
    'totalElements': 1,
  };

  static final Map<String, dynamic> ratingsSummary = {
    'bencineraId': '99484d32-ab42-4798-9fb6-5fc3baf06135',
    'promedio': 4.32,
    'total': 47,
  };

  static int fuelBackendId(Fuel fuel) {
    switch (fuel) {
      case Fuel.bencina93:
        return 1;
      case Fuel.bencina95:
        return 2;
      case Fuel.bencina97:
        return 3;
      case Fuel.diesel:
        return 4;
    }
  }
}
