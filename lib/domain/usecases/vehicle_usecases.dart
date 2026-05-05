import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../entities/vehicle_entity.dart';
import '../repositories/vehicle_repository.dart';

class GetVehiclesUseCase {
  final VehicleRepository repository;
  GetVehiclesUseCase(this.repository);

  Future<Either<Failure, List<VehicleEntity>>> call() async {
    return await repository.getVehicles();
  }
}

class AddVehicleUseCase {
  final VehicleRepository repository;
  AddVehicleUseCase(this.repository);

  Future<Either<Failure, VehicleEntity>> call(VehicleEntity vehicle) async {
    return await repository.addVehicle(vehicle);
  }
}

class DeleteVehicleUseCase {
  final VehicleRepository repository;
  DeleteVehicleUseCase(this.repository);

  Future<Either<Failure, void>> call(String id) async {
    return await repository.deleteVehicle(id);
  }
}

class SetActiveVehicleUseCase {
  final VehicleRepository repository;
  SetActiveVehicleUseCase(this.repository);

  Future<Either<Failure, void>> call(String id) async {
    return await repository.setActiveVehicle(id);
  }
}
