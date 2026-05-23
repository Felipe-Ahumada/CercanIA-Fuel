import apiClient from './client';
import type { CardProductResponse } from '../types';

export const cardProductsApi = {
  list: (bankId?: number) =>
    apiClient
      .get<CardProductResponse[]>('/api/v1/tarjetas-producto', {
        params: bankId ? { bankId } : {},
      })
      .then((r) => r.data),
};
