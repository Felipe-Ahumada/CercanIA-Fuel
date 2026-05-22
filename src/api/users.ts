import apiClient from './client';
import type { Page, UserResponse, VehicleResponse, TransactionResponse } from '../types';

export const usersApi = {
  list: (page = 0, size = 200) =>
    apiClient
      .get<Page<UserResponse>>('/api/v1/usuarios', { params: { page, size } })
      .then((r) => r.data),

  findById: (id: string) =>
    apiClient.get<UserResponse>(`/api/v1/usuarios/${id}`).then((r) => r.data),

  deactivate: (id: string) =>
    apiClient.patch(`/api/v1/usuarios/${id}/active`, null, { params: { active: false } }),

  activate: (id: string) =>
    apiClient.patch(`/api/v1/usuarios/${id}/active`, null, { params: { active: true } }),

  vehicles: (id: string) =>
    apiClient.get<VehicleResponse[]>(`/api/v1/usuarios/${id}/vehiculos`).then((r) => r.data),

  transactions: (id: string, page = 0, size = 50) =>
    apiClient.get<Page<TransactionResponse>>('/api/v1/transacciones', {
      params: { userId: id, page, size },
    }).then((r) => r.data),
};
