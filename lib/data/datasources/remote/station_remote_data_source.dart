import '../../models/station_model.dart';
import '../../mock/mock_backend_data.dart';
import '../../../core/config/app_config.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';

abstract class StationRemoteDataSource {
  Future<List<StationModel>> getNearbyStations(
      double lat, double lng, double radiusKm);
  Future<StationModel> getStationDetail(String stationId);
  Future<StationModel> toggleFavorite(String stationId, bool isFavorite);
}

class StationRemoteDataSourceImpl implements StationRemoteDataSource {
  final DioClient dioClient;
  final Set<String> _favoriteStationIds;

  StationRemoteDataSourceImpl(this.dioClient)
      : _favoriteStationIds = MockBackendData.stations
            .where((station) => station.esFavorita)
            .map((station) => station.id)
            .toSet();

  @override
  Future<List<StationModel>> getNearbyStations(
    double lat,
    double lng,
    double radiusKm,
  ) async {
    if (AppConfig.useMockData) {
      return _withFavoriteState(MockBackendData.stations);
    }

    final response = await dioClient.dio.get(
      '/bencineras/cercanas',
      queryParameters: {
        'lat': lat,
        'lon': lng,
        'radioKm': radiusKm,
      },
    );

    final List<dynamic> data = response.data;
    return data.map((json) => StationModel.fromJson(json)).toList();
  }

  @override
  Future<StationModel> getStationDetail(String stationId) async {
    if (AppConfig.useMockData) {
      return _withFavoriteState([MockBackendData.findStation(stationId)]).first;
    }

    final response = await dioClient.dio.get('/bencineras/$stationId');
    return StationModel.fromJson(response.data);
  }

  @override
  Future<StationModel> toggleFavorite(String stationId, bool isFavorite) async {
    if (AppConfig.useMockData) {
      if (isFavorite) {
        _favoriteStationIds.add(stationId);
      } else {
        _favoriteStationIds.remove(stationId);
      }
      return _withFavoriteState([MockBackendData.findStation(stationId)]).first;
    }

    throw ServerException(
      message: 'Favoritos reales requieren usuarioId y endpoint /favoritos.',
    );
  }

  List<StationModel> _withFavoriteState(List<StationModel> stations) {
    return stations
        .map(
          (station) => StationModel(
            id: station.id,
            nombre: station.nombre,
            marca: station.marca,
            lat: station.lat,
            lng: station.lng,
            precios: station.precios,
            esFavorita: _favoriteStationIds.contains(station.id),
            ultimaSincronizacion: station.ultimaSincronizacion,
          ),
        )
        .toList();
  }
}
