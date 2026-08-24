import { apiClient } from "./client";
import type { Page } from "./types";
import type { ImageResponse } from "../types/image";

export interface FeaturedItem {
  id: string;
  cityId: string;
  banner: ImageResponse | null;
  description: string | null;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface FeaturedItemQuery {
  cityId?: string;
  page?: number;
  size?: number;
}

export interface FeaturedItemUpsertRequest {
  /** 关联地图，创建后不可变。 */
  cityId: string;
  /** banner 图片 objectKey。 */
  banner: string;
  description?: string | null;
  online?: boolean;
}

export async function pageFeaturedItems(query: FeaturedItemQuery): Promise<Page<FeaturedItem>> {
  const params: Record<string, string | number> = {};
  if (query.cityId) params.cityId = query.cityId;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<FeaturedItem>>("/api/admin/featured-items/page", {
    params,
  });
  return data;
}

export async function createFeaturedItem(req: FeaturedItemUpsertRequest): Promise<FeaturedItem> {
  const { data } = await apiClient.post<FeaturedItem>("/api/admin/featured-items", req);
  return data;
}

export async function updateFeaturedItem(
  id: string,
  req: FeaturedItemUpsertRequest,
): Promise<FeaturedItem> {
  const { data } = await apiClient.put<FeaturedItem>(`/api/admin/featured-items/${id}`, req);
  return data;
}

export async function deleteFeaturedItem(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/featured-items/${id}`);
}

export async function setFeaturedItemOnline(id: string, online: boolean): Promise<FeaturedItem> {
  const { data } = await apiClient.put<FeaturedItem>(`/api/admin/featured-items/${id}/online`, {
    online,
  });
  return data;
}
