import '../../models/station_model.dart';
import '../../mock/mock_backend_data.dart';
import '../../../core/config/app_config.dart';
import '../../../core/network/dio_client.dart';

abstract class StationRemoteDataSource {
  Future<List<StationModel>> getNearbyStations(double lat, double lng, double radiusKm);
  Future<StationModel> getStationDetail(String stationId);
}

class StationRemoteDataSourceImpl implements StationRemoteDataSource {
  final DioClient dioClient;

  StationRemoteDataSourceImpl(this.dioClient);

  @override
  Future<List<StationModel>> getNearbyStations(
      double lat, double lng, double radiusKm) async {
    if (AppConfig.useMockData) return MockBackendData.stations;

    final response = await dioClient.dio.get(
      '/bencineras/cercanas',
      queryParameters: {'lat': lat, 'lon': lng, 'radioKm': radiusKm},
    );
    final List<dynamic> data = response.data;
    return data.map((json) => StationModel.fromJson(json)).toList();
  }

  @override
  Future<StationModel> getStationDetail(String stationId) async {
    if (AppConfig.useMockData) return MockBackendData.findStation(stationId);

    final response = await dioClient.dio.get('/bencineras/$stationId');
    return StationModel.fromJson(response.data as Map<String, dynamic>);
  }
}
