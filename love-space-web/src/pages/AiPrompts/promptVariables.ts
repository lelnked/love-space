import type { AiPromptVariable } from "../../api/ai";

/** 与后端 PromptRenderer 同一规则。 */
export const VARIABLE_NAME = /^[A-Za-z_][A-Za-z0-9_]*$/;
const REFERENCE = /\{\{\s*([A-Za-z_][A-Za-z0-9_]*)\s*\}\}/g;

export type PromptLocation = "系统提示词" | "输入模板";

export interface UndeclaredVariable {
  name: string;
  location: PromptLocation;
}

/** 找出提示词里引用了但变量表没声明的变量（每个位置内去重）。 */
export function findUndeclaredVariables(
  systemPrompt: string,
  userPromptTemplate: string,
  variables: AiPromptVariable[],
): UndeclaredVariable[] {
  const declared = new Set(variables.map((v) => v.name.trim()));
  const result: UndeclaredVariable[] = [];
  const scan = (text: string, location: PromptLocation) => {
    const seen = new Set<string>();
    for (const m of text.matchAll(REFERENCE)) {
      if (!declared.has(m[1]) && !seen.has(m[1])) {
        seen.add(m[1]);
        result.push({ name: m[1], location });
      }
    }
  };
  scan(systemPrompt, "系统提示词");
  scan(userPromptTemplate, "输入模板");
  return result;
}

/** 变量声明错误（名称非法 / 重复），按行号返回提示，空数组 = 无错误。 */
export function variableErrors(variables: AiPromptVariable[]): Record<number, string> {
  const errors: Record<number, string> = {};
  const seen = new Set<string>();
  variables.forEach((v, i) => {
    const name = v.name.trim();
    if (!VARIABLE_NAME.test(name)) errors[i] = "变量名须以字母或下划线开头，仅含字母、数字、下划线";
    else if (seen.has(name)) errors[i] = "变量名重复";
    seen.add(name);
  });
  return errors;
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return "—";
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

export function prettyJson(value: unknown): string {
  if (value === null || value === undefined) return "";
  if (typeof value === "string") return value;
  return JSON.stringify(value, null, 2);
}

/** 模板测试失败码对应的中文分类说明。 */
export const TEST_ERROR_LABEL: Record<string, string> = {
  FORMAT_ERROR: "输出格式非 JSON",
  EMPTY: "模型返回为空",
  REFUSED: "模型安全拒答",
  TIMEOUT: "模型响应超时",
  RATE_LIMITED: "上游接口限流",
  UPSTREAM_ERROR: "模型服务调用异常",
};

/** 解析后端存储的 lastTestError（格式："{errorCode}：{errorMessage}"）。 */
export function parseLastTestError(raw: string | null | undefined): {
  code: string | null;
  label: string | null;
  message: string;
} | null {
  if (!raw) return null;
  const idx = raw.indexOf("：");
  if (idx <= 0) {
    return { code: null, label: null, message: raw };
  }
  const code = raw.slice(0, idx).trim();
  let message = raw.slice(idx + 1).trim();
  const redundantSuffix = `（${code}）`;
  if (message.endsWith(redundantSuffix)) {
    message = message.slice(0, -redundantSuffix.length);
  }
  return {
    code,
    label: TEST_ERROR_LABEL[code] ?? null,
    message,
  };
}

/** 组装测试返回 200 但 success=false 时的弹窗提示。 */
export function formatTestFailureToast(
  errorCode: string | null | undefined,
  errorMessage: string | null | undefined,
): string {
  const label = errorCode ? TEST_ERROR_LABEL[errorCode] : null;
  let detail = errorMessage ?? errorCode ?? "";
  if (errorCode && detail.endsWith(`（${errorCode}）`)) {
    detail = detail.slice(0, -`（${errorCode}）`.length);
  }
  return `测试失败${label ? `（${label}）` : ""}${detail ? `：${detail}` : ""}`;
}

/** 组装测试 HTTP 请求异常（超前断开、网关 504、400/409 校验等）时的提示。 */
export function formatTestRequestError(ax: {
  code?: string;
  response?: { status?: number; data?: { detail?: string } };
}): string {
  const status = ax.response?.status;
  if (ax.code === "ECONNABORTED" || status === 504 || status === 408) {
    return "请求超时，模型可能仍在生成中，请稍后刷新页面查看结果";
  }
  if (status === 502 || status === 503) {
    return `网关连接异常（HTTP ${status}），后端可能仍在处理中，请稍后刷新查看结果`;
  }
  if (!ax.response) {
    return "网络连接中断（可能因等待模型响应耗时过长被断开），请稍后刷新页面查看结果";
  }
  return ax.response.data?.detail ?? `测试请求失败（HTTP ${status ?? "未知"}）`;
}
