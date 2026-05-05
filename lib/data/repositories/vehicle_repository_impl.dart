import 'package:dartz/dartz.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/repositories/vehicle_repository.dart';
import '../datasources/remote/vehicle_remote_data_source.dart';
import '../../core/errors/exceptions.dart';

class VehicleRepositoryImpl implements VehicleRepository {
  final VehicleRemoteDataSource remoteDataSource;

  VehicleRepositoryImpl(this.remoteDataSource);

  @override
  Future<Either<Failure, List<VehicleEntity>>> getVehicles() async {
    try {
      final vehicles = await remoteDataSource.getVehicles();
      return Right(vehicles);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener vehículos'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al obtener vehículos'));
    }
  }

  @override
  Future<Either<Failure, VehicleEntity>> addVehicle(VehicleEntity vehicle) async {
    try {
      final newVehicle = await remoteDataSource.addVehicle(vehicle);
      return Right(newVehicle);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al agregar vehículo'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al agregar vehículo'));
    }
  }

  @override
  Future<Either<Failure, void>> deleteVehicle(String id) async {
    try {
      await remoteDataSource.deleteVehicle(id);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al eliminar vehículo'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al eliminar vehículo'));
    }
  }

  @override
  Future<Either<Failure, void>> setActiveVehicle(String id) async {
     try {
      await remoteDataSource.setActiveVehicle(id);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al establecer vehículo activo'));
    } catch (e) {
      return const Left(ServerFailure('Error interno al establecer vehículo activo'));
    }
  }
}
