package cl.fuelonline.shared.util;

/**
 * Normalizes a Chilean RUT to a canonical form: digits + verifier, no dots or dashes.
 * Examples:
 *   "12.345.678-9" -> "123456789"
 *   "12345678-K"   -> "12345678K"
 *   "123456789"    -> "123456789"
 */
public final class RutUtils {
    private RutUtils() {}

    public static String normalize(String rut) {
        if (rut == null) return null;
        return rut.trim()
                  .replace(".", "")
                  .replace("-", "")
                  .toUpperCase();
    }
}