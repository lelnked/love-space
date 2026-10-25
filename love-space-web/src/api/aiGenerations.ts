import { apiClient } from "./client";
import type { Page } from "./types";

export type AiGenerationStatus =
  | "PROCESSING"
  | "SUCCESS"
  | "REFUSED"
  | "EMPTY"
  | "FORMAT_ERROR"
  | "TIMEOUT"
  | "RATE_LIMITED"
  | "UPSTREAM_ERROR";
export type AiRating = "USABLE" | "NEEDS_IMPROVEMENT" | "UNUSABLE";
export type AiRatingTag =
  | "IGNORED_INPUT"
  | "WEAK_CYCLE_LINK"
  | "BAD_TONE"
  | "VAGUE"
  | "INAPPLICABLE_ADVICE"
  | "OVER_MEDICALIZED"
  | "FORMAT_ERROR"
  | "OTHER";

export const STATUS_LABEL: Record<AiGenerationStatus, string> = {
  PROCESSING: "生成中",
  SUCCESS: "成功",
  REFUSED: "拒答",
  EMPTY: "空输出",
  FORMAT_ERROR: "格式失败",
  TIMEOUT: "超时",
  RATE_LIMITED: "限流",
  UPSTREAM_ERROR: "上游异常",
};

export const RATING_LABEL: Record<AiRating, string> = {
  USABLE: "可用",
  NEEDS_IMPROVEMENT: "需优化",
  UNUSABLE: "不可用",
};

export const RATING_TAG_LABEL: Record<AiRatingTag, string> = {
  IGNORED_INPUT: "忽略输入",
  WEAK_CYCLE_LINK: "周期关联牵强",
  BAD_TONE: "语气不合适",
  VAGUE: "内容空泛",
  INAPPLICABLE_ADVICE: "建议不适用",
  OVER_MEDICALIZED: "过度医疗化",
  FORMAT_ERROR: "格式错误",
  OTHER: "其他",
};

export const TRIGGER_REASON_LABEL: Record<string, string> = {
  INITIAL: "首次",
  RETRY: "重试",
  FALLBACK: "切备用",
};

export interface AiGenerationItem {
  id: string;
  createdAt: string;
  anonymousUserId: string;
  sceneId: string;
  sceneName: string | null;
  templateId: string;
  templateSeq: number;
  finalModel: string | null;
  status: AiGenerationStatus;
  latencyMs: number | null;
  rating: AiRating | null;
}

export interface AiGenerationAttempt {
  seq: number;
  triggerReason: string;
  model: string;
  status: string;
  errorCode: string | null;
  errorMessage: string | null;
  providerRequestId: string | null;
  rawResponse: string | null;
  parsedContent: unknown;
  validationError: string | null;
  promptTokens: number | null;
  completionTokens: number | null;
  usage: unknown;
  startedAt: string | null;
  finishedAt: string | null;
  latencyMs: number | null;
}

export interface AiGenerationDetail {
  id: string;
  clientRequestId: string;
  anonymousUserId: string;
  sceneId: string;
  sceneName: string | null;
  templateId: string;
  templateSeq: number;
  templateName: string | null;
  appVersion: string | null;
  inputs: unknown;
  requestMessages: unknown;
  requestParams: unknown;
  status: AiGenerationStatus;
  errorCode: string | null;
  errorMessage: string | null;
  finalModel: string | null;
  attemptCount: number;
  rawOutput: string | null;
  content: unknown;
  promptTokens: number | null;
  completionTokens: number | null;
  latencyMs: number | null;
  createdAt: string;
  completedAt: string | null;
  rating: AiRating | null;
  ratingTags: AiRatingTag[];
  ratingRemark: string | null;
  ratedBy: string | null;
  ratedAt: string | null;
  attempts: AiGenerationAttempt[];
}

export interface AiGenerationQuery {
  startDate?: string;
  endDate?: string;
  requestId?: string;
  anonymousUserId?: string;
  sceneId?: string;
  templateId?: string;
  model?: string;
  status?: AiGenerationStatus;
  rating?: AiRating | "NONE";
  tag?: AiRatingTag;
  page?: number;
  size?: number;
}

export interface AiGenerationRatingRequest {
  rating: AiRating;
  tags?: AiRatingTag[];
  remark?: string | null;
}

export async function listAiGenerations(query: AiGenerationQuery = {}): Promise<Page<AiGenerationItem>> {
  const { data } = await apiClient.get<Page<AiGenerationItem>>("/api/admin/ai/generations/page", { params: query });
  return data;
}

export async function getAiGeneration(id: string): Promise<AiGenerationDetail> {
  const { data } = await apiClient.get<AiGenerationDetail>(`/api/admin/ai/generations/${id}`);
  return data;
}

export async function rateAiGeneration(id: string, req: AiGenerationRatingRequest): Promise<void> {
  await apiClient.put(`/api/admin/ai/generations/${id}/rating`, req);
}
