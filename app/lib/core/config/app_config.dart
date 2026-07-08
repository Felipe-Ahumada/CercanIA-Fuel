class AppConfig {
  static const bool useMockData = bool.fromEnvironment(
    'USE_MOCK_DATA',
    defaultValue: false,
  );

  static const String _rawApiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1',
  );

  // Default targets the local backend on an Android emulator (10.0.2.2 = host machine).
  // iOS simulator: use http://localhost:8080/api/v1
  // Production:    use https://cercania-fuel-production.up.railway.app/api/v1
  //
  // Override at run time:
  //   flutter run \
  //     --dart-define=USE_MOCK_DATA=false \
  //     --dart-define=API_BASE_URL=https://cercania-fuel-production.up.railway.app/api/v1
  static String get apiBaseUrl => _normalizeApiBaseUrl(_rawApiBaseUrl);

  static String _normalizeApiBaseUrl(String value) {
    final trimmed = value.trim();
    if (trimmed.isEmpty) {
      return 'http://10.0.2.2:8080/api/v1';
    }

    final withScheme = trimmed.startsWith('http://') || trimmed.startsWith('https://')
        ? trimmed
        : 'https://$trimmed';

    final uri = Uri.parse(withScheme);
    final normalizedPath = uri.path.isEmpty || uri.path == '/'
        ? '/api/v1'
        : uri.path;

    return uri.replace(path: normalizedPath).toString();
  }
}
