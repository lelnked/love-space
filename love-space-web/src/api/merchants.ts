import { apiClient } from "./client";
import type { Page, Period } from "./types";
import type { ImageResponse } from "../types/image";

export interface MerchantItem {
  id: string;
  name: string;
  logo: ImageResponse | null;
  address: string;
  cityId: string;
  categoryId: string | null;
  weight: number;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface MerchantDetail {
  id: string;
  name: string;
  logo: ImageResponse | null;
  address: string;
  longitude: string | number | null;
  latitude: string | number | null;
  cityId: string;
  categoryId: string | null;
  safetyEnvironmentScore: number;
  businessRightsScore: number;
  experienceFriendlyScore: number;
  socialContributionScore: number;
  story: string | null;
  weight: number;
  online: boolean;
  periods: Period[];
  tagIds: string[];
  images: ImageResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface MerchantQuery {
  cityId?: string;
  categoryId?: string;
  period?: Period | "";
  online?: boolean | "";
  name?: string;
  page?: number;
  size?: number;
}

export interface MerchantUpsertRequest {
  name: string;
  logo: string;
  address: string;
  longitude?: number | string | null;
  latitude?: number | string | null;
  cityId: string;
  categoryId?: string | null;
  safetyEnvironmentScore: number;
  businessRightsScore: number;
  experienceFriendlyScore: number;
  socialContributionScore: number;
  story?: string | null;
  weight?: number;
  online?: boolean;
  periods?: Period[];
  tagIds?: string[];
  images: string[];
}

function buildParams(query: MerchantQuery): Record<string, string | number | boolean> {
  const params: Record<string, string | number | boolean> = {};
  if (query.cityId) params.cityId = query.cityId;
  if (query.categoryId) params.categoryId = query.categoryId;
  if (query.period) params.period = query.period;
  if (query.online === true || query.online === false) params.online = query.online;
  if (query.name) params.name = query.name;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  return params;
}

export async function pageMerchants(query: MerchantQuery): Promise<Page<MerchantItem>> {
  const { data } = await apiClient.get<Page<MerchantItem>>("/api/admin/merchants/page", {
    params: buildParams(query),
  });
  return data;
}

export async function getMerchant(id: string): Promise<MerchantDetail> {
  const { data } = await apiClient.get<MerchantDetail>(`/api/admin/merchants/${id}`);
  return data;
}

export async function createMerchant(req: MerchantUpsertRequest): Promise<MerchantDetail> {
  const { data } = await apiClient.post<MerchantDetail>("/api/admin/merchants", req);
  return data;
}

export async function updateMerchant(id: string, req: MerchantUpsertRequest): Promise<MerchantDetail> {
  const { data } = await apiClient.put<MerchantDetail>(`/api/admin/merchants/${id}`, req);
  return data;
}

export async function deleteMerchant(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/merchants/${id}`);
}

export async function setMerchantOnline(id: string, online: boolean): Promise<MerchantDetail> {
  const { data } = await apiClient.put<MerchantDetail>(
    `/api/admin/merchants/${id}/online`,
    { online },
  );
  return data;
}
