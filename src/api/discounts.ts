import apiClient from './client';
import type { DiscountCreateRequest, DiscountResponse, DiscountUpdateRequest } from '../types';

export const discountsApi = {
  listAll: () =>
    apiClient.get<DiscountResponse[]>('/api/v1/descuentos/catalogo').then((r) => r.data),

  findById: (id: number) =>
    apiClient.get<DiscountResponse>(`/api/v1/descuentos/${id}`).then((r) => r.data),

  create: (req: DiscountCreateRequest) =>
    apiClient.post<DiscountResponse>('/api/v1/descuentos', req).then((r) => r.data),

  update: (id: number, req: DiscountUpdateRequest) =>
    apiClient.put<DiscountResponse>(`/api/v1/descuentos/${id}`, req).then((r) => r.data),

  delete: (id: number) =>
    apiClient.delete(`/api/v1/descuentos/${id}`),
};
