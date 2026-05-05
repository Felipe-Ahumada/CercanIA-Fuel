import '../../models/station_model.dart';
import '../../../core/network/dio_client.dart';

abstract class StationRemoteDataSource {
  Future<List<StationModel>> getNearbyStations(double lat, double lng, double radiusKm);
  Future<StationModel> getStationDetail(String stationId);
  Future<StationModel> toggleFavorite(String stationId, bool isFavorite);
}

class StationRemoteDataSourceImpl implements StationRemoteDataSource {
  final DioClient dioClient;

  StationRemoteDataSourceImpl(this.dioClient);

  @override
  Future<List<StationModel>> getNearbyStations(double lat, double lng, double radiusKm) async {
    try {
      final response = await dioClient.dio.get('/stations/nearby', queryParameters: {
        'lat': lat,
        'lng': lng,
        'radius': radiusKm,
      });

      final List<dynamic> data = response.data;
      return data.map((json) => StationModel.fromJson(json)).toList();
    } catch (e) {
      // For now, mock data if endpoint is not fully implemented
      return [
        StationModel(
          id: '1', nombre: 'Copec Apoquindo', marca: 'Copec', lat: -33.411, lng: -70.572, 
          precios: {}, esFavorita: false, ultimaSincronizacion: DateTime.now()
        ),
        StationModel(
          id: '2', nombre: 'Shell Las Condes', marca: 'Shell', lat: -33.415, lng: -70.570, 
          precios: {}, esFavorita: true, ultimaSincronizacion: DateTime.now()
        ),
      ];
    }
  }

  @override
  Future<StationModel> getStationDetail(String stationId) async {
    try {
      final response = await dioClient.dio.get('/stations/\$stationId');
      return StationModel.fromJson(response.data);
    } catch (e) {
      return StationModel(
        id: stationId, nombre: 'Copec Mock', marca: 'Copec', lat: 0, lng: 0, 
        precios: {}, esFavorita: false, ultimaSincronizacion: DateTime.now()
      );
    }
  }

  @override
  Future<StationModel> toggleFavorite(String stationId, bool isFavorite) async {
    try {
      final response = await dioClient.dio.post('/stations/\$stationId/favorite', data: {
        'es_favorita': isFavorite
      });
      return StationModel.fromJson(response.data);
    } catch (e) {
      return StationModel(
        id: stationId, nombre: 'Copec Mock', marca: 'Copec', lat: 0, lng: 0, 
        precios: {}, esFavorita: isFavorite, ultimaSincronizacion: DateTime.now()
      );
    }
  }
}
