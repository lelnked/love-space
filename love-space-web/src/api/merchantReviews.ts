import { apiClient } from "./client";

export interface MerchantReviewItem {
  id: string;
  merchantId: string;
  nickname: string;
  title: string;
  content: string;
  sortOrder: number;
  recommended: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface MerchantReviewUpsertRequest {
  nickname: string;
  title: string;
  content: string;
  sortOrder: number;
  recommended: boolean;
}

export async function listMerchantReviews(merchantId: string): Promise<MerchantReviewItem[]> {
  const { data } = await apiClient.get<MerchantReviewItem[]>(
    `/api/admin/merchants/${merchantId}/reviews`,
  );
  return data;
}

export async function getMerchantReview(
  merchantId: string,
  id: string,
): Promise<MerchantReviewItem> {
  const { data } = await apiClient.get<MerchantReviewItem>(
    `/api/admin/merchants/${merchantId}/reviews/${id}`,
  );
  return data;
}

export async function createMerchantReview(
  merchantId: string,
  req: MerchantReviewUpsertRequest,
): Promise<MerchantReviewItem> {
  const { data } = await apiClient.post<MerchantReviewItem>(
    `/api/admin/merchants/${merchantId}/reviews`,
    req,
  );
  return data;
}

export async function updateMerchantReview(
  merchantId: string,
  id: string,
  req: MerchantReviewUpsertRequest,
): Promise<MerchantReviewItem> {
  const { data } = await apiClient.put<MerchantReviewItem>(
    `/api/admin/merchants/${merchantId}/reviews/${id}`,
    req,
  );
  return data;
}

export async function deleteMerchantReview(merchantId: string, id: string): Promise<void> {
  await apiClient.delete<void>(`/api/admin/merchants/${merchantId}/reviews/${id}`);
}
