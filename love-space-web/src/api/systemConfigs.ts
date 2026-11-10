import { apiClient } from "./client";
import type { Page } from "./types";

/** 系统配置（key 由系统预置，value 结构由 key 决定）。 */
export interface SystemConfigItem {
  id: string;
  key: string;
  name: string;
  value: unknown;
  description: string | null;
  createdAt: string;
  updatedAt: string | null;
}

/** ios_llm_popup 配置项的 value 结构。 */
export interface IosLlmPopupConfigValue {
  enabled: boolean;
}

export interface SystemConfigQuery {
  keyword?: string;
  page?: number;
  size?: number;
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

export async function updateSystemConfig(id: string, body: SystemConfigUpdateRequest): Promise<SystemConfigItem> {
  const { data } = await apiClient.put<SystemConfigItem>(`/api/admin/system-configs/${id}`, body);
  return data;
}
