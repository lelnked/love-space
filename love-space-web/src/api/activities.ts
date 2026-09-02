import { apiClient } from "./client";
import type { Page, Period } from "./types";
import type { ImageResponse } from "../types/image";

export type ActivityLevel = "L1" | "L2" | "L3";

export const ACTIVITY_LEVELS: ActivityLevel[] = ["L1", "L2", "L3"];

/** 活动适合周期文案（契约口径「月经期」；商户侧沿用 PERIOD_LABEL 的「经期」不动）。 */
export const ACTIVITY_PERIOD_LABEL: Record<Period, string> = {
  MENSTRUAL: "月经期",
  FOLLICULAR: "卵泡期",
  OVULATION: "排卵期",
  LUTEAL: "黄体期",
};

export interface ActivityItineraryItem {
  title: string;
  content: string;
}

export interface ActivityItem {
  id: string;
  cover: ImageResponse | null;
  title: string;
  subtitle: string | null;
  tags: string[];
  periods: Period[];
  level: ActivityLevel | null;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ActivityDetail {
  id: string;
  images: ImageResponse[];
  title: string;
  subtitle: string | null;
  tags: string[];
  periods: Period[];
  level: ActivityLevel | null;
  introduction: string | null;
  editorNote: string | null;
  gatheringPlace: string | null;
  dismissalPlace: string | null;
  transportation: string | null;
  visa: string | null;
  landscape: string | null;
  itinerary: ActivityItineraryItem[];
  /** 富文本 HTML，img src 为签名 URL。 */
  detailHtml: string | null;
  online: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ActivityQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export interface ActivityUpsertRequest {
  /** 图片 objectKey 列表，至少 1 张。 */
  images: string[];
  title: string;
  subtitle?: string | null;
  tags?: string[];
  periods?: Period[];
  level?: ActivityLevel | null;
  introduction?: string | null;
  editorNote?: string | null;
  gatheringPlace?: string | null;
  dismissalPlace?: string | null;
  transportation?: string | null;
  visa?: string | null;
  landscape?: string | null;
  itinerary?: ActivityItineraryItem[];
  detailHtml?: string | null;
  online?: boolean;
}

export async function pageActivities(query: ActivityQuery): Promise<Page<ActivityItem>> {
  const params: Record<string, string | number> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<ActivityItem>>("/api/admin/activities/page", {
    params,
  });
  return data;
}

export async function getActivity(id: string): Promise<ActivityDetail> {
  const { data } = await apiClient.get<ActivityDetail>(`/api/admin/activities/${id}`);
  return data;
}

export async function createActivity(req: ActivityUpsertRequest): Promise<ActivityDetail> {
  const { data } = await apiClient.post<ActivityDetail>("/api/admin/activities", req);
  return data;
}

export async function updateActivity(
  id: string,
  req: ActivityUpsertRequest,
): Promise<ActivityDetail> {
  const { data } = await apiClient.put<ActivityDetail>(`/api/admin/activities/${id}`, req);
  return data;
}

export async function deleteActivity(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/activities/${id}`);
}

export async function setActivityOnline(id: string, online: boolean): Promise<ActivityDetail> {
  const { data } = await apiClient.put<ActivityDetail>(`/api/admin/activities/${id}/online`, {
    online,
  });
  return data;
}
