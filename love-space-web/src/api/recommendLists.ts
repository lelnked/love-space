import { apiClient } from "./client";
import type { Page } from "./types";
import type { ImageResponse } from "../types/image";

export interface RecommendListItem {
  id: string;
  title: string;
  introduction: string | null;
  cityId: string;
  sortOrder: number;
  merchantCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface RecommendListMerchant {
  merchantId: string;
  name: string;
  logo: ImageResponse | null;
  address: string;
  online: boolean;
  sortOrder: number;
}

export interface RecommendListDetail {
  id: string;
  title: string;
  introduction: string | null;
  cityId: string;
  sortOrder: number;
  merchants: RecommendListMerchant[];
  createdAt: string;
  updatedAt: string;
}

export interface RecommendListQuery {
  cityId?: string;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface RecommendListUpsertRequest {
  title: string;
  introduction?: string | null;
  cityId?: string;
  sortOrder?: number;
}

export interface RecommendListMerchantItemRequest {
  merchantId: string;
  sortOrder: number;
}

export async function pageRecommendLists(
  query: RecommendListQuery,
): Promise<Page<RecommendListItem>> {
  const params: Record<string, string | number> = {};
  if (query.cityId) params.cityId = query.cityId;
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<RecommendListItem>>(
    "/api/admin/recommend-lists/page",
    { params },
  );
  return data;
}

export async function getRecommendList(id: string): Promise<RecommendListDetail> {
  const { data } = await apiClient.get<RecommendListDetail>(`/api/admin/recommend-lists/${id}`);
  return data;
}

export async function createRecommendList(
  req: RecommendListUpsertRequest,
): Promise<RecommendListDetail> {
  const { data } = await apiClient.post<RecommendListDetail>("/api/admin/recommend-lists", req);
  return data;
}

export async function updateRecommendList(
  id: string,
  req: RecommendListUpsertRequest,
): Promise<RecommendListDetail> {
  const { data } = await apiClient.put<RecommendListDetail>(
    `/api/admin/recommend-lists/${id}`,
    req,
  );
  return data;
}

export async function deleteRecommendList(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/recommend-lists/${id}`);
}

export async function replaceRecommendListMerchants(
  id: string,
  items: RecommendListMerchantItemRequest[],
): Promise<RecommendListDetail> {
  const { data } = await apiClient.put<RecommendListDetail>(
    `/api/admin/recommend-lists/${id}/merchants`,
    items,
  );
  return data;
}
