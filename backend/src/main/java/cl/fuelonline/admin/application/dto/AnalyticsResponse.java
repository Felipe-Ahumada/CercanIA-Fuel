package cl.fuelonline.admin.application.dto;

import java.util.List;

public record AnalyticsResponse(
        List<MonthlyCount>   usersByMonth,
        List<NamedCount>     stationsByBrand,
        List<NamedCount>     fuelDistribution,
        List<DiscountUsage>  topDiscountsByUsage,
        List<HourlyCount>    transactionsByHour,
        List<BrandDetail>    brandDetails,
        long totalUsers,
        long activeUsers,
        long totalStations,
        long activeDiscounts,
        long totalDiscountUses
) {
    public record MonthlyCount(String month, long count) {}
    public record NamedCount(String name, long count) {}
    public record DiscountUsage(String brand, String bank, String cardProduct,
                                double value, String type, long userCount) {}
    public record HourlyCount(int hour, long count) {}
    public record BrandDetail(
            String brand,
            long   recargas,
            long   uniqueUsers,
            double ahorroTotal,
            double litrosTotal,
            double tendencia
    ) {}
}
