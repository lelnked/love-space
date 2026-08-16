import { apiClient } from "./client";
import type { ImageResponse } from "../types/image";

export interface ArticleCategory {
  id: string;
  name: string;
  icon: ImageResponse | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface ArticleCategoryUpsertRequest {
  name: string;
  /** icon 图片 objectKey。 */
  icon: string;
  sortOrder?: number;
}

export async function listArticleCategories(): Promise<ArticleCategory[]> {
  const { data } = await apiClient.get<ArticleCategory[]>("/api/admin/article-categories");
  return data;
}

export async function createArticleCategory(
  req: ArticleCategoryUpsertRequest,
): Promise<ArticleCategory> {
  const { data } = await apiClient.post<ArticleCategory>("/api/admin/article-categories", req);
  return data;
}

export async function updateArticleCategory(
  id: string,
  req: ArticleCategoryUpsertRequest,
): Promise<ArticleCategory> {
  const { data } = await apiClient.put<ArticleCategory>(`/api/admin/article-categories/${id}`, req);
  return data;
}

export async function deleteArticleCategory(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/article-categories/${id}`);
}
