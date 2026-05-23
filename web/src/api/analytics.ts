import apiClient from './client';
import type { AnalyticsResponse } from '../types';

export interface AnalyticsParams {
  startDate?: string; // YYYY-MM-DD
  endDate?:   string;
  regionId?:  number;
}

export const analyticsApi = {
  get: (params?: AnalyticsParams) =>
    apiClient
      .get<AnalyticsResponse>('/api/v1/admin/analytics', { params })
      .then((r) => r.data),
};
