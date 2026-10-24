import type { AiPromptTemplateDetail, AiPromptTemplateTestResult, AiPromptVariable, AiScene } from "../../api/ai";
import { formatDateTime, prettyJson } from "./promptVariables";

const inputClass =
  "border border-gray-300 dark:border-gray-700 rounded px-3 py-2 text-sm w-full bg-transparent dark:text-white/90";

interface Props {
  scene: AiScene | null;
  variables: AiPromptVariable[];
  inputs: Record<string, string>;
  onInputsChange: (inputs: Record<string, string>) => void;
  /** 不能运行测试的原因；null = 可运行。 */
  disabledReason: string | null;
  running: boolean;
  onRun: () => void;
  detail: AiPromptTemplateDetail | null;
  /** 本次会话内最后一次测试响应（含 tokens）。 */
  lastResult: AiPromptTemplateTestResult | null;
}

/** 模板编辑页右栏：使用模型 → 填变量 → 运行测试 → 最后一次结果。 */
export default function TestPanel({
  scene,
  variables,
  inputs,
  onInputsChange,
  disabledReason,
  running,
  onRun,
  detail,
  lastResult,
}: Props) {
  const named = variables.filter((v) => v.name.trim());
  const tokens =
    lastResult && lastResult.promptTokens !== null
      ? ` · ${lastResult.promptTokens}/${lastResult.completionTokens ?? 0} tokens`
      : "";

  return (
    <div className="space-y-5">
      <div className="text-sm text-gray-700 dark:text-gray-300">
        <div className="font-semibold mb-1">使用模型</div>
        {scene?.primaryModel ? (
          <>
            <div>{scene.primaryModel}（主）</div>
            {scene.backupModel && <div>{scene.backupModel}（备用）</div>}
          </>
        ) : (
          <div className="text-warning-600">未配置主模型，无法测试</div>
        )}
      </div>

      <div>
        <div className="text-sm font-semibold text-gray-800 dark:text-white/90 mb-2">1. 填写变量取值</div>
        {named.length === 0 ? (
          <div className="text-xs text-gray-400">模板未声明变量</div>
        ) : (
          <div className="space-y-2">
            {named.map((v) => (
              <label key={v.name} className="grid grid-cols-[8rem_1fr] items-center gap-2 text-sm">
                <span className="truncate" title={v.description ?? ""}>
                  {v.name}
                  {v.required && <span className="text-error-500"> *</span>}
                </span>
                <input
                  className={inputClass}
                  value={inputs[v.name] ?? ""}
                  placeholder={v.description ?? ""}
                  onChange={(e) => onInputsChange({ ...inputs, [v.name]: e.target.value })}
                />
              </label>
            ))}
          </div>
        )}
      </div>

      <div>
        <div className="text-sm font-semibold text-gray-800 dark:text-white/90 mb-2">2. 运行测试</div>
        <button
          type="button"
          onClick={onRun}
          disabled={disabledReason !== null || running}
          className="w-full py-3 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {running ? "测试中..." : "▶ 运行测试"}
        </button>
        {disabledReason && <div className="text-xs text-warning-600 mt-1">{disabledReason}</div>}
      </div>

      <div>
        <div className="text-sm font-semibold text-gray-800 dark:text-white/90 mb-2">3. 测试结果（最后一次）</div>
        {!detail?.lastTestAt ? (
          <div className="text-xs text-gray-400">尚未测试</div>
        ) : (
          <div className="space-y-2 text-sm">
            <div className="text-gray-600 dark:text-gray-400">
              {formatDateTime(detail.lastTestAt)} · {detail.lastTestBy ?? "—"} ·{" "}
              {detail.lastTestSuccess ? (
                <span className="text-success-600">✓ 成功</span>
              ) : (
                <span className="text-error-500">✗ 失败</span>
              )}
            </div>
            <div className="text-gray-600 dark:text-gray-400">
              {((detail.lastTestLatencyMs ?? 0) / 1000).toFixed(1)}s · {detail.lastTestModel ?? "—"}
              {tokens}
            </div>
            {!detail.lastTestSuccess && detail.lastTestError && (
              <div className="text-error-500 text-xs break-all">{detail.lastTestError}</div>
            )}
            <pre className="max-h-80 overflow-auto rounded bg-gray-50 dark:bg-gray-800 p-3 text-xs whitespace-pre-wrap break-all">
              {detail.lastTestContent != null ? prettyJson(detail.lastTestContent) : detail.lastTestOutput ?? ""}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}
