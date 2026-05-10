package cl.fuelonline.shared.util;

import java.math.BigDecimal;

/**
 * Utilidades geograficas. Distancias calculadas con la formula de Haversine
 * sobre una Tierra esferica de radio promedio 6371 km. Precision suficiente
 * para distancias urbanas (error tipico < 0.5%).
 */
public final class GeoUtils {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private GeoUtils() {}

    public static double distanciaKm(BigDecimal lat1, BigDecimal lon1,
                                     BigDecimal lat2, BigDecimal lon2) {
        return distanciaKm(lat1.doubleValue(), lon1.doubleValue(),
                           lat2.doubleValue(), lon2.doubleValue());
    }

    public static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;
    }

    /**
     * Aproxima un bounding-box (latMin/latMax/lonMin/lonMax) alrededor de un punto
     * para filtrar candidatos via indice antes de calcular distancia exacta.
     * Sobreestima el rango (no falsos negativos), aceptando algunos falsos positivos.
     */
    public static BoundingBox boundingBox(double lat, double lon, double radioKm) {
        double latDelta = radioKm / 111.0;
        double lonDelta = radioKm / (111.0 * Math.cos(Math.toRadians(lat)));
        return new BoundingBox(
                BigDecimal.valueOf(lat - latDelta),
                BigDecimal.valueOf(lat + latDelta),
                BigDecimal.valueOf(lon - lonDelta),
                BigDecimal.valueOf(lon + lonDelta)
        );
    }

    public record BoundingBox(BigDecimal latMin, BigDecimal latMax,
                              BigDecimal lonMin, BigDecimal lonMax) {}
}
