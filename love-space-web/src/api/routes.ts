import { apiClient } from "./client";
import type { Page } from "./types";
import type { ImageResponse } from "../types/image";

export interface RouteItem {
  id: string;
  sortOrder: number;
  title: string;
  thumbnail: ImageResponse;
  ambassadorId: string;
  ambassadorName: string | null;
  cityName: string;
  spotCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface RouteSpot {
  name: string;
  image: ImageResponse;
  introduction: string;
}

export interface RouteDetail {
  id: string;
  sortOrder: number;
  title: string;
  cityName: string;
  ambassadorNote: string | null;
  thumbnail: ImageResponse;
  images: ImageResponse[];
  travelTime: string | null;
  season: string | null;
  travelStatus: string | null;
  ambassadorId: string;
  ambassadorName: string;
  spots: RouteSpot[];
  createdAt: string;
  updatedAt: string;
}

export interface RouteQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export interface RouteSpotRequest {
  name: string;
  /** 地点图片 objectKey，必填。 */
  image: string;
  introduction: string;
}

export interface RouteUpsertRequest {
  cityName: string;
  sortOrder?: number;
  title: string;
  ambassadorNote?: string | null;
  /** 缩略图 objectKey，必填。 */
  thumbnail: string;
  /** 图片 objectKey 列表，至少 1 张。 */
  images: string[];
  travelTime?: string | null;
  season?: string | null;
  travelStatus?: string | null;
  ambassadorId: string;
  spots?: RouteSpotRequest[];
}

export async function pageRoutes(query: RouteQuery): Promise<Page<RouteItem>> {
  const params: Record<string, string | number> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<RouteItem>>("/api/admin/routes/page", { params });
  return data;
}

export async function getRoute(id: string): Promise<RouteDetail> {
  const { data } = await apiClient.get<RouteDetail>(`/api/admin/routes/${id}`);
  return data;
}

export async function createRoute(req: RouteUpsertRequest): Promise<RouteDetail> {
  const { data } = await apiClient.post<RouteDetail>("/api/admin/routes", req);
  return data;
}

export async function updateRoute(id: string, req: RouteUpsertRequest): Promise<RouteDetail> {
  const { data } = await apiClient.put<RouteDetail>(`/api/admin/routes/${id}`, req);
  return data;
}

export async function deleteRoute(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/routes/${id}`);
}
