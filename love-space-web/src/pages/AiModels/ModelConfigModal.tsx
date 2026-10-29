import { FormEvent, useEffect, useState } from "react";
import { AxiosError } from "axios";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { AiConnectionTestResult, AiScene, listAiModels, setAiSceneModels, testAiModelConnection } from "../../api/ai";
import Select from "../../components/form/Select";
import Switch from "../../components/form/switch/Switch";

type TestState = { status: "idle" } | { status: "loading" } | { status: "done"; result: AiConnectionTestResult };

function ConnectionTest({ model }: { model: string }) {
  const toast = useToast();
  const [state, setState] = useState<TestState>({ status: "idle" });

  useEffect(() => setState({ status: "idle" }), [model]);

  const run = async () => {
    setState({ status: "loading" });
    try {
      setState({ status: "done", result: await testAiModelConnection(model) });
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "测试失败");
      setState({ status: "idle" });
    }
  };

  return (
    <div className="flex items-center gap-3 shrink-0">
      <Button size="sm" variant="outline" type="button" disabled={!model || state.status === "loading"} onClick={() => void run()}>
        {state.status === "loading" ? "测试中..." : "测试连接"}
      </Button>
      {state.status === "done" &&
        (state.result.ok ? (
          <span className="text-sm text-success-600">✓ 可达 {state.result.latencyMs}ms</span>
        ) : (
          <span className="text-sm text-error-500 max-w-[12rem] truncate" title={state.result.message}>
            ✗ {state.result.message}
          </span>
        ))}
    </div>
  );
}

interface Props {
  scene: AiScene | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 模型配置弹窗：主模型 / 备用模型 / 深度思考。 */
export default function ModelConfigModal({ scene, onClose, onSaved }: Props) {
  const toast = useToast();
  const [models, setModels] = useState<string[]>([]);
  const [primary, setPrimary] = useState("");
  const [backup, setBackup] = useState("");
  const [thinking, setThinking] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!scene) return;
    setPrimary(scene.primaryModel ?? "");
    setBackup(scene.backupModel ?? "");
    setThinking(scene.thinkingEnabled ?? false);
    listAiModels()
      .then(setModels)
      .catch((err) => {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(ax.response?.data?.detail ?? "模型列表加载失败");
      });
  }, [scene, toast]);

  const sameModel = Boolean(primary) && primary === backup;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!scene || submitting || !primary || sameModel) return;
    setSubmitting(true);
    try {
      await setAiSceneModels(scene.id, { primaryModel: primary, backupModel: backup || null, thinkingEnabled: thinking });
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
      isOpen={scene !== null}
      onClose={() => !submitting && onClose()}
      showBackdrop={false}
      className="max-w-[640px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
    >
      <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
          配置模型：{scene?.name} ({scene?.sceneId})
        </h4>
        <form onSubmit={handleSubmit} noValidate className="space-y-5">
          <div>
            <Label>
              主模型 <span className="text-error-500">*</span>
            </Label>
            <div className="flex items-center gap-3">
              <Select
                placeholder="请选择"
                value={primary}
                onChange={setPrimary}
                options={models.map((m) => ({ value: m, label: m }))}
              />
              <ConnectionTest model={primary} />
            </div>
          </div>
          <div>
            <Label>备用模型</Label>
            <div className="flex items-center gap-3">
              <Select
                placeholder="不使用"
                value={backup}
                onChange={setBackup}
                options={models.map((m) => ({ value: m, label: m }))}
              />
              <ConnectionTest model={backup} />
            </div>
            {sameModel && <div className="text-error-500 text-xs mt-1">备用模型不能与主模型相同</div>}
          </div>
          <div>
            <Label>深度思考</Label>
            <div className="flex items-center gap-3">
              <Switch label={thinking ? "开启" : "关闭"} checked={thinking} disabled={submitting} onChange={setThinking} />
              <span className="text-xs text-gray-400">开启后响应更慢、token 消耗更高</span>
            </div>
          </div>
          <div className="text-xs text-gray-400">提示：连接成功只代表服务可达，不代表生成质量</div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              取消
            </button>
            <Button size="sm" disabled={submitting || !primary || sameModel}>
              {submitting ? "提交中..." : "保存"}
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}
