import { FormEvent, useState } from "react";
import { AxiosError } from "axios";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { AiScene, createAiScene, updateAiScene } from "../../api/ai";

const SCENE_ID_PATTERN = /^[a-z][a-z0-9_]{1,63}$/;
const textareaClass =
  "border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-2 text-sm w-full min-h-[80px] bg-transparent dark:text-white/90";

interface Props {
  /** 编辑的场景（直接用列表行数据，不再拉详情，避免加载态切换导致弹窗闪烁）；null = 新增。 */
  scene: AiScene | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 业务配置：新增 / 编辑场景弹窗（3 个字段，按手册可用弹窗）。 */
export default function SceneFormModal({ scene, onClose, onSaved }: Props) {
  const toast = useToast();
  const [sceneId, setSceneId] = useState(scene?.sceneId ?? "");
  const [name, setName] = useState(scene?.name ?? "");
  const [description, setDescription] = useState(scene?.description ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const editingId = scene?.id ?? null;
  const editing = editingId !== null;

  const validate = (): boolean => {
    const next: Record<string, string> = {};
    if (!editing && !SCENE_ID_PATTERN.test(sceneId)) {
      next.sceneId = "以小写字母开头，仅含小写字母、数字、下划线，长度 2~64";
    }
    if (!name.trim()) next.name = "场景名称不能为空";
    else if (name.trim().length > 64)
      next.name = "场景名称长度不能超过 64 个字符";
    if (description.length > 500)
      next.description = "说明长度不能超过 500 个字符";
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting || !validate()) return;
    setSubmitting(true);
    try {
      const desc = description.trim() || null;
      if (editingId)
        await updateAiScene(editingId, {
          name: name.trim(),
          description: desc,
        });
      else
        await createAiScene({ sceneId, name: name.trim(), description: desc });
      toast.success("已保存");
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
      isOpen
      onClose={() => !submitting && onClose()}
      showBackdrop={false}
      className="max-w-[520px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
    >
      <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
          {editing ? "编辑场景" : "新增场景"}
        </h4>
        <form onSubmit={handleSubmit} noValidate className="space-y-5">
          <div>
            <Label>
              scene_id <span className="text-error-500">*</span>
            </Label>
            <Input
              placeholder="如 cycle_advice"
              value={sceneId}
              onChange={(e) => setSceneId(e.target.value)}
              disabled={editing}
              error={Boolean(errors.sceneId)}
              hint={errors.sceneId}
            />
            {editing && (
              <div className="text-xs text-gray-400 mt-1">创建后不可修改</div>
            )}
          </div>
          <div>
            <Label>
              名称 <span className="text-error-500">*</span>
            </Label>
            <Input
              placeholder="如 周期建议"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={Boolean(errors.name)}
              hint={errors.name}
            />
          </div>
          <div>
            <Label>说明</Label>
            <textarea
              className={textareaClass}
              value={description}
              maxLength={500}
              onChange={(e) => setDescription(e.target.value)}
            />
            {errors.description && (
              <div className="text-error-500 text-xs mt-1">
                {errors.description}
              </div>
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
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : "保存"}
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}
