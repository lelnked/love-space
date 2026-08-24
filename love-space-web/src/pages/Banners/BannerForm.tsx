import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import ImageUploaderList, {
  ImageListItem,
} from "../../components/form/ImageUploaderList";
import {
  BannerType,
  BannerUpsertRequest,
  createBanner,
  getBanner,
  updateBanner,
} from "../../api/banners";
import CitySelect from "./components/CitySelect";
import { useToast } from "../../context/ToastContext";

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
  const [positionCode, setPositionCode] = useState("");
  const [type, setType] = useState<BannerType>("CITY");
  const [images, setImages] = useState<ImageListItem[]>([]);
  const [link, setLink] = useState<string>("");
  const [sortOrder, setSortOrder] = useState("0");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getBanner(id)
      .then((d) => {
        setName(d.name);
        setPositionCode(d.positionCode);
        setType(d.type);
        setImages(d.imageUrls.map((im) => ({ objectKey: im.id, previewUrl: im.url })));
        setLink(d.link);
        setSortOrder(String(d.sortOrder));
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
    if (!name.trim()) errs.name = "名称不能为空";
    if (!positionCode.trim()) errs.positionCode = "位置标识不能为空";
    if (images.length === 0) errs.imageUrls = "至少上传 1 张图片";
    if (!link) errs.link = "请选择关联地图";
    const sortValue = Number(sortOrder);
    if (!Number.isInteger(sortValue) || sortValue < 0) errs.sortOrder = "排序值需为非负整数";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: BannerUpsertRequest = {
      name: name.trim(),
      positionCode: positionCode.trim(),
      type,
      imageUrls: images.map((it) => it.objectKey),
      link,
      sortOrder: sortValue,
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
      toast.error(data?.detail ?? "保存失败");
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
              位置标识 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={positionCode}
              onChange={(e) => setPositionCode(e.target.value)}
              error={Boolean(fieldErrors.positionCode)}
              hint={fieldErrors.positionCode}
            />
          </div>

          <div>
            <Label>
              排序（越小越靠前） <span className="text-error-500">*</span>
            </Label>
            <Input
              type="number"
              min="0"
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
              error={Boolean(fieldErrors.sortOrder)}
              hint={fieldErrors.sortOrder}
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
              关联地图 <span className="text-error-500">*</span>
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
            {fieldErrors.imageUrls && (
              <p className="mb-2 text-xs text-error-500">{fieldErrors.imageUrls}</p>
            )}
            <ImageUploaderList value={images} onChange={setImages} />
          </div>

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
