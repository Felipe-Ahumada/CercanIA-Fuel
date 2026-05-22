import apiClient from './client';
import type { Page, StationSummaryResponse } from '../types';

export const stationsApi = {
  list: (page = 0, size = 100) =>
    apiClient
      .get<Page<StationSummaryResponse>>('/api/v1/bencineras', { params: { page, size } })
      .then((r) => r.data),
};
