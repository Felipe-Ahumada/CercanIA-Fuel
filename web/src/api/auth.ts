import apiClient from './client';
import type { AuthResponse, LoginRequest, MeResponse } from '../types';

export const authApi = {
  login: (req: LoginRequest) =>
    apiClient.post<AuthResponse>('/api/v1/auth/login', req).then((r) => r.data),

  me: () =>
    apiClient.get<MeResponse>('/api/v1/auth/me').then((r) => r.data),
};
