import { FormEvent, useEffect, useState } from "react";
import { AxiosError } from "axios";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { SystemConfigItem, createSystemConfig, updateSystemConfig } from "../../api/systemConfigs";

const textareaClass =
  "border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-2 text-sm w-full min-h-[160px] font-mono bg-transparent dark:bg-gray-900 dark:text-white/90";

type ParseResult = { ok: true; value: unknown } | { ok: false; error: string };

/** value 校验：能被 JSON 解析且结果不为 null。 */
function parseJsonValue(text: string): ParseResult {
  if (!text.trim()) return { ok: false, error: "value 不能为空" };
  try {
    const value: unknown = JSON.parse(text);
    return value === null ? { ok: false, error: "value 不能为空" } : { ok: true, value };
  } catch {
    return { ok: false, error: "value 必须是合法的 JSON" };
  }
}

interface Props {
  open: boolean;
  /** 编辑目标；null 为新增。 */
  config: SystemConfigItem | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 新增 / 编辑系统配置（4 个字段，按规范用弹窗）。 */
export default function ConfigEditModal({ open, config, onClose, onSaved }: Props) {
  const toast = useToast();
  const editing = config !== null;
  const [key, setKey] = useState("");
  const [name, setName] = useState("");
  const [valueText, setValueText] = useState("");
  const [description, setDescription] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [serverError, setServerError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setKey(config?.key ?? "");
    setName(config?.name ?? "");
    setValueText(config ? JSON.stringify(config.value, null, 2) : "");
    setDescription(config?.description ?? "");
    setErrors({});
    setServerError("");
  }, [open, config]);

  const setValueError = (error?: string) =>
    setErrors((prev) => {
      const next = { ...prev };
      if (error) next.value = error;
      else delete next.value;
      return next;
    });

  const validateValue = () => {
    const r = parseJsonValue(valueText);
    setValueError(r.ok ? undefined : r.error);
    return r;
  };

  const handleFormat = () => {
    const r = validateValue();
    if (r.ok) setValueText(JSON.stringify(r.value, null, 2));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setServerError("");
    const next: Record<string, string> = {};
    if (!editing && !key.trim()) next.key = "key 不能为空";
    if (!name.trim()) next.name = "配置名称不能为空";
    const parsed = parseJsonValue(valueText);
    if (!parsed.ok) next.value = parsed.error;
    setErrors(next);
    if (Object.keys(next).length > 0 || !parsed.ok) return;

    setSubmitting(true);
    try {
      const body = { name: name.trim(), value: parsed.value, description: description.trim() || null };
      if (config) await updateSystemConfig(config.id, body);
      else await createSystemConfig({ key: key.trim(), ...body });
      toast.success("已保存，立即生效");
      onSaved();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      setServerError(ax.response?.data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={open}
      onClose={() => !submitting && onClose()}
      showBackdrop={false}
      className="max-w-[640px] m-4 shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
    >
      <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
          {editing ? "编辑配置" : "新增配置"}
        </h4>
        <form onSubmit={handleSubmit} noValidate className="space-y-5">
          <div>
            <Label>
              key <span className="text-error-500">*</span>
            </Label>
            <Input
              value={key}
              disabled={editing}
              placeholder="如 home.notice"
              error={Boolean(errors.key)}
              onChange={(e) => setKey(e.target.value)}
            />
            {errors.key ? (
              <div className="text-error-500 text-xs mt-1">{errors.key}</div>
            ) : (
              <div className="text-xs text-gray-400 mt-1">
                {editing ? "创建后不可修改" : "小写字母开头，可含数字、下划线、点，2~64 位；创建后不可修改"}
              </div>
            )}
          </div>
          <div>
            <Label>
              名称 <span className="text-error-500">*</span>
            </Label>
            <Input value={name} error={Boolean(errors.name)} onChange={(e) => setName(e.target.value)} />
            {errors.name && <div className="text-error-500 text-xs mt-1">{errors.name}</div>}
          </div>
          <div>
            <div className="flex items-center justify-between">
              <Label>
                value <span className="text-error-500">*</span>
              </Label>
              <button
                type="button"
                onClick={handleFormat}
                className="mb-1.5 text-xs text-brand-500 hover:text-brand-600"
              >
                格式化
              </button>
            </div>
            <textarea
              className={textareaClass}
              value={valueText}
              placeholder='如 {"text": "放假通知"}'
              onChange={(e) => setValueText(e.target.value)}
              onBlur={validateValue}
            />
            {errors.value && <div className="text-error-500 text-xs mt-1">{errors.value}</div>}
          </div>
          <div>
            <Label>说明</Label>
            <Input value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <div className="text-xs text-gray-400">App 可读取全部配置，请勿填写密钥、密码等机密</div>
          {serverError && <div className="text-error-500 text-sm">{serverError}</div>}
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              取消
            </button>
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : "保存"}
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}
