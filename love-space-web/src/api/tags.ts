import { apiClient } from "./client";

export interface TagItem {
  id: string;
  name: string;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TagQuery {
  online?: boolean | "";
  name?: string;
}

export interface TagUpsertRequest {
  name: string;
}

function buildParams(query: TagQuery): Record<string, string | boolean> {
  const params: Record<string, string | boolean> = {};
  if (query.name) params.name = query.name;
  if (query.online === true || query.online === false) params.online = query.online;
  return params;
}

export async function listTags(query: TagQuery = {}): Promise<TagItem[]> {
  const { data } = await apiClient.get<TagItem[]>("/api/admin/tags", {
    params: buildParams(query),
  });
  return data;
}

export async function createTag(req: TagUpsertRequest): Promise<TagItem> {
  const { data } = await apiClient.post<TagItem>("/api/admin/tags", req);
  return data;
}

export async function updateTag(id: string, req: TagUpsertRequest): Promise<TagItem> {
  const { data } = await apiClient.put<TagItem>(`/api/admin/tags/${id}`, req);
  return data;
}

export async function setTagOnline(id: string, online: boolean): Promise<TagItem> {
  const { data } = await apiClient.put<TagItem>(`/api/admin/tags/${id}/online`, { online });
  return data;
}

export async function deleteTag(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/tags/${id}`);
}
