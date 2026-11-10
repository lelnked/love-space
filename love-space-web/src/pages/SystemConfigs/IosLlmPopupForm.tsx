import { FormEvent, useState } from "react";
import { useNavigate } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Switch from "../../components/form/switch/Switch";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import {
  IosLlmPopupConfigValue,
  SystemConfigItem,
  updateSystemConfig,
} from "../../api/systemConfigs";

const sectionClass =
  "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900 space-y-4";
const sectionTitleClass =
  "text-sm font-semibold text-gray-800 dark:text-white/90 px-1";

function readInitialEnabled(value: unknown): boolean {
  if (typeof value === "object" && value !== null && "enabled" in value) {
    const v = (value as IosLlmPopupConfigValue).enabled;
    if (typeof v === "boolean") return v;
  }
  return true;
}

interface Props {
  config: SystemConfigItem;
}

/**
 * key = ios_llm_popup 专属编辑表单页：控制 iOS 系统调用 LLM 时是否需要弹窗。
 */
export default function IosLlmPopupForm({ config }: Props) {
  const navigate = useNavigate();
  const toast = useToast();

  const [name, setName] = useState(config.name);
  const [description, setDescription] = useState(config.description ?? "");
  const [enabled, setEnabled] = useState<boolean>(() => readInitialEnabled(config.value));
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;

    const errs: Record<string, string> = {};
    if (!name.trim()) {
      errs.name = "配置名称不能为空";
    } else if (name.trim().length > 64) {
      errs.name = "配置名称长度不能超过 64 个字符";
    }
    if (description.length > 500) {
      errs.description = "说明长度不能超过 500 个字符";
    }
    setFieldErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try {
      await updateSystemConfig(config.id, {
        name: name.trim(),
        description: description.trim() || null,
        value: { enabled },
      });
      toast.success("已保存，立即生效");
      navigate("/system-configs");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageMeta title="编辑 iOS LLM 弹窗开关 | Love Space Admin" description="编辑 iOS LLM 弹窗开关系统配置" />
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        编辑系统配置：{config.name}
      </h1>
      <form onSubmit={handleSubmit} noValidate className="max-w-2xl space-y-5">
        <fieldset className={sectionClass}>
          <legend className={sectionTitleClass}>基本信息</legend>
          <div>
            <Label>key</Label>
            <Input value={config.key} disabled />
            <div className="text-xs text-gray-400 mt-1">系统预置配置，key 不可修改</div>
          </div>
          <div>
            <Label>
              名称 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={name}
              error={Boolean(fieldErrors.name)}
              onChange={(e) => setName(e.target.value)}
            />
            {fieldErrors.name && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.name}</div>
            )}
          </div>
          <div>
            <Label>说明</Label>
            <Input
              value={description}
              error={Boolean(fieldErrors.description)}
              onChange={(e) => setDescription(e.target.value)}
            />
            {fieldErrors.description && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.description}</div>
            )}
          </div>
        </fieldset>

        <fieldset className={sectionClass}>
          <legend className={sectionTitleClass}>配置项参数（iOS LLM 弹窗控制）</legend>
          <div>
            <Label>
              是否需要弹窗 <span className="text-error-500">*</span>
            </Label>
            <div className="mt-2">
              <Switch
                checked={enabled}
                onChange={setEnabled}
                label={enabled ? "开启（需要弹窗）" : "关闭（不弹窗）"}
              />
            </div>
            <div className="text-xs text-gray-400 mt-1">
              控制 iOS 系统调用 LLM 时是否需要向用户展示确认弹窗
            </div>
          </div>
        </fieldset>

        <div className="flex gap-3">
          <Button size="sm" disabled={submitting}>
            {submitting ? "提交中..." : "保存"}
          </Button>
          <button
            type="button"
            onClick={() => navigate("/system-configs")}
            disabled={submitting}
            className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
          >
            取消
          </button>
        </div>
      </form>
    </div>
  );
}
