import { apiClient } from "./client";
import type { Page } from "./managers";
import type { ImageResponse } from "../types/image";

export type BannerType = "CITY";

export interface BannerListItem {
  id: string;
  name: string;
  type: BannerType;
  imageUrls: ImageResponse[];
  link: string;
  linkedCityName: string | null;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export type BannerDetail = BannerListItem;

export interface BannerQuery {
  keyword?: string;
  type?: BannerType | "";
  online?: boolean | "";
  page?: number;
  size?: number;
}

export interface BannerUpsertRequest {
  name: string;
  type: BannerType;
  imageUrls: string[];
  link: string;
}

function buildParams(query: BannerQuery): Record<string, string | number | boolean> {
  const params: Record<string, string | number | boolean> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.type) params.type = query.type;
  if (query.online === true || query.online === false) params.online = query.online;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  return params;
}

export async function pageBanners(query: BannerQuery = {}): Promise<Page<BannerListItem>> {
  const { data } = await apiClient.get<Page<BannerListItem>>("/api/admin/banners/page", {
    params: buildParams(query),
  });
  return data;
}

export async function getBanner(id: string): Promise<BannerDetail> {
  const { data } = await apiClient.get<BannerDetail>(`/api/admin/banners/${id}`);
  return data;
}

export async function createBanner(req: BannerUpsertRequest): Promise<BannerDetail> {
  const { data } = await apiClient.post<BannerDetail>("/api/admin/banners", req);
  return data;
}

export async function updateBanner(id: string, req: BannerUpsertRequest): Promise<BannerDetail> {
  const { data } = await apiClient.put<BannerDetail>(`/api/admin/banners/${id}`, req);
  return data;
}

export async function deleteBanner(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/banners/${id}`);
}

export async function setBannerOnline(id: string, online: boolean): Promise<BannerDetail> {
  const { data } = await apiClient.post<BannerDetail>(`/api/admin/banners/${id}/online`, { online });
  return data;
}
