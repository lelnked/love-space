import { apiClient } from "./client";
import type { Role } from "./auth";

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ManagerItem {
  id: string;
  username: string;
  nickname?: string | null;
  role: Role;
  enable: boolean;
  createdAt: string;
}

export interface ManagerDetail extends ManagerItem {
  updatedAt?: string;
}

export interface ManagerQuery {
  username?: string;
  role?: Role | "";
  enable?: boolean | "";
  createdAtFrom?: string;
  createdAtTo?: string;
  page?: number;
  size?: number;
}

export interface ManagerCreateRequest {
  username: string;
  password: string;
  nickname?: string;
}

export interface PasswordResetRequest {
  newPassword: string;
}

function buildParams(query: ManagerQuery): Record<string, string | number | boolean> {
  const params: Record<string, string | number | boolean> = {};
  if (query.username) params.username = query.username;
  if (query.role) params.role = query.role;
  if (query.enable !== undefined && query.enable !== "") params.enable = query.enable;
  if (query.createdAtFrom) params.createdAtFrom = query.createdAtFrom;
  if (query.createdAtTo) params.createdAtTo = query.createdAtTo;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  return params;
}

export async function pageManagers(query: ManagerQuery): Promise<Page<ManagerItem>> {
  const { data } = await apiClient.get<Page<ManagerItem>>("/api/admin/managers", {
    params: buildParams(query),
  });
  return data;
}

export async function createManager(req: ManagerCreateRequest): Promise<ManagerDetail> {
  const { data } = await apiClient.post<ManagerDetail>("/api/admin/managers", req);
  return data;
}

export async function getManager(id: string): Promise<ManagerDetail> {
  const { data } = await apiClient.get<ManagerDetail>(`/api/admin/managers/${id}`);
  return data;
}

export async function enableManager(id: string): Promise<void> {
  await apiClient.put<void>(`/api/admin/managers/${id}/enable`);
}

export async function disableManager(id: string): Promise<void> {
  await apiClient.put<void>(`/api/admin/managers/${id}/disable`);
}

export async function resetPassword(id: string, req: PasswordResetRequest): Promise<void> {
  await apiClient.put<void>(`/api/admin/managers/${id}/password`, req);
}
