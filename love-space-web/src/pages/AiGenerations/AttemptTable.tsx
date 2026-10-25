import { Fragment, useState } from "react";
import { AiGenerationAttempt, TRIGGER_REASON_LABEL } from "../../api/aiGenerations";
import { formatDateTime } from "../AiPrompts/promptVariables";
import { renderStatus, formatLatency, formatTokens, shortId } from "./format";

const th = "px-3 py-2 text-left font-medium text-gray-500 text-theme-xs";
const td = "px-3 py-2 text-gray-700 dark:text-gray-300";

/** 调用明细：每次模型调用一行，可展开原始返回与解析错误。 */
export default function AttemptTable({ attempts }: { attempts: AiGenerationAttempt[] }) {
  const [expanded, setExpanded] = useState<number | null>(null);

  if (attempts.length === 0) return <div className="text-sm text-gray-400">暂无调用明细</div>;

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead className="border-b border-gray-100 dark:border-gray-800">
          <tr>
            <th className={th}>#</th>
            <th className={th}>触发</th>
            <th className={th}>模型</th>
            <th className={th}>状态</th>
            <th className={th}>错误码</th>
            <th className={th}>供应商请求 ID</th>
            <th className={th}>开始</th>
            <th className={th}>耗时</th>
            <th className={th}>tokens</th>
            <th className={th}></th>
          </tr>
        </thead>
        <tbody>
          {attempts.map((a) => (
            <Fragment key={a.seq}>
              <tr className="border-b border-gray-100 dark:border-gray-800">
                <td className={td}>{a.seq}</td>
                <td className={td}>{TRIGGER_REASON_LABEL[a.triggerReason] ?? a.triggerReason}</td>
                <td className={td}>{a.model}</td>
                <td className={td}>
                  {renderStatus(a.status)}
                </td>
                <td className={td}>{a.errorCode ?? "—"}</td>
                <td className={td} title={a.providerRequestId ?? undefined}>
                  {shortId(a.providerRequestId)}
                </td>
                <td className={td}>{formatDateTime(a.startedAt)}</td>
                <td className={td}>{formatLatency(a.latencyMs)}</td>
                <td className={td}>{formatTokens(a.promptTokens, a.completionTokens)}</td>
                <td className={td}>
                  <button
                    type="button"
                    className="text-brand-500 hover:underline whitespace-nowrap"
                    onClick={() => setExpanded(expanded === a.seq ? null : a.seq)}
                  >
                    {expanded === a.seq ? "▾ 收起" : "▸ 原始返回"}
                  </button>
                </td>
              </tr>
              {expanded === a.seq && (
                <tr className="border-b border-gray-100 dark:border-gray-800">
                  <td colSpan={10} className="px-3 py-3 space-y-2">
                    {a.errorMessage && <div className="text-xs text-error-600">错误信息：{a.errorMessage}</div>}
                    <div className="text-xs text-gray-500">解析错误：{a.validationError ?? "—"}</div>
                    <div className="text-xs text-gray-500">原始返回：</div>
                    <pre className="text-xs bg-gray-50 dark:bg-gray-800 rounded p-3 overflow-auto max-h-96 whitespace-pre-wrap break-all">
                      {a.rawResponse ?? "—"}
                    </pre>
                  </td>
                </tr>
              )}
            </Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
}
