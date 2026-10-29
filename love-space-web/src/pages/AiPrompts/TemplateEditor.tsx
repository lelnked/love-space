import { FormEvent, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import Label from "../../components/form/Label";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import {
  AiPromptTemplateDetail,
  AiPromptTemplateTestResult,
  AiPromptVariable,
  AiScene,
  createAiPromptTemplate,
  findAiSceneBySceneId,
  getAiPromptTemplate,
  publishAiPromptTemplate,
  testAiPromptTemplate,
  updateAiPromptTemplate,
} from "../../api/ai";
import { findUndeclaredVariables, variableErrors } from "./promptVariables";
import VariableTable from "./VariableTable";
import TestPanel from "./TestPanel";

const sectionClass = "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900";
const inputClass =
  "border border-gray-300 dark:border-gray-700 rounded px-3 py-2 text-sm w-full h-11 bg-transparent dark:text-white/90 disabled:bg-gray-100 dark:disabled:bg-gray-800";
const textareaClass =
  "border border-gray-300 dark:border-gray-700 rounded px-3 py-2 text-sm w-full min-h-[140px] font-mono bg-transparent dark:text-white/90 disabled:bg-gray-100 dark:disabled:bg-gray-800";

interface FormState {
  name: string;
  remark: string;
  systemPrompt: string;
  userPromptTemplate: string;
  variables: AiPromptVariable[];
}

const EMPTY_FORM: FormState = { name: "", remark: "", systemPrompt: "", userPromptTemplate: "", variables: [] };

function formFrom(d: AiPromptTemplateDetail): FormState {
  return {
    name: d.name,
    remark: d.remark ?? "",
    systemPrompt: d.systemPrompt,
    userPromptTemplate: d.userPromptTemplate,
    variables: d.variables.map((v) => ({ ...v, description: v.description ?? "" })),
  };
}

function inputsFrom(d: AiPromptTemplateDetail | null): Record<string, string> {
  const result: Record<string, string> = {};
  Object.entries(d?.lastTestInputs ?? {}).forEach(([k, v]) => {
    result[k] = typeof v === "string" ? v : JSON.stringify(v);
  });
  return result;
}

/** 模板编辑与测试页（页面 3）：/ai/prompts/:sceneId/templates/new 与 /ai/prompt-templates/:id。 */
export default function AiPromptTemplateEditor() {
  const { id, sceneId: routeSceneId } = useParams();
  const [searchParams] = useSearchParams();
  const copyFrom = searchParams.get("copyFrom");
  const navigate = useNavigate();
  const toast = useToast();
  const confirm = useConfirm();

  const [detail, setDetail] = useState<AiPromptTemplateDetail | null>(null);
  const [scene, setScene] = useState<AiScene | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [dirty, setDirty] = useState(false);
  const [inputs, setInputs] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);
  const [lastResult, setLastResult] = useState<AiPromptTemplateTestResult | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const sceneId = detail?.sceneId ?? routeSceneId ?? "";
  const readOnly = detail?.active ?? false;

  // 加载：编辑路由拉详情；新建路由可按 copyFrom 预填。新建保存后跳到编辑路由时已持有详情，不重复拉。
  useEffect(() => {
    const sourceId = id ?? copyFrom;
    if (id && detail?.id === id) return;
    if (!sourceId) {
      setLoading(false);
      return;
    }
    setLoading(true);
    getAiPromptTemplate(sourceId)
      .then((d) => {
        setForm(formFrom(d));
        setInputs(inputsFrom(d));
        if (id) setDetail(d);
      })
      .catch((err) => {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(ax.response?.data?.detail ?? "加载失败");
        navigate(routeSceneId ? `/ai/prompts/${routeSceneId}` : "/ai/prompts");
      })
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, copyFrom]);

  useEffect(() => {
    if (!sceneId) return;
    findAiSceneBySceneId(sceneId)
      .then(setScene)
      .catch(() => setScene(null));
  }, [sceneId]);

  const undeclared = useMemo(
    () => findUndeclaredVariables(form.systemPrompt, form.userPromptTemplate, form.variables),
    [form],
  );
  const varErrors = useMemo(() => variableErrors(form.variables), [form.variables]);
  const missingRequired = form.variables.filter((v) => v.required && v.name.trim() && !inputs[v.name.trim()]?.trim());

  const disabledReason = !scene?.primaryModel
    ? "场景未配置主模型"
    : Object.keys(varErrors).length
      ? "变量声明有误"
      : undeclared.length
        ? "存在未声明变量"
        : missingRequired.length
          ? `请填写必填变量：${missingRequired.map((v) => v.name).join("、")}`
          : null;

  const patch = (p: Partial<FormState>) => {
    setForm((f) => ({ ...f, ...p }));
    setDirty(true);
  };

  const validate = (): boolean => {
    const next: Record<string, string> = {};
    if (!form.name.trim()) next.name = "模板名称不能为空";
    else if (form.name.trim().length > 64) next.name = "模板名称长度不能超过 64 个字符";
    if (!form.systemPrompt.trim()) next.systemPrompt = "系统提示词不能为空";
    if (!form.userPromptTemplate.trim()) next.userPromptTemplate = "输入模板不能为空";
    setErrors(next);
    return Object.keys(next).length === 0 && Object.keys(varErrors).length === 0;
  };

  /** 保存：新建 POST 后跳到编辑路由；编辑 PUT 带 version。失败返回 null，保留本地内容。 */
  const save = async (): Promise<AiPromptTemplateDetail | null> => {
    if (!validate()) {
      toast.error("请先修正表单错误");
      return null;
    }
    const body = {
      name: form.name.trim(),
      systemPrompt: form.systemPrompt,
      userPromptTemplate: form.userPromptTemplate,
      variables: form.variables.map((v) => ({ ...v, name: v.name.trim(), description: v.description || null })),
      remark: form.remark.trim() || null,
    };
    setSaving(true);
    try {
      const saved = detail
        ? await updateAiPromptTemplate(detail.id, { ...body, version: detail.version })
        : await createAiPromptTemplate({ ...body, sceneId });
      setDetail(saved);
      setDirty(false);
      setLastResult(null);
      if (!detail) navigate(`/ai/prompt-templates/${saved.id}`, { replace: true });
      return saved;
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "保存失败");
      return null;
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (saving || readOnly) return;
    if (await save()) toast.success("已保存");
  };

  const runTest = async () => {
    if (testing || disabledReason) return;
    let target = detail;
    if (!target || dirty) {
      target = await save();
      if (!target) return;
    }
    setTesting(true);
    try {
      const values: Record<string, unknown> = {};
      form.variables.forEach((v) => {
        const name = v.name.trim();
        if (name && inputs[name] !== undefined && inputs[name] !== "") values[name] = inputs[name];
      });
      const result = await testAiPromptTemplate(target.id, values);
      setLastResult(result);
      setDetail(await getAiPromptTemplate(target.id));
      if (result.success) toast.success("测试成功");
      else toast.error(`测试失败：${result.errorMessage ?? result.errorCode ?? ""}`);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      if (ax.code === "ECONNABORTED") {
        toast.error("请求超时，模型可能仍在生成中，请稍后刷新查看结果");
      } else {
        toast.error(ax.response?.data?.detail ?? "测试失败");
      }
      getAiPromptTemplate(target.id).then(setDetail).catch(() => {});
    } finally {
      setTesting(false);
    }
  };

  const publish = async () => {
    if (!detail) return;
    const ok = await confirm({
      title: "发布模板",
      message: `发布后 V${detail.seq} 立即对新请求生效，原生效版本失效。`,
    });
    if (!ok) return;
    try {
      await publishAiPromptTemplate(detail.id, detail.version);
      toast.success(`V${detail.seq} 已发布`);
      setDetail(await getAiPromptTemplate(detail.id));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "发布失败");
    }
  };

  const canPublish = Boolean(detail?.tested) && !dirty && !readOnly;
  const title = detail ? `V${detail.seq} ${detail.name}` : "新建模板";
  const testState = !detail?.lastTestAt && !detail?.tested
    ? "✗ 未测试"
    : detail?.tested && !dirty
      ? "✓ 已测试"
      : "✗ 内容已修改，需重新测试";

  return (
    <div>
      <PageMeta title={`${title} | Love Space Admin`} description="提示词模板编辑与测试" />
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        提示词管理 › {scene?.name ?? sceneId} › {title}
      </h1>
      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} noValidate className="space-y-5">
          <div className="grid grid-cols-1 xl:grid-cols-2 gap-5">
            <fieldset className={sectionClass + " space-y-4"}>
              <legend className="text-sm font-semibold text-gray-800 dark:text-white/90 px-1">模板内容</legend>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <Label>
                    名称 <span className="text-error-500">*</span>
                  </Label>
                  <input
                    className={inputClass}
                    value={form.name}
                    maxLength={64}
                    disabled={readOnly}
                    onChange={(e) => patch({ name: e.target.value })}
                  />
                  {errors.name && <div className="text-error-500 text-xs mt-1">{errors.name}</div>}
                </div>
                <div>
                  <Label>修改说明</Label>
                  <input
                    className={inputClass}
                    value={form.remark}
                    disabled={readOnly}
                    onChange={(e) => patch({ remark: e.target.value })}
                  />
                </div>
              </div>
              <div>
                <Label>
                  系统提示词 <span className="text-error-500">*</span>
                </Label>
                <textarea
                  className={textareaClass}
                  value={form.systemPrompt}
                  disabled={readOnly}
                  onChange={(e) => patch({ systemPrompt: e.target.value })}
                />
                {errors.systemPrompt && <div className="text-error-500 text-xs mt-1">{errors.systemPrompt}</div>}
              </div>
              <div>
                <Label>
                  输入模板 <span className="text-error-500">*</span>
                </Label>
                <textarea
                  className={textareaClass}
                  value={form.userPromptTemplate}
                  disabled={readOnly}
                  onChange={(e) => patch({ userPromptTemplate: e.target.value })}
                />
                {errors.userPromptTemplate && (
                  <div className="text-error-500 text-xs mt-1">{errors.userPromptTemplate}</div>
                )}
                <div className="text-xs text-gray-400 mt-1">用 {"{{变量名}}"} 引用下方声明的变量</div>
              </div>
              {undeclared.length > 0 && (
                <div className="rounded bg-warning-50 dark:bg-warning-500/15 p-3 text-xs text-warning-700 dark:text-warning-400 space-y-1">
                  {undeclared.map((u) => (
                    <div key={u.location + u.name}>
                      ⚠ {u.location}引用了未声明变量 {`{{${u.name}}}`}
                    </div>
                  ))}
                </div>
              )}
              <VariableTable
                variables={form.variables}
                errors={varErrors}
                readOnly={readOnly}
                onChange={(variables) => patch({ variables })}
              />
            </fieldset>

            <fieldset className={sectionClass}>
              <legend className="text-sm font-semibold text-gray-800 dark:text-white/90 px-1">测试</legend>
              <TestPanel
                scene={scene}
                variables={form.variables}
                inputs={inputs}
                onInputsChange={setInputs}
                disabledReason={disabledReason}
                running={testing || saving}
                onRun={() => void runTest()}
                detail={detail}
                lastResult={lastResult}
              />
            </fieldset>
          </div>

          <div className="flex flex-wrap items-center justify-between gap-3">
            <div className="text-sm text-gray-600 dark:text-gray-400">
              状态：{detail?.active ? "●生效中" : "未生效"} · {testState}
            </div>
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => navigate(`/ai/prompts/${sceneId}`)}
                disabled={saving}
                className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
              >
                返回
              </button>
              {!readOnly && (
                <Button size="sm" disabled={saving}>
                  {saving ? "提交中..." : "保存"}
                </Button>
              )}
              {!readOnly && (
                <span title={canPublish ? "" : "请先运行测试"}>
                  <Button size="sm" variant="outline" type="button" disabled={!canPublish} onClick={() => void publish()}>
                    发布
                  </Button>
                </span>
              )}
            </div>
          </div>
        </form>
      )}
    </div>
  );
}
