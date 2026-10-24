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
