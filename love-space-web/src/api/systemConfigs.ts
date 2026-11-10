import { apiClient } from "./client";
import type { Page } from "./types";

/** 系统配置（value 为任意 JSON 原值）。 */
export interface SystemConfigItem {
  id: string;
  key: string;
  name: string;
  value: unknown;
  description: string | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface SystemConfigQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export interface SystemConfigCreateRequest {
  key: string;
  name: string;
  value: unknown;
  description?: string | null;
}

export interface SystemConfigUpdateRequest {
  name: string;
  value: unknown;
  description?: string | null;
}

export async function listSystemConfigs(query: SystemConfigQuery = {}): Promise<Page<SystemConfigItem>> {
  const params: Record<string, string | number> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<SystemConfigItem>>("/api/admin/system-configs/page", { params });
  return data;
}

export async function getSystemConfig(id: string): Promise<SystemConfigItem> {
  const { data } = await apiClient.get<SystemConfigItem>(`/api/admin/system-configs/${id}`);
  return data;
}

export async function createSystemConfig(body: SystemConfigCreateRequest): Promise<SystemConfigItem> {
  const { data } = await apiClient.post<SystemConfigItem>("/api/admin/system-configs", body);
  return data;
}

export async function updateSystemConfig(id: string, body: SystemConfigUpdateRequest): Promise<SystemConfigItem> {
  const { data } = await apiClient.put<SystemConfigItem>(`/api/admin/system-configs/${id}`, body);
  return data;
}

export async function deleteSystemConfig(id: string): Promise<void> {
  await apiClient.delete(`/api/admin/system-configs/${id}`);
}
