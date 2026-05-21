import { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import {
  CityUpsertRequest,
  createCity,
  getCity,
  updateCity,
} from "../../api/cities";
import { uploadFile } from "../../api/files";

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
  const [backgroundImage, setBackgroundImage] = useState("");
  const [bannerSortOrder, setBannerSortOrder] = useState<number>(0);
  const [online, setOnline] = useState(false);

  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getCity(id)
      .then((d) => {
        setChineseName(d.chineseName);
        setEnglishName(d.englishName);
        setChineseProvince(d.chineseProvince);
        setEnglishProvince(d.englishProvince);
        setBackgroundImage(d.backgroundImage ?? "");
        setBannerSortOrder(d.bannerSortOrder ?? 0);
        setOnline(d.online);
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        setError(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleUpload = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const { url } = await uploadFile(file);
      setBackgroundImage(url);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "上传失败");
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setError(null);
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!chineseName.trim()) errs.chineseName = "中文名不能为空";
    if (!englishName.trim()) errs.englishName = "英文名不能为空";
    if (!chineseProvince.trim()) errs.chineseProvince = "中文省份不能为空";
    if (!englishProvince.trim()) errs.englishProvince = "英文省份不能为空";
    if (!Number.isInteger(bannerSortOrder) || bannerSortOrder < 0) {
      errs.bannerSortOrder = "必须为非负整数";
    }
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: CityUpsertRequest = {
      chineseName: chineseName.trim(),
      englishName: englishName.trim(),
      chineseProvince: chineseProvince.trim(),
      englishProvince: englishProvince.trim(),
      backgroundImage: backgroundImage.trim() || null,
      bannerSortOrder,
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
      setError(data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑城市" : "新增城市"}
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
            <Label>背景图 URL</Label>
            <Input
              placeholder="可手动填入 URL，或下方上传"
              value={backgroundImage}
              onChange={(e) => setBackgroundImage(e.target.value)}
            />
            <div className="mt-2 flex items-center gap-2 text-xs text-gray-500">
              <input type="file" accept="image/*" onChange={handleUpload} disabled={uploading} />
              {uploading && <span>上传中...</span>}
            </div>
            {backgroundImage && (
              <img
                src={backgroundImage}
                alt="背景图"
                className="mt-2 h-32 object-cover rounded border"
              />
            )}
          </div>
          <div>
            <Label>Banner 排序权重</Label>
            <Input
              type="number"
              value={String(bannerSortOrder)}
              onChange={(e) => setBannerSortOrder(Number(e.target.value))}
              error={Boolean(fieldErrors.bannerSortOrder)}
              hint={fieldErrors.bannerSortOrder}
            />
            <p className="mt-1 text-xs text-gray-500">
              {">"}0 时自动作为 explore banner 展示，数值越小越靠前；=0 则不参与 banner；不影响城市列表排序
            </p>
          </div>
          <div>
            <label className="inline-flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
              <input
                type="checkbox"
                checked={online}
                onChange={(e) => setOnline(e.target.checked)}
              />
              上架
            </label>
          </div>

          {error && <div className="text-error-500 text-sm">{error}</div>}

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
