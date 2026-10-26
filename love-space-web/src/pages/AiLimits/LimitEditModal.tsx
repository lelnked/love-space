import { FormEvent, useEffect, useState } from "react";
import { AxiosError } from "axios";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { AiLimit, updateAiLimit } from "../../api/ai";

const inputClass =
  "border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-2 text-sm w-40 h-11 bg-transparent dark:bg-gray-900 dark:text-white/90";

/** -1 或正整数（不允许前导 0、小数、空串）。 */
const VALID_VALUE = /^(-1|[1-9]\d*)$/;

interface Props {
  limit: AiLimit | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 编辑一项限流配置（1 个字段）。 */
export default function LimitEditModal({ limit, onClose, onSaved }: Props) {
  const toast = useToast();
  const [value, setValue] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (limit) setValue(String(limit.value));
  }, [limit]);

  // 超出 int 范围交给后端 400 提示
  const valid = VALID_VALUE.test(value.trim());

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!limit || submitting || !valid) return;
    setSubmitting(true);
    try {
      await updateAiLimit(limit.key, Number(value.trim()));
      toast.success("已保存，立即生效");
      onSaved();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={limit !== null}
      onClose={() => !submitting && onClose()}
      showBackdrop={false}
      className="max-w-[480px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
    >
      <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">编辑：{limit?.name}</h4>
        <form onSubmit={handleSubmit} noValidate className="space-y-5">
          <div>
            <Label>
              上限 <span className="text-error-500">*</span>
            </Label>
            <div className="flex items-center gap-2">
              <input
                type="number"
                step={1}
                min={-1}
                className={inputClass}
                value={value}
                onChange={(e) => setValue(e.target.value)}
              />
              <span className="text-sm text-gray-500">次</span>
            </div>
            {valid ? (
              <div className="text-xs text-gray-400 mt-1">-1 表示不限制</div>
            ) : (
              <div className="text-error-500 text-xs mt-1">取值须为 -1 或正整数</div>
            )}
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              取消
            </button>
            <Button size="sm" disabled={submitting || !valid}>
              {submitting ? "提交中..." : "保存"}
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}
