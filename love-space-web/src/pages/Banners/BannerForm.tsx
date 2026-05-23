import { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import {
  BannerType,
  BannerUpsertRequest,
  createBanner,
  getBanner,
  updateBanner,
} from "../../api/banners";
import { uploadFile } from "../../api/files";
import CitySelect from "./components/CitySelect";

interface FieldError {
  field: string;
  message: string;
}

const TYPE_OPTIONS: { label: string; value: BannerType }[] = [
  { label: "城市", value: "CITY" },
];

export default function BannerForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [name, setName] = useState("");
  const [type, setType] = useState<BannerType>("CITY");
  const [imageKeys, setImageKeys] = useState<string[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [link, setLink] = useState<string>("");

  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getBanner(id)
      .then((d) => {
        setName(d.name);
        setType(d.type);
        setImageKeys(d.imageUrls.map((im) => im.id));
        setImagePreviews(d.imageUrls.map((im) => im.url));
        setLink(d.link);
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
      const { url: objectKey } = await uploadFile(file);
      setImageKeys((prev) => [...prev, objectKey]);
      setImagePreviews((prev) => [...prev, URL.createObjectURL(file)]);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "上传失败");
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  const removeImage = (idx: number) => {
    setImageKeys((prev) => prev.filter((_, i) => i !== idx));
    setImagePreviews((prev) => prev.filter((_, i) => i !== idx));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setError(null);
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!name.trim()) errs.name = "名称不能为空";
    if (imageKeys.length === 0) errs.imageUrls = "至少上传 1 张图片";
    if (!link) errs.link = "请选择关联城市";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: BannerUpsertRequest = {
      name: name.trim(),
      type,
      imageUrls: imageKeys,
      link,
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateBanner(id, payload);
      else await createBanner(payload);
      navigate("/banners", { replace: true });
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
        {editing ? "编辑 Banner" : "新增 Banner"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} className="max-w-2xl space-y-5">
          <div>
            <Label>
              名称 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={Boolean(fieldErrors.name)}
              hint={fieldErrors.name}
            />
          </div>

          <div>
            <Label>
              类型 <span className="text-error-500">*</span>
            </Label>
            <select
              value={type}
              onChange={(e) => setType(e.target.value as BannerType)}
              className="w-full px-3 py-2 text-sm rounded-lg border border-gray-300 bg-white dark:bg-gray-900 dark:border-gray-700"
            >
              {TYPE_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>

          <div>
            <Label>
              关联城市 <span className="text-error-500">*</span>
            </Label>
            <CitySelect
              value={link || null}
              onChange={(cityId) => setLink(cityId)}
              error={Boolean(fieldErrors.link)}
              hint={fieldErrors.link}
            />
          </div>

          <div>
            <Label>
              图片 <span className="text-error-500">*</span>
            </Label>
            <div className="flex items-center gap-2 text-xs text-gray-500">
              <input type="file" accept="image/*" onChange={handleUpload} disabled={uploading} />
              {uploading && <span>上传中...</span>}
            </div>
            {fieldErrors.imageUrls && (
              <p className="mt-1 text-xs text-error-500">{fieldErrors.imageUrls}</p>
            )}
            <div className="mt-2 flex flex-wrap gap-3">
              {imageKeys.map((key, idx) => (
                <div key={`${key}-${idx}`} className="relative">
                  <img src={imagePreviews[idx]} alt={`图片 ${idx + 1}`} className="h-24 w-24 object-cover rounded border" />
                  <button
                    type="button"
                    onClick={() => removeImage(idx)}
                    className="absolute -top-2 -right-2 bg-white border rounded-full w-5 h-5 text-xs leading-4 text-error-500"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          </div>

          {error && <div className="text-error-500 text-sm">{error}</div>}

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/banners")}
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
