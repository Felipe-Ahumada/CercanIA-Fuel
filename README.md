# CercanIA Fuel

Proyecto academico con backend Spring Boot, dashboard web React y app movil Flutter.

## Levantar backend, web y base de datos con Docker

Requisitos:

- Docker Desktop corriendo.

Desde la raiz del proyecto:

```powershell
docker compose up --build
```

Servicios locales:

- Dashboard web: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/actuator/health
- MySQL: localhost:3306

Credenciales del dashboard:

```text
Email: admin@fuelonline.cl
Password: Admin12345
```

Para detener el proyecto:

```powershell
docker compose down
```

Para reiniciar tambien la base de datos desde cero:

```powershell
docker compose down -v
docker compose up --build
```

## Cargar datos reales de la CNE

La integracion CNE se activa por variables de entorno. Crea un archivo `.env` en la raiz usando `.env.example` como base:

```powershell
Copy-Item .env.example .env
```

Completa una de estas opciones:

```text
CNE_EMAIL=tu_correo_cne
CNE_PASSWORD=tu_password_cne
```

o:

```text
CNE_API_TOKEN=tu_token_cne
```

Luego reconstruye el backend:

```powershell
docker compose up --build -d
```

Ejecuta la sincronizacion manual:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/admin/cne/sync -Headers @{ "X-Dev-User" = "admin@fuelonline.cl" }
```

La app movil mostrara estaciones cuando existan datos sincronizados cerca de la ubicacion del emulador.

## Levantar la app movil

La app no va en Docker. Para Android Emulator, el backend del host se alcanza por `10.0.2.2`.

```powershell
cd app
flutter run --dart-define=USE_MOCK_DATA=false --dart-define=API_BASE_URL=http://10.0.2.2:8080/api/v1
```
