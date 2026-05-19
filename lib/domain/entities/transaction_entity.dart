class TransactionEntity {
  final String id;
  final String stationId;
  final String stationName;
  final DateTime transactionDate;
  final double liters;
  final double unitPrice;
  final double grossAmount;
  final double finalAmount;
  final double discountAmount;
  final String fuelTypeName;
  final String? vehicleId;
  final int? cardProductId;
  final String? cardProductName;
  final int? discountId;
  final String? notes;

  const TransactionEntity({
    required this.id,
    required this.stationId,
    required this.stationName,
    required this.transactionDate,
    required this.liters,
    required this.unitPrice,
    required this.grossAmount,
    required this.finalAmount,
    required this.discountAmount,
    required this.fuelTypeName,
    this.vehicleId,
    this.cardProductId,
    this.cardProductName,
    this.discountId,
    this.notes,
  });
}
