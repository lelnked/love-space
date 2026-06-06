import { apiClient } from "./client";

export interface CategoryItem {
  id: string;
  name: string;
  sortOrder: number;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryUpsertRequest {
  name: string;
  sortOrder: number;
  online: boolean;
}

export interface CategoryQuery {
  name?: string;
}

function buildParams(query: CategoryQuery): Record<string, string> {
  const params: Record<string, string> = {};
  if (query.name) params.name = query.name;
  return params;
}

export async function listCategories(query: CategoryQuery = {}): Promise<CategoryItem[]> {
  const { data } = await apiClient.get<CategoryItem[]>("/api/admin/categories", {
    params: buildParams(query),
  });
  return data;
}

export async function getCategory(id: string): Promise<CategoryItem> {
  const { data } = await apiClient.get<CategoryItem>(`/api/admin/categories/${id}`);
  return data;
}

export async function createCategory(req: CategoryUpsertRequest): Promise<CategoryItem> {
  const { data } = await apiClient.post<CategoryItem>("/api/admin/categories", req);
  return data;
}

export async function updateCategory(id: string, req: CategoryUpsertRequest): Promise<CategoryItem> {
  const { data } = await apiClient.put<CategoryItem>(`/api/admin/categories/${id}`, req);
  return data;
}

export async function deleteCategory(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/categories/${id}`);
}

export async function setCategoryOnline(id: string, online: boolean): Promise<CategoryItem> {
  const { data } = await apiClient.put<CategoryItem>(`/api/admin/categories/${id}/online`, { online });
  return data;
}
