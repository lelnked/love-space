import { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import { useToast } from "../../context/ToastContext";
import { AiPromptTemplateDetail, getAiPromptTemplate } from "../../api/ai";
import { lineDiff } from "./lineDiff";
import { prettyJson } from "./promptVariables";

type Field = { label: string; get: (t: AiPromptTemplateDetail) => string; empty?: string };

const FIELDS: Field[] = [
  { label: "系统提示词", get: (t) => t.systemPrompt },
  { label: "输入模板", get: (t) => t.userPromptTemplate },
  { label: "变量", get: (t) => t.variables.map((v) => v.name + (v.required ? "*" : "")).join(" ") },
  { label: "最后一次测试输入", get: (t) => prettyJson(t.lastTestInputs), empty: "—" },
  {
    label: "最后一次测试输出",
    get: (t) => (t.lastTestContent != null ? prettyJson(t.lastTestContent) : t.lastTestOutput ?? ""),
    empty: "尚未测试",
  },
];

/** 提示词管理 › 对比（页面 4）：2~3 个模板并排，按行高亮差异。 */
export default function AiPromptCompare() {
  const { sceneId = "" } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [templates, setTemplates] = useState<AiPromptTemplateDetail[] | null>(null);

  useEffect(() => {
    const ids = (searchParams.get("ids") ?? "").split(",").filter(Boolean);
    if (ids.length < 2 || ids.length > 3) {
      toast.error("请选择 2~3 个模板对比");
      navigate(`/ai/prompts/${sceneId}`, { replace: true });
      return;
    }
    Promise.all(ids.map(getAiPromptTemplate))
      .then(setTemplates)
      .catch((err) => {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(ax.response?.data?.detail ?? "加载失败");
        navigate(`/ai/prompts/${sceneId}`, { replace: true });
      });
  }, [searchParams, sceneId, navigate, toast]);

  return (
    <div>
      <PageMeta title="模板对比 | Love Space Admin" description="提示词模板对比" />
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        提示词管理 › {sceneId} › 对比
      </h1>
      {!templates ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <>
          <div className={`grid grid-cols-1 gap-4 ${templates.length === 3 ? "lg:grid-cols-3" : "lg:grid-cols-2"}`}>
            {templates.map((t, col) => (
              <div
                key={t.id}
                className="border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900 space-y-4"
              >
                <div className="font-semibold text-gray-800 dark:text-white/90">
                  V{t.seq} {t.name}（{t.active ? "●生效中" : "未生效"}）
                </div>
                {FIELDS.map((f) => {
                  const lines = lineDiff(templates.map(f.get))[col];
                  const isEmpty = !f.get(t);
                  return (
                    <div key={f.label}>
                      <div className="text-xs text-gray-500 mb-1">── {f.label} ──</div>
                      {isEmpty && f.empty ? (
                        <div className="text-xs text-gray-400">{f.empty}</div>
                      ) : (
                        <pre className="text-xs whitespace-pre-wrap break-all font-mono">
                          {lines.map((l, i) => (
                            <div key={i} className={l.changed ? "bg-warning-100 dark:bg-warning-500/20" : undefined}>
                              {l.text || " "}
                            </div>
                          ))}
                        </pre>
                      )}
                    </div>
                  );
                })}
              </div>
            ))}
          </div>
          <div className="flex justify-end mt-4">
            <button
              type="button"
              onClick={() => navigate(`/ai/prompts/${sceneId}`)}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              返回
            </button>
          </div>
        </>
      )}
    </div>
  );
}
