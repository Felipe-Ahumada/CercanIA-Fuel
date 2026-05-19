import '../../domain/entities/transaction_entity.dart';

class TransactionModel extends TransactionEntity {
  const TransactionModel({
    required super.id,
    required super.stationId,
    required super.stationName,
    required super.transactionDate,
    required super.liters,
    required super.unitPrice,
    required super.grossAmount,
    required super.finalAmount,
    required super.discountAmount,
    required super.fuelTypeName,
    super.vehicleId,
    super.cardProductId,
    super.cardProductName,
    super.discountId,
    super.notes,
  });

  factory TransactionModel.fromJson(Map<String, dynamic> json) =>
      TransactionModel(
        id: (json['id'] ?? '').toString(),
        stationId: (json['stationId'] ?? json['bencineraId'] ?? '').toString(),
        stationName: (json['stationName'] ?? json['bencineraNombre'] ?? '') as String,
        transactionDate: DateTime.parse(
            (json['transactionDate'] ?? json['fechaCarga'] ?? '') as String),
        liters:        ((json['liters']         ?? json['litros']         ?? 0) as num).toDouble(),
        unitPrice:     ((json['unitPrice']       ?? json['precioUnitario'] ?? 0) as num).toDouble(),
        grossAmount:   ((json['grossAmount']     ?? json['montoTotal']     ?? 0) as num).toDouble(),
        finalAmount:   ((json['finalAmount']     ?? json['montoFinal']     ?? 0) as num).toDouble(),
        discountAmount:((json['discountAmount']  ?? json['montoDescuento'] ?? 0) as num).toDouble(),
        fuelTypeName: (json['fuelTypeName'] ?? json['tipoCombustible'] ?? '') as String,
        vehicleId: json['vehicleId']?.toString(),
        cardProductId: json['cardProductId'] as int?,
        cardProductName: json['cardProductName'] as String?,
        discountId: json['discountId'] as int?,
        notes: json['notes'] as String?,
      );
}
