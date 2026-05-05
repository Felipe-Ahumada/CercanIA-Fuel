import 'package:dio/dio.dart';
import 'package:flutter_application_1/core/network/dio_client.dart';
import 'package:flutter_application_1/domain/entities/bank_profile_entity.dart';
import 'package:flutter_application_1/data/datasources/remote/bank_profile_remote_data_source.dart';
import 'package:flutter_application_1/core/errors/exceptions.dart';

class BankProfileRemoteDataSourceImpl implements BankProfileRemoteDataSource {
  final DioClient dioClient;

  BankProfileRemoteDataSourceImpl(this.dioClient);

  @override
  Future<BankProfileEntity> getBankProfile() async {
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
  Future<BankProfileEntity> updateBankProfile(List<BankConvenio> convenios) async {
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
    // Mock catálogo local si el backend no lo provee inicialmente
    try {
      final response = await dioClient.get('/catalogs/banks');
      if (response.statusCode == 200) {
        return List<String>.from(response.data);
      }
      throw ServerException(message: 'Error al cargar bancos');
    } catch (_) {
      return ['Banco de Chile', 'Banco Santander', 'Banco Estado', 'Scotiabank', 'BCI', 'Itaú', 'Banco Falabella'];
    }
  }

  @override
  Future<List<String>> getCardTypesCatalog() async {
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
