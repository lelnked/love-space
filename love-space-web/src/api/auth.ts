import { apiClient } from "./client";

export type Role = "ADMIN" | "MEMBER";

export interface CurrentUser {
  id: string;
  username: string;
  nickname?: string | null;
  role: Role;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  manager: CurrentUser;
}

export async function login(req: LoginRequest): Promise<LoginResponse> {
  const { data } = await apiClient.post<LoginResponse>("/api/admin/auth/login", req);
  return data;
}

export async function logout(): Promise<void> {
  await apiClient.post<void>("/api/admin/auth/logout");
}

export async function me(): Promise<CurrentUser> {
  const { data } = await apiClient.get<CurrentUser>("/api/admin/auth/me");
  return data;
}
