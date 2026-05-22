package cl.fuelonline.admin.application.service;

import cl.fuelonline.admin.application.dto.AnalyticsResponse;
import cl.fuelonline.admin.application.dto.AnalyticsResponse.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAnalyticsService {

    private final EntityManager em;

    public AnalyticsResponse getAnalytics(LocalDate startDate, LocalDate endDate, Integer regionId) {
        return new AnalyticsResponse(
                usersByMonth(startDate, endDate),
                stationsByBrand(startDate, endDate, regionId),
                fuelDistribution(startDate, endDate, regionId),
                topDiscountsByUsage(),
                transactionsByHour(startDate, endDate, regionId),
                brandDetails(startDate, endDate, regionId),
                totalUsers(),
                activeUsers(),
                totalStations(),
                activeDiscounts(),
                totalDiscountUses(startDate, endDate, regionId)
        );
    }

    // ── helpers ───────────────────────────────────────────────────────────────────

    /** WHERE fragment for transaction date; conditions go in the main WHERE clause */
    private String txDateWhere(LocalDate start, LocalDate end, String alias) {
        StringBuilder sb = new StringBuilder();
        if (start != null) sb.append(" AND ").append(alias).append(".transaction_date >= :startDate");
        if (end   != null) sb.append(" AND ").append(alias).append(".transaction_date <= :endDate");
        return sb.toString();
    }

    /** ON fragment for transaction date; conditions go inside a LEFT JOIN ON clause */
    private String txDateOn(LocalDate start, LocalDate end, String alias) {
        StringBuilder sb = new StringBuilder();
        if (start != null) sb.append(" AND ").append(alias).append(".transaction_date >= :startDate");
        if (end   != null) sb.append(" AND ").append(alias).append(".transaction_date <= :endDate");
        return sb.toString();
    }

    private String userDateWhere(LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder();
        if (start != null) sb.append(" AND u.created_at >= :startDate");
        if (end   != null) sb.append(" AND u.created_at <= :endDate");
        return sb.toString();
    }

    /** Extra INNER JOIN to commune — only added when regionId is present */
    private String regionJoin(Integer regionId) {
        return regionId != null ? " JOIN commune co ON s.commune_id = co.id" : "";
    }

    private String regionWhere(Integer regionId) {
        return regionId != null ? " AND co.region_id = :regionId" : "";
    }

    private void bind(Query q, LocalDate start, LocalDate end, Integer regionId) {
        if (start    != null) q.setParameter("startDate", start);
        if (end      != null) q.setParameter("endDate",   end);
        if (regionId != null) q.setParameter("regionId",  regionId);
    }

    // ── queries ───────────────────────────────────────────────────────────────────

    private List<MonthlyCount> usersByMonth(LocalDate start, LocalDate end) {
        String sql =
            "SELECT DATE_FORMAT(u.created_at, '%Y-%m') as month, COUNT(*) as total " +
            "FROM user u WHERE 1=1" +
            userDateWhere(start, end) +
            " GROUP BY month ORDER BY month";
        Query q = em.createNativeQuery(sql);
        bind(q, start, end, null);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new MonthlyCount((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    private List<NamedCount> stationsByBrand(LocalDate start, LocalDate end, Integer regionId) {
        String sql =
            "SELECT b.name, COUNT(t.id) as recargas " +
            "FROM transaction t " +
            "JOIN station s ON t.station_id = s.id " +
            "JOIN brand b ON s.brand_id = b.id" +
            regionJoin(regionId) +
            " WHERE t.deleted_at IS NULL" +
            txDateWhere(start, end, "t") +
            regionWhere(regionId) +
            " GROUP BY b.id, b.name ORDER BY recargas DESC LIMIT 10";
        Query q = em.createNativeQuery(sql);
        bind(q, start, end, regionId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new NamedCount((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    private List<NamedCount> fuelDistribution(LocalDate start, LocalDate end, Integer regionId) {
        String sql =
            "SELECT ft.name, COUNT(t.id) as recargas " +
            "FROM transaction t " +
            "JOIN fuel_type ft ON t.fuel_type_id = ft.id " +
            "JOIN station s ON t.station_id = s.id" +
            regionJoin(regionId) +
            " WHERE t.deleted_at IS NULL" +
            txDateWhere(start, end, "t") +
            regionWhere(regionId) +
            " GROUP BY ft.id, ft.name ORDER BY recargas DESC";
        Query q = em.createNativeQuery(sql);
        bind(q, start, end, regionId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new NamedCount((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    private List<DiscountUsage> topDiscountsByUsage() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                "SELECT b.name, COALESCE(ba.name, 'Sin banco'), COALESCE(cp.name, '-'), " +
                "       d.discount_value, d.discount_type, COUNT(usd.user_id) as user_count " +
                "FROM discount d " +
                "JOIN brand b ON d.brand_id = b.id " +
                "LEFT JOIN card_product cp ON d.card_product_id = cp.id " +
                "LEFT JOIN bank ba ON cp.bank_id = ba.id " +
                "LEFT JOIN user_selected_discount usd ON d.id = usd.discount_id " +
                "WHERE d.active = 1 " +
                "GROUP BY d.id, b.name, ba.name, cp.name, d.discount_value, d.discount_type " +
                "ORDER BY user_count DESC, d.discount_value DESC LIMIT 10").getResultList();
        return rows.stream()
                .map(r -> new DiscountUsage(
                        (String) r[0], (String) r[1], (String) r[2],
                        ((Number) r[3]).doubleValue(), (String) r[4],
                        ((Number) r[5]).longValue()))
                .toList();
    }

    private List<HourlyCount> transactionsByHour(LocalDate start, LocalDate end, Integer regionId) {
        String sql =
            "SELECT HOUR(t.created_at) as hour, COUNT(*) as total " +
            "FROM transaction t " +
            "JOIN station s ON t.station_id = s.id" +
            regionJoin(regionId) +
            " WHERE t.deleted_at IS NULL" +
            txDateWhere(start, end, "t") +
            regionWhere(regionId) +
            " GROUP BY hour ORDER BY hour";
        Query q = em.createNativeQuery(sql);
        bind(q, start, end, regionId);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new HourlyCount(((Number) r[0]).intValue(), ((Number) r[1]).longValue()))
                .toList();
    }

    private List<BrandDetail> brandDetails(LocalDate start, LocalDate end, Integer regionId) {
        // Date filter goes in LEFT JOIN ON so brands with 0 transactions aren't excluded.
        // Region filter goes in WHERE (after the INNER JOIN to commune).
        String refDate = "COALESCE(:refDate, CURDATE())";
        String sql =
            "SELECT b.name, " +
            "  COUNT(t.id)                                                              AS recargas, " +
            "  COUNT(DISTINCT t.user_id)                                                AS unique_users, " +
            "  COALESCE(SUM(t.discount_amount), 0)                                     AS ahorro, " +
            "  COALESCE(SUM(t.liters), 0)                                              AS litros, " +
            "  COUNT(CASE WHEN PERIOD_DIFF(DATE_FORMAT(t.transaction_date,'%Y%m'), " +
            "        DATE_FORMAT(" + refDate + ",'%Y%m')) = 0  THEN 1 END)             AS mes_actual, " +
            "  COUNT(CASE WHEN PERIOD_DIFF(DATE_FORMAT(t.transaction_date,'%Y%m'), " +
            "        DATE_FORMAT(" + refDate + ",'%Y%m')) = -1 THEN 1 END)             AS mes_anterior " +
            "FROM brand b " +
            "JOIN station s ON s.brand_id = b.id" +
            regionJoin(regionId) +
            " LEFT JOIN transaction t ON t.station_id = s.id AND t.deleted_at IS NULL" +
            txDateOn(start, end, "t") +
            (regionId != null ? " WHERE co.region_id = :regionId" : "") +
            " GROUP BY b.id, b.name " +
            "HAVING COUNT(s.id) > 0 " +
            "ORDER BY recargas DESC, b.name ASC";

        Query q = em.createNativeQuery(sql);
        // refDate: use endDate as the reference month for trend; null falls back to CURDATE()
        q.setParameter("refDate", end);
        if (start    != null) q.setParameter("startDate", start);
        if (end      != null) q.setParameter("endDate",   end);
        if (regionId != null) q.setParameter("regionId",  regionId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            long mesActual   = ((Number) r[5]).longValue();
            long mesAnterior = ((Number) r[6]).longValue();
            double tendencia = mesAnterior == 0
                    ? (mesActual > 0 ? 100.0 : 0.0)
                    : ((mesActual - mesAnterior) * 100.0 / mesAnterior);
            return new BrandDetail(
                    (String) r[0],
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue(),
                    ((Number) r[3]).doubleValue(),
                    ((Number) r[4]).doubleValue(),
                    Math.round(tendencia * 10.0) / 10.0
            );
        }).toList();
    }

    private long totalUsers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM user").getSingleResult()).longValue();
    }

    private long activeUsers() {
        return ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM user u JOIN role r ON u.role_id = r.id WHERE r.name = 'USER'")
                .getSingleResult()).longValue();
    }

    private long totalStations() {
        return ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM station WHERE in_maintenance = 0").getSingleResult()).longValue();
    }

    private long activeDiscounts() {
        return ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM discount WHERE active = 1").getSingleResult()).longValue();
    }

    private long totalDiscountUses(LocalDate start, LocalDate end, Integer regionId) {
        String sql =
            "SELECT COUNT(*) FROM transaction t " +
            "JOIN station s ON t.station_id = s.id" +
            regionJoin(regionId) +
            " WHERE t.discount_id IS NOT NULL AND t.deleted_at IS NULL" +
            txDateWhere(start, end, "t") +
            regionWhere(regionId);
        Query q = em.createNativeQuery(sql);
        bind(q, start, end, regionId);
        return ((Number) q.getSingleResult()).longValue();
    }
}
