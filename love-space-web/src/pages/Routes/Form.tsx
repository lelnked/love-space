import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import ImageUploader from "../../components/form/ImageUploader";
import ImageUploaderList, { ImageListItem } from "../../components/form/ImageUploaderList";
import { createRoute, getRoute, RouteUpsertRequest, updateRoute } from "../../api/routes";
import { AmbassadorItem, pageAmbassadors } from "../../api/ambassadors";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

/** 地点子项的表单态：图片持 objectKey + 预览 URL。 */
interface SpotRow {
  name: string;
  imageKey: string;
  imagePreview: string;
  introduction: string;
}

export default function RouteForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [cityName, setCityName] = useState("");
  const [sortOrder, setSortOrder] = useState<number>(0);
  const [title, setTitle] = useState("");
  const [ambassadorNote, setAmbassadorNote] = useState("");
  const [thumbnailKey, setThumbnailKey] = useState("");
  const [thumbnailPreview, setThumbnailPreview] = useState("");
  const [images, setImages] = useState<ImageListItem[]>([]);
  const [travelTime, setTravelTime] = useState("");
  const [season, setSeason] = useState("");
  const [travelStatus, setTravelStatus] = useState("");
  const [ambassadorId, setAmbassadorId] = useState("");
  const [spots, setSpots] = useState<SpotRow[]>([]);
  const [ambassadors, setAmbassadors] = useState<AmbassadorItem[]>([]);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    void pageAmbassadors({ page: 1, size: 200 })
      .then((d) => setAmbassadors(d.content))
      .catch(() => undefined);
    if (!id) {
      return;
    }
    setLoading(true);
    getRoute(id)
      .then((d) => {
        setCityName(d.cityName ?? "");
        setSortOrder(d.sortOrder);
        setTitle(d.title);
        setAmbassadorNote(d.ambassadorNote ?? "");
        setThumbnailKey(d.thumbnail.id);
        setThumbnailPreview(d.thumbnail.url);
        setImages(d.images.map((im) => ({ objectKey: im.id, previewUrl: im.url })));
        setTravelTime(d.travelTime ?? "");
        setSeason(d.season ?? "");
        setTravelStatus(d.travelStatus ?? "");
        setAmbassadorId(d.ambassadorId);
        setSpots(
          d.spots.map((s) => ({
            name: s.name,
            imageKey: s.image.id,
            imagePreview: s.image.url,
            introduction: s.introduction,
          })),
        );
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const updateSpot = (index: number, patch: Partial<SpotRow>) =>
    setSpots((prev) => prev.map((s, i) => (i === index ? { ...s, ...patch } : s)));

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!cityName.trim()) errs.cityName = "请输入所属城市名";
    if (!title.trim()) errs.title = "路线标题不能为空";
    if (!thumbnailKey.trim()) errs.thumbnail = "请上传缩略图";
    if (images.length === 0) errs.images = "至少上传 1 张图片";
    if (!ambassadorId) errs.ambassadorId = "请选择关联大使";
    spots.forEach((s, i) => {
      if (!s.name.trim()) errs[`spots.${i}.name`] = "地点名称不能为空";
      if (!s.imageKey.trim()) errs[`spots.${i}.image`] = "请上传地点图片";
      if (!s.introduction.trim()) errs[`spots.${i}.introduction`] = "地点介绍不能为空";
    });
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: RouteUpsertRequest = {
      cityName: cityName.trim(),
      sortOrder,
      title: title.trim(),
      ambassadorNote: ambassadorNote.trim() || null,
      thumbnail: thumbnailKey.trim(),
      images: images.map((it) => it.objectKey.trim()),
      travelTime: travelTime.trim() || null,
      season: season.trim() || null,
      travelStatus: travelStatus.trim() || null,
      ambassadorId,
      spots: spots.map((s) => ({
        name: s.name.trim(),
        image: s.imageKey.trim(),
        introduction: s.introduction.trim(),
      })),
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateRoute(id, payload);
      else await createRoute(payload);
      navigate("/routes", { replace: true });
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

  const sectionClass =
    "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900";
  const sectionTitleClass = "text-sm font-semibold text-gray-800 dark:text-white/90 mb-3";

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑路线" : "新增路线"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} noValidate className="max-w-4xl space-y-5">
          {/* 1. 基础信息 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>基础信息</legend>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label>
                  所属城市 <span className="text-error-500">*</span>
                </Label>
                <Input
                  value={cityName}
                  onChange={(e) => setCityName(e.target.value)}
                  error={Boolean(fieldErrors.cityName)}
                  hint={fieldErrors.cityName}
                />
                <div className="text-xs text-gray-400 mt-1">
                  输入已有的城市中文名，创建/编辑时写入路线并校验城市库。
                </div>
              </div>
              <div>
                <Label>
                  主标题 <span className="text-error-500">*</span>
                </Label>
                <Input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  error={Boolean(fieldErrors.title)}
                  hint={fieldErrors.title}
                />
              </div>
              <div>
                <Label>排序号</Label>
                <Input
                  type="number"
                  value={String(sortOrder)}
                  onChange={(e) => setSortOrder(Number(e.target.value))}
                />
              </div>
              <div>
                <Label>
                  关联大使 <span className="text-error-500">*</span>
                </Label>
                <select
                  className="border rounded px-3 py-2 text-sm w-full h-11"
                  value={ambassadorId}
                  onChange={(e) => setAmbassadorId(e.target.value)}
                >
                  <option value="">请选择</option>
                  {ambassadors.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.name}
                      {a.online ? "" : "（已下线）"}
                    </option>
                  ))}
                </select>
                {fieldErrors.ambassadorId && (
                  <div className="text-error-500 text-xs mt-1">{fieldErrors.ambassadorId}</div>
                )}
              </div>
              <div>
                <Label>旅行时间</Label>
                <Input value={travelTime} onChange={(e) => setTravelTime(e.target.value)} />
              </div>
              <div>
                <Label>适合季节</Label>
                <Input value={season} onChange={(e) => setSeason(e.target.value)} />
              </div>
              <div>
                <Label>旅行状态</Label>
                <Input value={travelStatus} onChange={(e) => setTravelStatus(e.target.value)} />
              </div>
            </div>
          </fieldset>

          {/* 2. 图片 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>图片</legend>
            <div>
              <Label>
                缩略图 <span className="text-error-500">*</span>
              </Label>
              <ImageUploader
                value={thumbnailKey}
                previewUrl={thumbnailPreview}
                onChange={setThumbnailKey}
                className="h-32 w-32"
              />
              {fieldErrors.thumbnail && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.thumbnail}</div>
              )}
            </div>
            <div className="mt-5">
              <Label>图片列表（至少 1 张）</Label>
              {fieldErrors.images && (
                <div className="text-error-500 text-xs mb-2">{fieldErrors.images}</div>
              )}
              <ImageUploaderList value={images} onChange={setImages} />
            </div>
          </fieldset>

          {/* 3. 爱女大使说 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>爱女大使说</legend>
            <textarea
              placeholder="爱女大使说（选填）"
              value={ambassadorNote}
              onChange={(e) => setAmbassadorNote(e.target.value)}
              className="border rounded px-3 py-2 text-sm w-full min-h-[100px]"
            />
          </fieldset>

          {/* 4. 地点列表 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>地点列表</legend>
            <div className="space-y-4">
              {spots.map((s, i) => (
                <div
                  key={i}
                  className="border border-gray-200 dark:border-gray-800 rounded-lg p-3 space-y-3"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">地点 {i + 1}</span>
                    <Button
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() => setSpots((prev) => prev.filter((_, j) => j !== i))}
                    >
                      删除地点
                    </Button>
                  </div>
                  <div>
                    <Label>
                      名称 <span className="text-error-500">*</span>
                    </Label>
                    <Input
                      value={s.name}
                      onChange={(e) => updateSpot(i, { name: e.target.value })}
                      error={Boolean(fieldErrors[`spots.${i}.name`])}
                      hint={fieldErrors[`spots.${i}.name`]}
                    />
                  </div>
                  <div>
                    <Label>
                      图片 <span className="text-error-500">*</span>
                    </Label>
                    <ImageUploader
                      value={s.imageKey}
                      previewUrl={s.imagePreview}
                      onChange={(key) => updateSpot(i, { imageKey: key })}
                      className="h-28 w-28"
                    />
                    {fieldErrors[`spots.${i}.image`] && (
                      <div className="text-error-500 text-xs mt-1">
                        {fieldErrors[`spots.${i}.image`]}
                      </div>
                    )}
                  </div>
                  <div>
                    <Label>
                      介绍 <span className="text-error-500">*</span>
                    </Label>
                    <textarea
                      placeholder="地点介绍"
                      value={s.introduction}
                      onChange={(e) => updateSpot(i, { introduction: e.target.value })}
                      className="border rounded px-3 py-2 text-sm w-full min-h-[80px]"
                    />
                    {fieldErrors[`spots.${i}.introduction`] && (
                      <div className="text-error-500 text-xs mt-1">
                        {fieldErrors[`spots.${i}.introduction`]}
                      </div>
                    )}
                  </div>
                </div>
              ))}
              <Button
                type="button"
                size="sm"
                variant="outline"
                onClick={() =>
                  setSpots((prev) => [
                    ...prev,
                    { name: "", imageKey: "", imagePreview: "", introduction: "" },
                  ])
                }
              >
                添加地点
              </Button>
            </div>
          </fieldset>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/routes")}
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
