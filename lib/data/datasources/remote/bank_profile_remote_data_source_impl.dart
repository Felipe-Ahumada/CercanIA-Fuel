import 'package:dio/dio.dart';

import '../../../core/config/app_config.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/dio_client.dart';
import '../../../domain/entities/bank_profile_entity.dart';
import '../../mock/mock_backend_data.dart';
import 'bank_profile_remote_data_source.dart';

class BankProfileRemoteDataSourceImpl implements BankProfileRemoteDataSource {
  final DioClient dioClient;
  BankProfileEntity _mockProfile = MockBackendData.bankProfile;

  BankProfileRemoteDataSourceImpl(this.dioClient);

  @override
  Future<BankProfileEntity> getBankProfile() async {
    if (AppConfig.useMockData) {
      return _mockProfile;
    }

    try {
      final response = await dioClient.get('/bank-profile');

      if (response.statusCode == 200) {
        final data = response.data;
        final list = (data['convenios'] as List)
            .map((e) => BankConvenio.fromJson(e))
            .toList();
        return BankProfileEntity(
          userId: data['userId'] ?? '',
          convenios: list,
        );
      } else if (response.statusCode == 404) {
        return BankProfileEntity(userId: '', convenios: []);
      } else {
        throw ServerException(message: 'Error al obtener perfil bancario');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 404) {
        return BankProfileEntity(userId: '', convenios: []);
      }
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<BankProfileEntity> updateBankProfile(
      List<BankConvenio> convenios) async {
    if (AppConfig.useMockData) {
      _mockProfile = BankProfileEntity(
        userId: _mockProfile.userId,
        convenios: List.unmodifiable(convenios),
      );
      return _mockProfile;
    }

    try {
      final response = await dioClient.post(
        '/bank-profile',
        data: {
          'convenios': convenios.map((c) => c.toJson()).toList(),
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = response.data;
        final list = (data['convenios'] as List)
            .map((e) => BankConvenio.fromJson(e))
            .toList();
        return BankProfileEntity(
          userId: data['userId'] ?? '',
          convenios: list,
        );
      } else {
        throw ServerException(message: 'Error al actualizar perfil bancario');
      }
    } on DioException catch (e) {
      throw ServerException(message: e.message);
    } catch (e) {
      throw ServerException(message: e.toString());
    }
  }

  @override
  Future<List<String>> getBanksCatalog() async {
    if (AppConfig.useMockData) {
      return MockBackendData.banksCatalog;
    }

    // Mock catálogo local si el backend no lo provee inicialmente
    try {
      final response = await dioClient.get('/catalogs/banks');
      if (response.statusCode == 200) {
        return List<String>.from(response.data);
      }
      throw ServerException(message: 'Error al cargar bancos');
    } catch (_) {
      return [
        'Banco de Chile',
        'Banco Santander',
        'Banco Estado',
        'Scotiabank',
        'BCI',
        'Itaú',
        'Banco Falabella'
      ];
    }
  }

  @override
  Future<List<String>> getCardTypesCatalog() async {
    if (AppConfig.useMockData) {
      return MockBackendData.cardTypesCatalog;
    }

    // Mock catálogo
    try {
      final response = await dioClient.get('/catalogs/card-types');
      if (response.statusCode == 200) {
        return List<String>.from(response.data);
      }
      throw ServerException(message: 'Error al cargar tarjetas');
    } catch (_) {
      return ['Crédito', 'Débito', 'Prepago'];
    }
  }
}
