import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Switch from "../../components/form/switch/Switch";
import Button from "../../components/ui/button/Button";
import ImageUploader from "../../components/form/ImageUploader";
import {
  CityUpsertRequest,
  createCity,
  getCity,
  updateCity,
} from "../../api/cities";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

export default function CityForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [chineseName, setChineseName] = useState("");
  const [englishName, setEnglishName] = useState("");
  const [chineseProvince, setChineseProvince] = useState("");
  const [englishProvince, setEnglishProvince] = useState("");
  const [backgroundImageKey, setBackgroundImageKey] = useState("");
  const [backgroundImagePreview, setBackgroundImagePreview] = useState("");
  const [editorNote, setEditorNote] = useState("");
  const [online, setOnline] = useState(false);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getCity(id)
      .then((d) => {
        setChineseName(d.chineseName);
        setEnglishName(d.englishName);
        setChineseProvince(d.chineseProvince);
        setEnglishProvince(d.englishProvince);
        setBackgroundImageKey(d.backgroundImage?.id ?? "");
        setBackgroundImagePreview(d.backgroundImage?.url ?? "");
        setEditorNote(d.editorNote ?? "");
        setOnline(d.online);
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!chineseName.trim()) errs.chineseName = "中文名不能为空";
    if (!englishName.trim()) errs.englishName = "英文名不能为空";
    if (!chineseProvince.trim()) errs.chineseProvince = "中文省份不能为空";
    if (!englishProvince.trim()) errs.englishProvince = "英文省份不能为空";
    if (editorNote.length > 200) errs.editorNote = "编辑说最多 200 字";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: CityUpsertRequest = {
      chineseName: chineseName.trim(),
      englishName: englishName.trim(),
      chineseProvince: chineseProvince.trim(),
      englishProvince: englishProvince.trim(),
      backgroundImage: backgroundImageKey.trim() || null,
      editorNote: editorNote.trim() || null,
      online,
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateCity(id, payload);
      else await createCity(payload);
      navigate("/cities", { replace: true });
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string; errors?: FieldError[] }>;
      const data = ax.response?.data;
      if (data?.errors?.length) {
        const map: Record<string, string> = {};
        for (const fe of data.errors) map[fe.field] = fe.message;
        setFieldErrors(map);
      }
      toast.error(data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑地图" : "新增地图"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} className="max-w-2xl space-y-5">
          <div>
            <Label>
              中文名 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={chineseName}
              onChange={(e) => setChineseName(e.target.value)}
              error={Boolean(fieldErrors.chineseName)}
              hint={fieldErrors.chineseName}
            />
          </div>
          <div>
            <Label>
              英文名 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={englishName}
              onChange={(e) => setEnglishName(e.target.value)}
              error={Boolean(fieldErrors.englishName)}
              hint={fieldErrors.englishName}
            />
          </div>
          <div>
            <Label>
              中文省份 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={chineseProvince}
              onChange={(e) => setChineseProvince(e.target.value)}
              error={Boolean(fieldErrors.chineseProvince)}
              hint={fieldErrors.chineseProvince}
            />
          </div>
          <div>
            <Label>
              英文省份 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={englishProvince}
              onChange={(e) => setEnglishProvince(e.target.value)}
              error={Boolean(fieldErrors.englishProvince)}
              hint={fieldErrors.englishProvince}
            />
          </div>
          <div>
            <Label>背景图</Label>
            <ImageUploader
              value={backgroundImageKey}
              previewUrl={backgroundImagePreview}
              onChange={setBackgroundImageKey}
              className="h-40 w-full max-w-md"
            />
          </div>
          <div>
            <Label>编辑说</Label>
            <textarea
              placeholder="编辑说（≤200 字，选填，App 端展示）"
              value={editorNote}
              onChange={(e) => setEditorNote(e.target.value)}
              maxLength={200}
              className="border rounded px-3 py-2 text-sm w-full min-h-[100px]"
            />
            {fieldErrors.editorNote && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.editorNote}</div>
            )}
            <div className="text-xs text-gray-400 mt-1">{editorNote.length} / 200</div>
          </div>
          <div>
            <Label>上架</Label>
            <Switch
              label={online ? "已上架" : "未上架"}
              defaultChecked={online}
              onChange={setOnline}
            />
          </div>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/cities")}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              取消
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
