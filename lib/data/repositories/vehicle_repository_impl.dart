import 'package:dartz/dartz.dart';
import '../../core/errors/exceptions.dart';
import '../../core/errors/failure.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/repositories/vehicle_repository.dart';
import '../datasources/remote/vehicle_remote_data_source.dart';

class VehicleRepositoryImpl implements VehicleRepository {
  final VehicleRemoteDataSource remoteDataSource;

  VehicleRepositoryImpl(this.remoteDataSource);

  @override
  Future<Either<Failure, List<VehicleBrandEntity>>> getBrands() async {
    try {
      return Right(await remoteDataSource.getBrands());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener marcas'));
    }
  }

  @override
  Future<Either<Failure, List<VehicleModelEntity>>> getModelsByBrand(int brandId) async {
    try {
      return Right(await remoteDataSource.getModelsByBrand(brandId));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener modelos'));
    }
  }

  @override
  Future<Either<Failure, List<FuelTypeEntity>>> getFuelTypes() async {
    try {
      return Right(await remoteDataSource.getFuelTypes());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener combustibles'));
    }
  }

  @override
  Future<Either<Failure, List<VehicleEntity>>> getVehicles() async {
    try {
      return Right(await remoteDataSource.getVehicles());
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al obtener vehículos'));
    }
  }

  @override
  Future<Either<Failure, VehicleEntity>> addVehicle({
    required int vehicleModelId,
    required int fuelTypeId,
    required String licensePlate,
    required int year,
    required String brandName,
    required String modelName,
  }) async {
    try {
      return Right(await remoteDataSource.addVehicle(
        vehicleModelId: vehicleModelId,
        fuelTypeId: fuelTypeId,
        licensePlate: licensePlate,
        year: year,
        brandName: brandName,
        modelName: modelName,
      ));
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al agregar vehículo'));
    }
  }

  @override
  Future<Either<Failure, void>> deleteVehicle(String id) async {
    try {
      await remoteDataSource.deleteVehicle(id);
      return const Right(null);
    } on ServerException catch (e) {
      return Left(ServerFailure(e.message ?? 'Error al eliminar vehículo'));
    }
  }
}
