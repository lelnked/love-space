import { AiGenerationStatus, STATUS_LABEL } from "../../api/aiGenerations";

/** 缩略 ID：前 8 位 + …，配合 title 显示全文。 */
export function shortId(id: string | null | undefined): string {
  if (!id) return "—";
  return id.length > 8 ? `${id.slice(0, 8)}…` : id;
}

/** 毫秒 → 秒，1 位小数。 */
export function formatLatency(ms: number | null | undefined): string {
  return ms == null ? "—" : `${(ms / 1000).toFixed(1)}s`;
}

export function formatTokens(prompt: number | null, completion: number | null): string {
  return prompt == null && completion == null ? "—" : `${prompt ?? "—"}/${completion ?? "—"}`;
}

/** 状态中文：成功 ✓ 绿、生成中灰、其余 ✗ 红。 */
export function renderStatus(status: string) {
  const label = STATUS_LABEL[status as AiGenerationStatus] ?? status;
  if (status === "PROCESSING") return <span className="text-gray-400">{label}</span>;
  if (status === "SUCCESS") return <span className="text-success-600">✓ {label}</span>;
  return <span className="text-error-600">✗ {label}</span>;
}
