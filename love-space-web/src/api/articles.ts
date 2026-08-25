import { apiClient } from "./client";
import type { Page } from "./types";
import type { ImageResponse } from "../types/image";

export interface ArticleItem {
  id: string;
  image: ImageResponse | null;
  title: string;
  /** 封面标题，列表展示；未设置时为 null。 */
  coverTitle: string | null;
  subtitle: string | null;
  intro: string | null;
  tags: string[];
  sortOrder: number;
  categoryIds: string[];
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ArticleDetail {
  id: string;
  image: ImageResponse | null;
  title: string;
  coverTitle: string | null;
  subtitle: string | null;
  intro: string | null;
  tags: string[];
  /** 富文本 HTML，img src 为签名 URL。 */
  contentHtml: string | null;
  sortOrder: number;
  categoryIds: string[];
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ArticleQuery {
  categoryId?: string;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface ArticleUpsertRequest {
  /** 文章图片 objectKey。 */
  image: string;
  title: string;
  coverTitle?: string | null;
  subtitle?: string | null;
  intro?: string | null;
  tags?: string[];
  contentHtml?: string | null;
  sortOrder?: number;
  categoryIds?: string[];
  online?: boolean;
}

export async function pageArticles(query: ArticleQuery): Promise<Page<ArticleItem>> {
  const params: Record<string, string | number> = {};
  if (query.categoryId) params.categoryId = query.categoryId;
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<ArticleItem>>("/api/admin/articles/page", { params });
  return data;
}

export async function getArticle(id: string): Promise<ArticleDetail> {
  const { data } = await apiClient.get<ArticleDetail>(`/api/admin/articles/${id}`);
  return data;
}

export async function createArticle(req: ArticleUpsertRequest): Promise<ArticleDetail> {
  const { data } = await apiClient.post<ArticleDetail>("/api/admin/articles", req);
  return data;
}

export async function updateArticle(id: string, req: ArticleUpsertRequest): Promise<ArticleDetail> {
  const { data } = await apiClient.put<ArticleDetail>(`/api/admin/articles/${id}`, req);
  return data;
}

export async function deleteArticle(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/articles/${id}`);
}

export async function setArticleOnline(id: string, online: boolean): Promise<ArticleDetail> {
  const { data } = await apiClient.put<ArticleDetail>(`/api/admin/articles/${id}/online`, {
    online,
  });
  return data;
}
