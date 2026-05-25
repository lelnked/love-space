import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import ImageUploader from "../../components/form/ImageUploader";
import ImageUploaderList, {
  ImageListItem,
} from "../../components/form/ImageUploaderList";
import Switch from "../../components/form/switch/Switch";
import Checkbox from "../../components/form/input/Checkbox";
import {
  createMerchant,
  getMerchant,
  MerchantUpsertRequest,
  updateMerchant,
} from "../../api/merchants";
import { CityItem, listCities } from "../../api/cities";
import { CategoryItem, listCategories } from "../../api/categories";
import { listTags, TagItem } from "../../api/tags";
import { PERIOD_LABEL, PERIOD_VALUES, Period } from "../../api/types";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

export default function MerchantForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  // 基础信息
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [longitude, setLongitude] = useState("");
  const [latitude, setLatitude] = useState("");
  // 图片：logo 用 objectKey + 预览 URL；images 用 ImageRow 列表
  const [logoKey, setLogoKey] = useState("");
  const [logoPreview, setLogoPreview] = useState("");
  const [images, setImages] = useState<ImageListItem[]>([]);
  // 周期+分类+城市
  const [periods, setPeriods] = useState<Period[]>([]);
  const [categoryId, setCategoryId] = useState("");
  const [cityId, setCityId] = useState("");
  // 标签
  const [tagIds, setTagIds] = useState<string[]>([]);
  // 四维评分
  const [safetyEnvironmentScore, setSafetyEnvironmentScore] = useState<number>(0);
  const [businessRightsScore, setBusinessRightsScore] = useState<number>(0);
  const [experienceFriendlyScore, setExperienceFriendlyScore] = useState<number>(0);
  const [socialContributionScore, setSocialContributionScore] = useState<number>(0);
  // 故事
  const [story, setStory] = useState("");
  // 权重+上下架
  const [weight, setWeight] = useState<number>(0);
  const [online, setOnline] = useState(false);

  // 选项数据
  const [cities, setCities] = useState<CityItem[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [tags, setTags] = useState<TagItem[]>([]);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
    void listCategories().then(setCategories).catch(() => undefined);
    void listTags({ online: true }).then(setTags).catch(() => undefined);
  }, []);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getMerchant(id)
      .then((d) => {
        setName(d.name);
        setAddress(d.address);
        setLongitude(d.longitude !== null && d.longitude !== undefined ? String(d.longitude) : "");
        setLatitude(d.latitude !== null && d.latitude !== undefined ? String(d.latitude) : "");
        setLogoKey(d.logo?.id ?? "");
        setLogoPreview(d.logo?.url ?? "");
        setImages(d.images.map((im) => ({ objectKey: im.id, previewUrl: im.url })));
        setPeriods(d.periods);
        setCategoryId(d.categoryId ?? "");
        setCityId(d.cityId);
        setTagIds(d.tagIds);
        setSafetyEnvironmentScore(d.safetyEnvironmentScore);
        setBusinessRightsScore(d.businessRightsScore);
        setExperienceFriendlyScore(d.experienceFriendlyScore);
        setSocialContributionScore(d.socialContributionScore);
        setStory(d.story ?? "");
        setWeight(d.weight);
        setOnline(d.online);
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const togglePeriod = (p: Period) =>
    setPeriods((prev) =>
      prev.includes(p) ? prev.filter((x) => x !== p) : [...prev, p],
    );

  const toggleTag = (tagId: string) =>
    setTagIds((prev) =>
      prev.includes(tagId) ? prev.filter((x) => x !== tagId) : [...prev, tagId],
    );

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!name.trim()) errs.name = "名称不能为空";
    else if (Array.from(name.trim()).length > 15) errs.name = "名称最多 15 个字符";
    if (!logoKey.trim()) errs.logo = "请上传 LOGO";
    if (!address.trim()) errs.address = "地址不能为空";
    if (!cityId) errs.cityId = "请选择城市";
    if (images.length === 0) errs.images = "至少上传 1 张图片";
    images.forEach((im, i) => {
      if (!im.objectKey.trim()) errs[`images.${i}.url`] = "请上传图片";
    });
    if (story.length > 5000) errs.story = "故事最多 5000 字";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: MerchantUpsertRequest = {
      name: name.trim(),
      logo: logoKey.trim(),
      address: address.trim(),
      longitude: longitude === "" ? null : longitude,
      latitude: latitude === "" ? null : latitude,
      cityId,
      categoryId: categoryId || null,
      safetyEnvironmentScore,
      businessRightsScore,
      experienceFriendlyScore,
      socialContributionScore,
      story: story.trim() || null,
      weight,
      online,
      periods,
      tagIds,
      images: images.map((it) => it.objectKey.trim()),
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateMerchant(id, payload);
      else await createMerchant(payload);
      navigate("/merchants", { replace: true });
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
  const sectionTitleClass =
    "text-sm font-semibold text-gray-800 dark:text-white/90 mb-3";

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑商户" : "新增商户"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} className="max-w-4xl space-y-5">
          {/* 1. 基础信息 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>基础信息</legend>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                  地址 <span className="text-error-500">*</span>
                </Label>
                <Input
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  error={Boolean(fieldErrors.address)}
                  hint={fieldErrors.address}
                />
              </div>
              <div>
                <Label>经度</Label>
                <Input
                  type="number"
                  value={longitude}
                  onChange={(e) => setLongitude(e.target.value)}
                />
              </div>
              <div>
                <Label>纬度</Label>
                <Input
                  type="number"
                  value={latitude}
                  onChange={(e) => setLatitude(e.target.value)}
                />
              </div>
            </div>
          </fieldset>

          {/* 2. 图片 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>图片</legend>
            <div>
              <Label>
                LOGO <span className="text-error-500">*</span>
              </Label>
              <ImageUploader
                value={logoKey}
                previewUrl={logoPreview}
                onChange={setLogoKey}
                className="h-32 w-32"
              />
              {fieldErrors.logo && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.logo}</div>
              )}
            </div>

            <div className="mt-5">
              <Label>图片列表（至少 1 张）</Label>
              {fieldErrors.images && (
                <div className="text-error-500 text-xs mb-2">{fieldErrors.images}</div>
              )}
              <ImageUploaderList
                value={images}
                onChange={setImages}
                errors={images.map((_, i) => fieldErrors[`images.${i}.url`])}
              />
            </div>
          </fieldset>

          {/* 3. 周期 + 分类 + 城市 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>推荐周期 / 分类 / 城市</legend>
            <div className="mb-3">
              <Label>推荐生理周期</Label>
              <div className="flex flex-wrap gap-4">
                {PERIOD_VALUES.map((p) => (
                  <Checkbox
                    key={p}
                    label={PERIOD_LABEL[p]}
                    checked={periods.includes(p)}
                    onChange={() => togglePeriod(p)}
                  />
                ))}
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label>
                  城市 <span className="text-error-500">*</span>
                </Label>
                <select
                  className="border rounded px-3 py-2 text-sm w-full h-11"
                  value={cityId}
                  onChange={(e) => setCityId(e.target.value)}
                >
                  <option value="">请选择</option>
                  {cities.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.chineseName}
                    </option>
                  ))}
                </select>
                {fieldErrors.cityId && (
                  <div className="text-error-500 text-xs mt-1">{fieldErrors.cityId}</div>
                )}
              </div>
              <div>
                <Label>分类</Label>
                <select
                  className="border rounded px-3 py-2 text-sm w-full h-11"
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                >
                  <option value="">未分类</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </fieldset>

          {/* 4. 标签 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>标签</legend>
            <div className="flex flex-wrap gap-2">
              {tags.length === 0 && <span className="text-xs text-gray-400">暂无在架标签</span>}
              {tags.map((t) => {
                const checked = tagIds.includes(t.id);
                return (
                  <button
                    type="button"
                    key={t.id}
                    onClick={() => toggleTag(t.id)}
                    className={`px-3 py-1 rounded-full text-xs border ${
                      checked
                        ? "bg-brand-500 text-white border-brand-500"
                        : "bg-white text-gray-700 border-gray-300"
                    }`}
                  >
                    {t.name}
                  </button>
                );
              })}
            </div>
          </fieldset>

          {/* 5. 四维评分 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>四维评分</legend>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label>安全环境分（0-30）</Label>
                <Input
                  type="number"
                  value={String(safetyEnvironmentScore)}
                  onChange={(e) => setSafetyEnvironmentScore(Number(e.target.value))}
                  min="0"
                  max="30"
                />
              </div>
              <div>
                <Label>经营权益分（0-25）</Label>
                <Input
                  type="number"
                  value={String(businessRightsScore)}
                  onChange={(e) => setBusinessRightsScore(Number(e.target.value))}
                  min="0"
                  max="25"
                />
              </div>
              <div>
                <Label>体验友好分（0-25）</Label>
                <Input
                  type="number"
                  value={String(experienceFriendlyScore)}
                  onChange={(e) => setExperienceFriendlyScore(Number(e.target.value))}
                  min="0"
                  max="25"
                />
              </div>
              <div>
                <Label>社会贡献分（0-20）</Label>
                <Input
                  type="number"
                  value={String(socialContributionScore)}
                  onChange={(e) => setSocialContributionScore(Number(e.target.value))}
                  min="0"
                  max="20"
                />
              </div>
            </div>
          </fieldset>

          {/* 7. 权重 + 上下架 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>权重 / 上下架</legend>
            <div className="grid grid-cols-1 gap-4">
              <div>
                <Label>权重</Label>
                <Input
                  type="number"
                  value={String(weight)}
                  onChange={(e) => setWeight(Number(e.target.value))}
                />
              </div>
              <div>
                <Switch
                  label="上架"
                  defaultChecked={online}
                  onChange={setOnline}
                />
              </div>
            </div>
          </fieldset>

          {/* 8. 故事 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>商户故事</legend>
            <textarea
              placeholder="商户故事（≤5000 字）"
              value={story}
              onChange={(e) => setStory(e.target.value)}
              maxLength={5000}
              className="border rounded px-3 py-2 text-sm w-full min-h-[140px]"
            />
            {fieldErrors.story && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.story}</div>
            )}
            <div className="text-xs text-gray-400 mt-1">{story.length} / 5000</div>
          </fieldset>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/merchants")}
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
