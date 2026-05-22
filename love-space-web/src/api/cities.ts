import { apiClient } from "./client";

export interface CityItem {
  id: string;
  chineseName: string;
  englishName: string;
  chineseProvince: string;
  englishProvince: string;
  backgroundImage: string | null;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export type CityDetail = CityItem;

export interface CityQuery {
  online?: boolean | "";
  name?: string;
}

export interface CityUpsertRequest {
  chineseName: string;
  englishName: string;
  chineseProvince: string;
  englishProvince: string;
  backgroundImage?: string | null;
  online?: boolean;
}

function buildParams(query: CityQuery): Record<string, string | boolean> {
  const params: Record<string, string | boolean> = {};
  if (query.name) params.name = query.name;
  if (query.online === true || query.online === false) params.online = query.online;
  return params;
}

export async function listCities(query: CityQuery = {}): Promise<CityItem[]> {
  const { data } = await apiClient.get<CityItem[]>("/api/admin/cities", {
    params: buildParams(query),
  });
  return data;
}

export async function listOnlineCities(name?: string): Promise<CityItem[]> {
  return listCities({ online: true, name });
}

export async function getCity(id: string): Promise<CityDetail> {
  const { data } = await apiClient.get<CityDetail>(`/api/admin/cities/${id}`);
  return data;
}

export async function createCity(req: CityUpsertRequest): Promise<CityDetail> {
  const { data } = await apiClient.post<CityDetail>("/api/admin/cities", req);
  return data;
}

export async function updateCity(id: string, req: CityUpsertRequest): Promise<CityDetail> {
  const { data } = await apiClient.put<CityDetail>(`/api/admin/cities/${id}`, req);
  return data;
}

export async function deleteCity(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/cities/${id}`);
}

export async function setCityOnline(id: string, online: boolean): Promise<CityDetail> {
  const { data } = await apiClient.put<CityDetail>(`/api/admin/cities/${id}/online`, { online });
  return data;
}
