import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";
import { AxiosError } from "axios";
import Button from "../../components/ui/button/Button";
import PageMeta from "../../components/common/PageMeta";
import Label from "../../components/form/Label";
import ImageUploader from "../../components/form/ImageUploader";
import { useToast } from "../../context/ToastContext";
import { PERIOD_LABEL, PERIOD_VALUES, type Period } from "../../api/types";
import {
  CYCLE_ITEM_TYPES,
  CYCLE_ITEM_TYPE_LABELS,
  FeaturedCycleItemType,
  FeaturedCycleItemUpsertRequest,
  createFeaturedCycleItem,
  getFeaturedCycleItem,
  updateFeaturedCycleItem,
} from "../../api/featuredCycleItems";
import { ActivityItem, pageActivities } from "../../api/activities";
import { RouteItem, pageRoutes } from "../../api/routes";
import { ArticleItem, pageArticles } from "../../api/articles";

interface FieldError {
  field: string;
  message: string;
}

/** 下拉数据一次拉一页即可——运营配置级数据量。 */
const OPTION_PAGE_SIZE = 30;

const isPeriod = (v: string | null): v is Period =>
  v !== null && (PERIOD_VALUES as readonly string[]).includes(v);

export default function FeaturedCycleItemForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const [searchParams] = useSearchParams();
  const editing = Boolean(id);
  const toast = useToast();

  // 新增时归属周期取自列表 Tab 带来的 ?phase=；编辑时以条目自身周期为准，两种情况都不可改
  const phaseParam = searchParams.get("phase");
  const [phase, setPhase] = useState<Period>(isPeriod(phaseParam) ? phaseParam : "MENSTRUAL");
  const [type, setType] = useState<FeaturedCycleItemType>("ACTIVITY");
  const [bannerKey, setBannerKey] = useState("");
  const [bannerPreview, setBannerPreview] = useState("");
  const [sortOrder, setSortOrder] = useState("0");
  const [activityId, setActivityId] = useState("");
  const [routeId, setRouteId] = useState("");
  const [articleId, setArticleId] = useState("");
  const [title, setTitle] = useState("");
  const [subtitle, setSubtitle] = useState("");
  const [description, setDescription] = useState("");
  const [note, setNote] = useState("");
  const [online, setOnline] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const [activities, setActivities] = useState<ActivityItem[]>([]);
  const [routes, setRoutes] = useState<RouteItem[]>([]);
  const [articles, setArticles] = useState<ArticleItem[]>([]);

  useEffect(() => {
    void pageActivities({ size: OPTION_PAGE_SIZE })
      .then((p) => setActivities(p.content))
      .catch(() => undefined);
    void pageRoutes({ size: OPTION_PAGE_SIZE })
      .then((p) => setRoutes(p.content))
      .catch(() => undefined);
    void pageArticles({ size: OPTION_PAGE_SIZE })
      .then((p) => setArticles(p.content))
      .catch(() => undefined);
  }, []);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getFeaturedCycleItem(id)
      .then((d) => {
        setPhase(d.phase);
        setType(d.type);
        setBannerKey(d.banner?.id ?? "");
        setBannerPreview(d.banner?.url ?? "");
        setSortOrder(String(d.sortOrder));
        setActivityId(d.activityId ?? "");
        setRouteId(d.routeId ?? "");
        setArticleId(d.articleId ?? "");
        setTitle(d.title ?? "");
        setSubtitle(d.subtitle ?? "");
        setDescription(d.description ?? "");
        setNote(d.note ?? "");
        setOnline(d.online);
      })
      .catch((err) => {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(ax.response?.data?.detail ?? "加载失败");
        navigate("/featured-cycle-items");
      })
      .finally(() => setLoading(false));
    // toast/navigate 恒定，只按 id 重载
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  /** 切换内容类型：清空下方字段块，避免上一类型的内容残留。 */
  const handleTypeChange = (next: FeaturedCycleItemType) => {
    setType(next);
    setActivityId("");
    setRouteId("");
    setArticleId("");
    setTitle("");
    setSubtitle("");
    setDescription("");
    setNote("");
    setFieldErrors({});
  };

  /** 周期生活法：选中文章后带出文章标题，仍可手改。 */
  const handleArticleChange = (nextId: string) => {
    setArticleId(nextId);
    const article = articles.find((a) => a.id === nextId);
    if (article) setTitle(article.title);
  };

  const validate = (): Record<string, string> => {
    const errs: Record<string, string> = {};
    if (!bannerKey.trim()) errs.banner = "请上传 banner 图片";
    if (type === "ACTIVITY") {
      if (!activityId) errs.activityId = "请选择关联活动";
      if (!description.trim()) errs.description = "请填写推荐说明";
    }
    if (type === "ROUTE") {
      if (!routeId) errs.routeId = "请选择关联路线";
      if (!title.trim()) errs.title = "请填写主标题";
      if (!subtitle.trim()) errs.subtitle = "请填写副标题";
      if (!description.trim()) errs.description = "请填写推荐说明";
    }
    if (type === "ARTICLE") {
      if (!articleId) errs.articleId = "请选择关联文章";
      if (!title.trim()) errs.title = "请填写主标题";
    }
    return errs;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    const errs = validate();
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }
    setFieldErrors({});
    setSubmitting(true);
    try {
      const payload: FeaturedCycleItemUpsertRequest = {
        phase,
        type,
        banner: bannerKey.trim(),
        sortOrder: Number(sortOrder) || 0,
        online,
        activityId: type === "ACTIVITY" ? activityId : null,
        routeId: type === "ROUTE" ? routeId : null,
        articleId: type === "ARTICLE" ? articleId : null,
        title: type === "ACTIVITY" ? null : title.trim(),
        subtitle: type === "ROUTE" ? subtitle.trim() : null,
        description: type === "ARTICLE" ? null : description.trim(),
        note: type === "ACTIVITY" ? note.trim() || null : null,
      };
      if (id) await updateFeaturedCycleItem(id, payload);
      else await createFeaturedCycleItem(payload);
      toast.success(editing ? "保存成功" : "创建成功");
      navigate("/featured-cycle-items");
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
  const selectClass =
    "border rounded px-3 py-2 text-sm w-full h-11 disabled:bg-gray-100 disabled:text-gray-500";
  const textareaClass = "border rounded px-3 py-2 text-sm w-full min-h-[80px]";
  const inputClass = "border rounded px-3 py-2 text-sm w-full h-11";

  const error = (key: string) =>
    fieldErrors[key] ? <div className="text-error-500 text-xs mt-1">{fieldErrors[key]}</div> : null;

  return (
    <div>
      <PageMeta
        title={`${editing ? "编辑" : "新增"}周期推荐 | Love Space Admin`}
        description="精选·你的周期活动推荐配置"
      />
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑周期推荐" : "新增周期推荐"}
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
                <Label>所属周期</Label>
                <input className={inputClass} value={PERIOD_LABEL[phase]} disabled readOnly />
              </div>
              <div>
                <Label>
                  内容类型 <span className="text-error-500">*</span>
                </Label>
                <select
                  className={selectClass}
                  value={type}
                  disabled={editing}
                  onChange={(e) => handleTypeChange(e.target.value as FeaturedCycleItemType)}
                >
                  {CYCLE_ITEM_TYPES.map((t) => (
                    <option key={t} value={t}>
                      {CYCLE_ITEM_TYPE_LABELS[t]}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <Label>排序号</Label>
                <input
                  type="number"
                  className={inputClass}
                  value={sortOrder}
                  onChange={(e) => setSortOrder(e.target.value)}
                />
                <div className="text-xs text-gray-400 mt-1">同周期内从小到大排列</div>
              </div>
            </div>
            <div className="text-xs text-gray-400 mt-3">
              周期推荐创建后所属周期与内容类型不可修改
            </div>
          </fieldset>

          {/* 2. banner */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>
              banner 图片 <span className="text-error-500">*</span>
            </legend>
            <ImageUploader
              value={bannerKey}
              previewUrl={bannerPreview}
              onChange={setBannerKey}
              className="h-28 w-48"
            />
            {error("banner")}
          </fieldset>

          {/* 3. 内容配置——字段随内容类型分派 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>{CYCLE_ITEM_TYPE_LABELS[type]}配置</legend>

            {type === "ACTIVITY" && (
              <div className="space-y-4">
                <div>
                  <Label>
                    关联活动 <span className="text-error-500">*</span>
                  </Label>
                  <select
                    className={selectClass}
                    value={activityId}
                    onChange={(e) => setActivityId(e.target.value)}
                  >
                    <option value="">请选择</option>
                    {activities.map((a) => (
                      <option key={a.id} value={a.id}>
                        {a.title}
                        {a.online ? "" : "（已下线）"}
                      </option>
                    ))}
                  </select>
                  {error("activityId")}
                </div>
                <div>
                  <Label>
                    推荐说明 <span className="text-error-500">*</span>
                  </Label>
                  <textarea
                    className={textareaClass}
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                  {error("description")}
                </div>
                <div>
                  <Label>活动说明</Label>
                  <textarea
                    placeholder="活动说明（选填）"
                    className={textareaClass}
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                  />
                </div>
              </div>
            )}

            {type === "ROUTE" && (
              <div className="space-y-4">
                <div>
                  <Label>
                    关联路线 <span className="text-error-500">*</span>
                  </Label>
                  <select
                    className={selectClass}
                    value={routeId}
                    onChange={(e) => setRouteId(e.target.value)}
                  >
                    <option value="">请选择</option>
                    {routes.map((r) => (
                      <option key={r.id} value={r.id}>
                        {r.title}
                      </option>
                    ))}
                  </select>
                  {error("routeId")}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <Label>
                      主标题 <span className="text-error-500">*</span>
                    </Label>
                    <input
                      className={inputClass}
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                    />
                    {error("title")}
                  </div>
                  <div>
                    <Label>
                      副标题 <span className="text-error-500">*</span>
                    </Label>
                    <input
                      className={inputClass}
                      value={subtitle}
                      onChange={(e) => setSubtitle(e.target.value)}
                    />
                    {error("subtitle")}
                  </div>
                </div>
                <div>
                  <Label>
                    推荐说明 <span className="text-error-500">*</span>
                  </Label>
                  <textarea
                    className={textareaClass}
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                  {error("description")}
                </div>
              </div>
            )}

            {type === "ARTICLE" && (
              <div className="space-y-4">
                <div>
                  <Label>
                    关联文章 <span className="text-error-500">*</span>
                  </Label>
                  <select
                    className={selectClass}
                    value={articleId}
                    onChange={(e) => handleArticleChange(e.target.value)}
                  >
                    <option value="">请选择</option>
                    {articles.map((a) => (
                      <option key={a.id} value={a.id}>
                        {a.title}
                        {a.online ? "" : "（已下线）"}
                      </option>
                    ))}
                  </select>
                  {error("articleId")}
                </div>
                <div>
                  <Label>
                    主标题 <span className="text-error-500">*</span>
                  </Label>
                  <input
                    className={inputClass}
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                  />
                  <div className="text-xs text-gray-400 mt-1">
                    选中文章后自动带出文章标题，可修改
                  </div>
                  {error("title")}
                </div>
              </div>
            )}
          </fieldset>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/featured-cycle-items")}
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
