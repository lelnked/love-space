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

export async function listCategories(): Promise<CategoryItem[]> {
  const { data } = await apiClient.get<CategoryItem[]>("/api/admin/categories");
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
