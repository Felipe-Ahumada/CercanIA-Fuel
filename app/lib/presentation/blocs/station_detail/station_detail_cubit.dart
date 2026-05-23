import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/usecases/station_usecases.dart';
import 'station_detail_state.dart';

class StationDetailCubit extends Cubit<StationDetailState> {
  final GetStationDetailUseCase _getDetail;

  StationDetailCubit(this._getDetail) : super(StationDetailInitial());

  Future<void> load(String stationId) async {
    emit(StationDetailLoading());
    final result = await _getDetail(stationId);
    result.fold(
      (failure) => emit(StationDetailError(failure.message)),
      (station) => emit(StationDetailLoaded(station)),
    );
  }
}
