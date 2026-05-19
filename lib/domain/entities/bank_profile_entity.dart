// ── Card product from the backend catalog ─────────────────────────────────────
class CardProductEntity {
  final int id;
  final String bankName;
  final String productName;
  final String cardType; // CREDIT | DEBIT | PREPAID | APP

  const CardProductEntity({
    required this.id,
    required this.bankName,
    required this.productName,
    required this.cardType,
  });

  String get displayName => '$bankName – $productName';
}

// ── User's selected card (persisted locally) ──────────────────────────────────
class BankAgreement {
  final int cardProductId;
  final String cardProductName;
  final String bankName;

  BankAgreement({
    required this.cardProductId,
    required this.cardProductName,
    required this.bankName,
  });

  Map<String, dynamic> toJson() => {
        'cardProductId': cardProductId,
        'cardProductName': cardProductName,
        'bankName': bankName,
      };

  factory BankAgreement.fromJson(Map<String, dynamic> json) => BankAgreement(
        cardProductId: (json['cardProductId'] as num).toInt(),
        cardProductName: json['cardProductName'] as String,
        bankName: json['bankName'] as String,
      );
}

// ── Discount from backend ─────────────────────────────────────────────────────
class DiscountEntity {
  final int id;
  final int brandId;
  final String brandName;
  // null = descuento global sin tarjeta requerida (Discount.cardProduct es nullable en el backend)
  final int? cardProductId;
  final String cardProductName;
  final String? bankName;
  final String? fuelTypeName;
  final int? dayOfWeek; // 1=Mon … 7=Sun; null = every day
  final String discountType; // PERCENTAGE | FIXED_AMOUNT
  final double discountValue;
  final double? maxCap;
  final String? description;

  const DiscountEntity({
    required this.id,
    required this.brandId,
    required this.brandName,
    this.cardProductId,
    required this.cardProductName,
    this.bankName,
    this.fuelTypeName,
    this.dayOfWeek,
    required this.discountType,
    required this.discountValue,
    this.maxCap,
    this.description,
  });

  bool get isPercentage => discountType == 'PERCENTAGE';

  String get valueLabel => discountType == 'PERCENTAGE'
      ? '${discountValue.toStringAsFixed(0)}%'
      : discountType == 'FIXED_PER_LITER'
          ? '\$${discountValue.toStringAsFixed(0)}/lt'
          : '\$${discountValue.toStringAsFixed(0)}';
}

// ── Profile wrapper ───────────────────────────────────────────────────────────
class BankProfileEntity {
  final String userId;
  final List<BankAgreement> agreements;

  const BankProfileEntity({
    required this.userId,
    this.agreements = const [],
  });

  List<int> get cardProductIds =>
      agreements.map((a) => a.cardProductId).toList();
}
