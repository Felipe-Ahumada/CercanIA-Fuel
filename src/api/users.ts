import apiClient from './client';
import type { Page, UserCreateRequest, UserResponse } from '../types';

export const usersApi = {
  list: (page = 0, size = 50) =>
    apiClient
      .get<Page<UserResponse>>('/api/v1/usuarios', { params: { page, size } })
      .then((r) => r.data),

  findById: (id: string) =>
    apiClient.get<UserResponse>(`/api/v1/usuarios/${id}`).then((r) => r.data),

  create: (req: UserCreateRequest) =>
    apiClient.post<UserResponse>('/api/v1/usuarios', req).then((r) => r.data),

  delete: (id: string) =>
    apiClient.delete(`/api/v1/usuarios/${id}`),
};
