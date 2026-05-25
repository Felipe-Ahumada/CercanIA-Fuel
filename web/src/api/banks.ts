import apiClient from './client';
import type { BankResponse, Page } from '../types';

export const banksApi = {
  list: (page = 0, size = 100) =>
    apiClient
      .get<Page<BankResponse>>('/api/v1/bancos', { params: { page, size } })
      .then((r) => r.data),

  create: (data: { name: string; code: string }) =>
    apiClient.post<BankResponse>('/api/v1/bancos', data).then((r) => r.data),
};
