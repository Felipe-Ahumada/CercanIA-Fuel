class AppConfig {
  static const bool useMockData = bool.fromEnvironment(
    'USE_MOCK_DATA',
    defaultValue: false,
  );

  // Default targets the local backend on an Android emulator (10.0.2.2 = host machine).
  // iOS simulator: use http://localhost:8080/api/v1
  // Production:    use https://cercania-fuel-backend.up.railway.app/api/v1
  //
  // Override at run time:
  //   flutter run \
  //     --dart-define=USE_MOCK_DATA=false \
  //     --dart-define=API_BASE_URL=http://10.0.2.2:8080/api/v1
  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1',
  );
}
