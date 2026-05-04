import 'package:equatable/equatable.dart';

class FuelStation extends Equatable {
  final String id;
  final String name;
  final String brand;
  final String address;
  final double latitude;
  final double longitude;
  final bool isOpen;

  const FuelStation({
    required this.id,
    required this.name,
    required this.brand,
    required this.address,
    required this.latitude,
    required this.longitude,
    required this.isOpen,
  });

  @override
  List<Object?> get props => [id, name, brand, address, latitude, longitude, isOpen];
}
