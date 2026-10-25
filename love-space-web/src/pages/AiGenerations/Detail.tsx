import { ReactNode, useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import { useToast } from "../../context/ToastContext";
import { AiGenerationDetail, getAiGeneration } from "../../api/aiGenerations";
import { formatDateTime } from "../AiPrompts/promptVariables";
import AttemptTable from "./AttemptTable";
import RatingPanel from "./RatingPanel";
import { renderStatus, formatLatency, formatTokens, shortId } from "./format";

const sectionClass = "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900";
const sectionTitleClass = "text-sm font-semibold text-gray-800 dark:text-white/90 mb-3";

function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className={sectionClass}>
      <h2 className={sectionTitleClass}>{title}</h2>
      {children}
    </section>
  );
}

/** 格式化展示 JSON；null 显示「—」。 */
function JsonBlock({ value }: { value: unknown }) {
  if (value == null) return <div className="text-sm text-gray-400">—</div>;
  return (
    <pre className="text-xs bg-gray-50 dark:bg-gray-800 rounded p-3 overflow-auto max-h-96 whitespace-pre-wrap break-all">
      {typeof value === "string" ? value : JSON.stringify(value, null, 2)}
    </pre>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="text-sm">
      <span className="text-gray-500 mr-2">{label}</span>
      <span className="text-gray-800 dark:text-white/90 break-all">{children}</span>
    </div>
  );
}

/** AI 管理 › 生成记录详情。 */
export default function AiGenerationDetailPage() {
  const { id = "" } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [detail, setDetail] = useState<AiGenerationDetail | null>(null);

  const load = useCallback(async () => {
    try {
      setDetail(await getAiGeneration(id));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
      navigate("/ai/generations");
    }
  }, [id, navigate, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <div>
      <PageMeta title="生成记录详情 | Love Space Admin" description="AI 生成记录详情" />
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90" title={id}>
          生成记录 › {shortId(id)}
        </h1>
        <button
          type="button"
          onClick={() => navigate("/ai/generations")}
          className="px-4 py-2 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
        >
          返回
        </button>
      </div>
      {!detail ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <div className="space-y-5">
          <Section title="业务请求">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-2">
              <Field label="requestId">{detail.id}</Field>
              <Field label="clientRequestId">{detail.clientRequestId}</Field>
              <Field label="匿名用户">{detail.anonymousUserId}</Field>
              <Field label="场景">
                {detail.sceneName ?? "—"}（{detail.sceneId}）
              </Field>
              <Field label="模板">
                <Link to={`/ai/prompt-templates/${detail.templateId}`} className="text-brand-500 hover:underline">
                  V{detail.templateSeq} {detail.templateName ?? ""} ↗
                </Link>
              </Field>
              <Field label="App 版本">{detail.appVersion ?? "—"}</Field>
              <Field label="状态">
                {renderStatus(detail.status)}
                {detail.errorCode && <span className="ml-2 text-gray-500">{detail.errorCode}：{detail.errorMessage}</span>}
              </Field>
              <Field label="实际模型">{detail.finalModel ?? "—"}</Field>
              <Field label="尝试次数">{detail.attemptCount}</Field>
              <Field label="创建">{formatDateTime(detail.createdAt)}</Field>
              <Field label="完成">{formatDateTime(detail.completedAt)}</Field>
              <Field label="耗时">{formatLatency(detail.latencyMs)}</Field>
              <Field label="tokens">{formatTokens(detail.promptTokens, detail.completionTokens)}</Field>
            </div>
          </Section>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
            <Section title="输入快照">
              <JsonBlock value={detail.inputs} />
            </Section>
            <Section title="实际请求">
              <div className="text-xs text-gray-500 mb-1">messages</div>
              <JsonBlock value={detail.requestMessages} />
              <div className="text-xs text-gray-500 mt-3 mb-1">params</div>
              <JsonBlock value={detail.requestParams} />
            </Section>
          </div>

          <Section title="调用明细">
            <AttemptTable attempts={detail.attempts} />
          </Section>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
            <Section title="生成结果">
              <div className="text-xs text-gray-500 mb-1">最终交付 content</div>
              <JsonBlock value={detail.content} />
              <div className="text-xs text-gray-500 mt-3 mb-1">最终模型原始输出</div>
              <JsonBlock value={detail.rawOutput} />
            </Section>
            <Section title="人工评价">
              <RatingPanel key={detail.ratedAt ?? "unrated"} detail={detail} onSaved={load} />
            </Section>
          </div>
        </div>
      )}
    </div>
  );
}
