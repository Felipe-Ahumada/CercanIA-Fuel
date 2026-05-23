class DiscountCalculationEntity {
  final int? discountId;
  final String? description;
  final double grossAmount;
  final double discountAmount;
  final double finalAmount;

  const DiscountCalculationEntity({
    this.discountId,
    this.description,
    required this.grossAmount,
    required this.discountAmount,
    required this.finalAmount,
  });
}
