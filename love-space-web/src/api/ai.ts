import { apiClient } from "./client";
import type { Page } from "./types";

/** 场景当前生效模板摘要。 */
export interface AiActiveTemplate {
  id: string;
  seq: number;
  name: string;
  publishedAt: string | null;
  publishedBy: string | null;
}

/** 场景（列表项与详情共用）。 */
export interface AiScene {
  id: string;
  sceneId: string;
  name: string;
  description: string | null;
  enabled: boolean;
  primaryModel: string | null;
  backupModel: string | null;
  thinkingEnabled: boolean;
  activeTemplate: AiActiveTemplate | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface AiSceneOption {
  id: string;
  sceneId: string;
  name: string;
  enabled: boolean;
}

export interface AiSceneQuery {
  keyword?: string;
  enabled?: boolean;
  page?: number;
  size?: number;
}

export interface AiSceneCreateRequest {
  sceneId: string;
  name: string;
  description?: string | null;
}

export interface AiSceneUpdateRequest {
  name: string;
  description?: string | null;
}

export interface AiSceneModelsRequest {
  primaryModel: string;
  backupModel?: string | null;
  thinkingEnabled?: boolean;
}

export interface AiConnectionTestResult {
  ok: boolean;
  latencyMs: number;
  message: string;
}

export interface AiPromptVariable {
  name: string;
  required: boolean;
  description?: string | null;
}

export interface AiPromptTemplateItem {
  id: string;
  seq: number;
  name: string;
  remark: string | null;
  active: boolean;
  tested: boolean;
  lastTestAt: string | null;
  publishedAt: string | null;
  publishedBy: string | null;
  updatedBy: string | null;
  updatedAt: string | null;
}

export interface AiPromptTemplateDetail {
  id: string;
  sceneId: string;
  seq: number;
  name: string;
  systemPrompt: string;
  userPromptTemplate: string;
  variables: AiPromptVariable[];
  remark: string | null;
  active: boolean;
  tested: boolean;
  lastTestInputs: Record<string, unknown> | null;
  lastTestOutput: string | null;
  lastTestContent: unknown;
  lastTestModel: string | null;
  lastTestSuccess: boolean | null;
  lastTestError: string | null;
  lastTestLatencyMs: number | null;
  lastTestAt: string | null;
  lastTestBy: string | null;
  publishedAt: string | null;
  publishedBy: string | null;
  createdBy: string | null;
  updatedBy: string | null;
  createdAt: string;
  updatedAt: string | null;
  version: number;
}

export interface AiPromptTemplateCreateRequest {
  sceneId: string;
  name: string;
  systemPrompt: string;
  userPromptTemplate: string;
  variables: AiPromptVariable[];
  remark?: string | null;
}

export interface AiPromptTemplateUpdateRequest {
  name: string;
  systemPrompt: string;
  userPromptTemplate: string;
  variables: AiPromptVariable[];
  remark?: string | null;
  version: number;
}

export interface AiPromptTemplateTestResult {
  success: boolean;
  model: string | null;
  output: string | null;
  content: unknown;
  errorCode: string | null;
  errorMessage: string | null;
  latencyMs: number;
  promptTokens: number | null;
  completionTokens: number | null;
  testedAt: string;
}

export async function listAiScenes(query: AiSceneQuery = {}): Promise<Page<AiScene>> {
  const params: Record<string, string | number | boolean> = {};
  if (query.keyword) params.keyword = query.keyword;
  if (query.enabled !== undefined) params.enabled = query.enabled;
  if (query.page) params.page = query.page;
  if (query.size) params.size = query.size;
  const { data } = await apiClient.get<Page<AiScene>>("/api/admin/ai/scenes/page", { params });
  return data;
}

export async function listAiSceneOptions(): Promise<AiSceneOption[]> {
  const { data } = await apiClient.get<AiSceneOption[]>("/api/admin/ai/scenes/options");
  return data;
}

export async function getAiScene(id: string): Promise<AiScene> {
  const { data } = await apiClient.get<AiScene>(`/api/admin/ai/scenes/${id}`);
  return data;
}

export async function createAiScene(req: AiSceneCreateRequest): Promise<AiScene> {
  const { data } = await apiClient.post<AiScene>("/api/admin/ai/scenes", req);
  return data;
}

export async function updateAiScene(id: string, req: AiSceneUpdateRequest): Promise<AiScene> {
  const { data } = await apiClient.put<AiScene>(`/api/admin/ai/scenes/${id}`, req);
  return data;
}

export async function setAiSceneEnabled(id: string, enabled: boolean): Promise<void> {
  await apiClient.put(`/api/admin/ai/scenes/${id}/enabled`, { enabled });
}

export async function listAiModels(): Promise<string[]> {
  const { data } = await apiClient.get<string[]>("/api/admin/ai/models");
  return data;
}

export async function setAiSceneModels(id: string, req: AiSceneModelsRequest): Promise<void> {
  await apiClient.put(`/api/admin/ai/scenes/${id}/models`, req);
}

const AI_TEST_TIMEOUT_MS = 180_000;

export async function testAiModelConnection(model: string): Promise<AiConnectionTestResult> {
  const { data } = await apiClient.post<AiConnectionTestResult>(
    "/api/admin/ai/models/test-connection",
    { model },
    { timeout: AI_TEST_TIMEOUT_MS },
  );
  return data;
}

export async function listAiPromptTemplates(
  sceneId: string,
  page = 1,
  size = 20,
): Promise<Page<AiPromptTemplateItem>> {
  const { data } = await apiClient.get<Page<AiPromptTemplateItem>>("/api/admin/ai/prompt-templates/page", {
    params: { sceneId, page, size },
  });
  return data;
}

export async function getAiPromptTemplate(id: string): Promise<AiPromptTemplateDetail> {
  const { data } = await apiClient.get<AiPromptTemplateDetail>(`/api/admin/ai/prompt-templates/${id}`);
  return data;
}

export async function createAiPromptTemplate(req: AiPromptTemplateCreateRequest): Promise<AiPromptTemplateDetail> {
  const { data } = await apiClient.post<AiPromptTemplateDetail>("/api/admin/ai/prompt-templates", req);
  return data;
}

export async function updateAiPromptTemplate(
  id: string,
  req: AiPromptTemplateUpdateRequest,
): Promise<AiPromptTemplateDetail> {
  const { data } = await apiClient.put<AiPromptTemplateDetail>(`/api/admin/ai/prompt-templates/${id}`, req);
  return data;
}

export async function testAiPromptTemplate(
  id: string,
  inputs: Record<string, unknown>,
): Promise<AiPromptTemplateTestResult> {
  const { data } = await apiClient.post<AiPromptTemplateTestResult>(
    `/api/admin/ai/prompt-templates/${id}/test`,
    { inputs },
    { timeout: AI_TEST_TIMEOUT_MS },
  );
  return data;
}

export async function publishAiPromptTemplate(id: string, version: number): Promise<void> {
  await apiClient.post(`/api/admin/ai/prompt-templates/${id}/publish`, { version });
}

/** 按业务编码取场景（后端无按 sceneId 取详情接口，用关键字查询后精确匹配）；不存在返回 null。 */
export async function findAiSceneBySceneId(sceneId: string): Promise<AiScene | null> {
  const page = await listAiScenes({ keyword: sceneId, size: 200 });
  return page.content.find((s) => s.sceneId === sceneId) ?? null;
}

/** 生成接口每日额度的一项限制；value 为 -1 表示不限制。 */
export interface AiLimit {
  key: string;
  name: string;
  value: number;
  updatedBy: string | null;
  updatedAt: string | null;
}

export async function listAiLimits(): Promise<AiLimit[]> {
  const { data } = await apiClient.get<AiLimit[]>("/api/admin/ai/limits");
  return data;
}

export async function updateAiLimit(key: string, value: number): Promise<AiLimit> {
  const { data } = await apiClient.put<AiLimit>(`/api/admin/ai/limits/${key}`, { value });
  return data;
}
