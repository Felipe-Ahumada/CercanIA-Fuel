import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:pretty_dio_logger/pretty_dio_logger.dart';

import '../config/app_config.dart';
import 'auth_interceptor.dart';

class DioClient {
  final Dio dio;
  final AuthInterceptor authInterceptor;

  DioClient({
    required this.dio,
    required this.authInterceptor,
  }) {
    // Configuración base de la API de backend
    // (Asegúrate de cambiar esta URL por la de tu backend real en Railway)
    dio.options.baseUrl = AppConfig.apiBaseUrl;
    dio.options.connectTimeout = const Duration(seconds: 10);
    dio.options.receiveTimeout = const Duration(seconds: 10);

    dio.interceptors.add(authInterceptor);

    if (kDebugMode) {
      dio.interceptors.add(
        PrettyDioLogger(
          requestHeader: true,
          requestBody: true,
          responseBody: true,
          responseHeader: false,
          error: true,
          compact: true,
          maxWidth: 90,
        ),
      );
    }
  }

  Future<Response> get(String uri,
      {Map<String, dynamic>? queryParameters}) async {
    return await dio.get(uri, queryParameters: queryParameters);
  }

  Future<Response> post(String uri, {dynamic data, Options? options}) async {
    return await dio.post(uri, data: data, options: options);
  }

  Future<Response> put(String uri, {dynamic data, Options? options}) async {
    return await dio.put(uri, data: data, options: options);
  }

  Future<Response> patch(String uri, {dynamic data, Options? options}) async {
    return await dio.patch(uri, data: data, options: options);
  }

  Future<Response> delete(String uri, {dynamic data}) async {
    return await dio.delete(uri, data: data);
  }
}
