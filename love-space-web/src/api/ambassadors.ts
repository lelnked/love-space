import { apiClient } from "./client";
import type { Page } from "./types";
import type { ImageResponse } from "../types/image";

export interface AmbassadorItem {
  id: string;
  avatar: ImageResponse | null;
  name: string;
  tags: string[];
  weight: number;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AmbassadorQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export interface AmbassadorUpsertRequest {
  /** 头像 objectKey，必填。 */
  avatar: string;
  name: string;
  /** 标签，最多 3 条。 */
  tags?: string[];
  /** 排序权重，app 端列表按其倒序取前 N 条；默认 0。 */
  weight?: number;
  online?: boolean;
}

export async function pageAmbassadors(query: AmbassadorQuery): Promise<Page<AmbassadorItem>> {
  const params: Record<string, string | number> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<AmbassadorItem>>("/api/admin/ambassadors/page", {
    params,
  });
  return data;
}

export async function getAmbassador(id: string): Promise<AmbassadorItem> {
  const { data } = await apiClient.get<AmbassadorItem>(`/api/admin/ambassadors/${id}`);
  return data;
}

export async function createAmbassador(req: AmbassadorUpsertRequest): Promise<AmbassadorItem> {
  const { data } = await apiClient.post<AmbassadorItem>("/api/admin/ambassadors", req);
  return data;
}

export async function updateAmbassador(
  id: string,
  req: AmbassadorUpsertRequest,
): Promise<AmbassadorItem> {
  const { data } = await apiClient.put<AmbassadorItem>(`/api/admin/ambassadors/${id}`, req);
  return data;
}

export async function deleteAmbassador(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/ambassadors/${id}`);
}

export async function setAmbassadorOnline(id: string, online: boolean): Promise<AmbassadorItem> {
  const { data } = await apiClient.put<AmbassadorItem>(`/api/admin/ambassadors/${id}/online`, {
    online,
  });
  return data;
}
