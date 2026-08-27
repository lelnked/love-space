import { apiClient } from "./client";
import type { Page, Period } from "./types";
import type { ImageResponse } from "../types/image";

/** 周期推荐的内容类型。 */
export type FeaturedCycleItemType = "ACTIVITY" | "ROUTE" | "ARTICLE";

export const CYCLE_ITEM_TYPE_LABELS: Record<FeaturedCycleItemType, string> = {
  ACTIVITY: "tripperclub活动",
  ROUTE: "路线体验",
  ARTICLE: "周期生活法",
};

export const CYCLE_ITEM_TYPES: FeaturedCycleItemType[] = ["ACTIVITY", "ROUTE", "ARTICLE"];

export interface FeaturedCycleItem {
  id: string;
  phase: Period;
  type: FeaturedCycleItemType;
  sortOrder: number;
  online: boolean;
  activityId: string | null;
  routeId: string | null;
  articleId: string | null;
  /** 关联实体标题；实体已被删除时为 null。 */
  relatedTitle: string | null;
  title: string | null;
  subtitle: string | null;
  description: string | null;
  note: string | null;
  banner: ImageResponse | null;
  createdAt: string;
  updatedAt: string;
}

export interface FeaturedCycleItemQuery {
  phase?: Period;
  type?: FeaturedCycleItemType;
  page?: number;
  size?: number;
}

/** 三种类型共用的请求体；必填性由后端按 type 分派校验。 */
export interface FeaturedCycleItemUpsertRequest {
  /** 所属周期，创建后不可变。 */
  phase: Period;
  /** 内容类型，创建后不可变。 */
  type: FeaturedCycleItemType;
  /** banner 图片 objectKey。 */
  banner: string;
  sortOrder?: number;
  online?: boolean;
  /** type=ACTIVITY 必填。 */
  activityId?: string | null;
  /** type=ROUTE 必填。 */
  routeId?: string | null;
  /** type=ARTICLE 必填。 */
  articleId?: string | null;
  /** type=ROUTE / ARTICLE 必填。 */
  title?: string | null;
  /** type=ROUTE 必填。 */
  subtitle?: string | null;
  /** type=ACTIVITY / ROUTE 必填。 */
  description?: string | null;
  /** type=ACTIVITY 选填。 */
  note?: string | null;
}

export async function pageFeaturedCycleItems(
  query: FeaturedCycleItemQuery,
): Promise<Page<FeaturedCycleItem>> {
  const params: Record<string, string | number> = {};
  if (query.phase) params.phase = query.phase;
  if (query.type) params.type = query.type;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<FeaturedCycleItem>>(
    "/api/admin/featured-cycle-items/page",
    { params },
  );
  return data;
}

export async function getFeaturedCycleItem(id: string): Promise<FeaturedCycleItem> {
  const { data } = await apiClient.get<FeaturedCycleItem>(`/api/admin/featured-cycle-items/${id}`);
  return data;
}

export async function createFeaturedCycleItem(
  req: FeaturedCycleItemUpsertRequest,
): Promise<FeaturedCycleItem> {
  const { data } = await apiClient.post<FeaturedCycleItem>("/api/admin/featured-cycle-items", req);
  return data;
}

export async function updateFeaturedCycleItem(
  id: string,
  req: FeaturedCycleItemUpsertRequest,
): Promise<FeaturedCycleItem> {
  const { data } = await apiClient.put<FeaturedCycleItem>(
    `/api/admin/featured-cycle-items/${id}`,
    req,
  );
  return data;
}

export async function deleteFeaturedCycleItem(id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/featured-cycle-items/${id}`);
}

export async function setFeaturedCycleItemOnline(
  id: string,
  online: boolean,
): Promise<FeaturedCycleItem> {
  const { data } = await apiClient.put<FeaturedCycleItem>(
    `/api/admin/featured-cycle-items/${id}/online`,
    { online },
  );
  return data;
}
