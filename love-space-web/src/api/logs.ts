import { apiClient } from "./client";
import type { Page } from "./managers";

export interface OperationLogItem {
  id: string;
  username: string;
  module: string;
  action: string;
  target: string | null;
  createdAt: string;
}

export interface OperationLogQuery {
  username?: string;
  module?: string;
  createdAtFrom?: string;
  createdAtTo?: string;
  page?: number;
  size?: number;
}

function buildParams(query: OperationLogQuery): Record<string, string | number> {
  const params: Record<string, string | number> = {};
  if (query.username) params.username = query.username;
  if (query.module) params.module = query.module;
  if (query.createdAtFrom) params.createdAtFrom = query.createdAtFrom;
  if (query.createdAtTo) params.createdAtTo = query.createdAtTo;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  return params;
}

export async function pageOperationLogs(query: OperationLogQuery): Promise<Page<OperationLogItem>> {
  const { data } = await apiClient.get<Page<OperationLogItem>>("/api/admin/logs", {
    params: buildParams(query),
  });
  return data;
}
