class BankConvenio {
  final String banco;
  final String tipoTarjeta;

  BankConvenio({
    required this.banco,
    required this.tipoTarjeta,
  });

  Map<String, dynamic> toJson() => {
        'banco': banco,
        'tipoTarjeta': tipoTarjeta,
      };

  factory BankConvenio.fromJson(Map<String, dynamic> json) => BankConvenio(
        banco: json['banco'],
        tipoTarjeta: json['tipoTarjeta'],
      );
}

class BankProfileEntity {
  final String userId;
  final List<BankConvenio> convenios;

  BankProfileEntity({
    required this.userId,
    required this.convenios,
  });
}
