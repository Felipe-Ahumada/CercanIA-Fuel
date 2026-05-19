import 'package:equatable/equatable.dart';
import '../../../domain/entities/station_entity.dart';

abstract class StationDetailState extends Equatable {
  const StationDetailState();
  @override
  List<Object?> get props => [];
}

class StationDetailInitial extends StationDetailState {}

class StationDetailLoading extends StationDetailState {}

class StationDetailLoaded extends StationDetailState {
  final StationEntity station;
  const StationDetailLoaded(this.station);
  @override
  List<Object?> get props => [station];
}

class StationDetailError extends StationDetailState {
  final String message;
  const StationDetailError(this.message);
  @override
  List<Object?> get props => [message];
}
