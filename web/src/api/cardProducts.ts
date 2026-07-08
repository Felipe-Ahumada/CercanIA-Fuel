import apiClient from './client';
import type { CardProductResponse } from '../types';

export const cardProductsApi = {
  list: (bankId?: number) =>
    apiClient
      .get<CardProductResponse[]>('/api/v1/tarjetas-producto', {
        params: bankId ? { bankId } : {},
      })
      .then((r) => r.data),

  create: (data: { bankId: number; name: string; cardType: string }) =>
    apiClient.post<CardProductResponse>('/api/v1/tarjetas-producto', data).then((r) => r.data),
};
