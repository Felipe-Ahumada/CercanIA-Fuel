import apiClient from './client';

export interface RegionDto  { id: number; name: string; }
export interface CommuneDto { id: number; name: string; }
export interface BrandDto   { id: number; name: string; }

export const catalogApi = {
  regions:  () =>
    apiClient.get<RegionDto[]>('/api/v1/regiones').then((r) => r.data),

  communes: (regionId: number) =>
    apiClient.get<CommuneDto[]>('/api/v1/comunas', { params: { regionId } }).then((r) => r.data),

  brands:   () =>
    apiClient.get<BrandDto[]>('/api/v1/marcas').then((r) => r.data),
};
