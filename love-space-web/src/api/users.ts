import { apiClient } from "./client";
import type { Role } from "./auth";

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UserItem {
  id: string;
  username: string;
  nickname?: string | null;
  role: Role;
  enable: boolean;
  createdAt: string;
}

export interface UserDetail extends UserItem {
  updatedAt?: string;
}

export interface UserQuery {
  username?: string;
  role?: Role | "";
  enable?: boolean | "";
  createdAtFrom?: string;
  createdAtTo?: string;
  page?: number;
  size?: number;
}

export interface UserCreateRequest {
  username: string;
  password: string;
  nickname?: string;
}

export interface PasswordResetRequest {
  newPassword: string;
}

function buildParams(query: UserQuery): Record<string, string | number | boolean> {
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

export async function pageUsers(query: UserQuery): Promise<Page<UserItem>> {
  const { data } = await apiClient.get<Page<UserItem>>("/api/admin/users", {
    params: buildParams(query),
  });
  return data;
}

export async function createUser(req: UserCreateRequest): Promise<UserDetail> {
  const { data } = await apiClient.post<UserDetail>("/api/admin/users", req);
  return data;
}

export async function getUser(id: string): Promise<UserDetail> {
  const { data } = await apiClient.get<UserDetail>(`/api/admin/users/${id}`);
  return data;
}

export async function enableUser(id: string): Promise<void> {
  await apiClient.put<void>(`/api/admin/users/${id}/enable`);
}

export async function disableUser(id: string): Promise<void> {
  await apiClient.put<void>(`/api/admin/users/${id}/disable`);
}

export async function resetPassword(id: string, req: PasswordResetRequest): Promise<void> {
  await apiClient.put<void>(`/api/admin/users/${id}/password`, req);
}
