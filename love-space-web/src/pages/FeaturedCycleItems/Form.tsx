import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import Button from "../../components/ui/button/Button";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import ImageUploader from "../../components/form/ImageUploader";
import { useToast } from "../../context/ToastContext";
import type { Period } from "../../api/types";
import {
  CYCLE_ITEM_TYPES,
  CYCLE_ITEM_TYPE_LABELS,
  FeaturedCycleItem,
  FeaturedCycleItemType,
  FeaturedCycleItemUpsertRequest,
  createFeaturedCycleItem,
  updateFeaturedCycleItem,
} from "../../api/featuredCycleItems";
import { ActivityItem, pageActivities } from "../../api/activities";
import { RouteItem, pageRoutes } from "../../api/routes";
import { ArticleItem, pageArticles } from "../../api/articles";

interface FieldError {
  field: string;
  message: string;
}

interface Props {
  open: boolean;
  /** 新增时条目归属的周期（取自当前 Tab）；编辑时以条目自身周期为准。 */
  phase: Period;
  editing: FeaturedCycleItem | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 下拉数据一次拉一页即可——运营配置级数据量。 */
const OPTION_PAGE_SIZE = 30;

export default function FeaturedCycleItemForm({ open, phase, editing, onClose, onSaved }: Props) {
  const toast = useToast();

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
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const [activities, setActivities] = useState<ActivityItem[]>([]);
  const [routes, setRoutes] = useState<RouteItem[]>([]);
  const [articles, setArticles] = useState<ArticleItem[]>([]);

  useEffect(() => {
    if (!open) return;
    void pageActivities({ size: OPTION_PAGE_SIZE })
      .then((p) => setActivities(p.content))
      .catch(() => undefined);
    void pageRoutes({ size: OPTION_PAGE_SIZE })
      .then((p) => setRoutes(p.content))
      .catch(() => undefined);
    void pageArticles({ size: OPTION_PAGE_SIZE })
      .then((p) => setArticles(p.content))
      .catch(() => undefined);
  }, [open]);

  // 打开弹窗时按编辑/新增初始化
  useEffect(() => {
    if (!open) return;
    setFieldErrors({});
    setType(editing?.type ?? "ACTIVITY");
    setBannerKey(editing?.banner?.id ?? "");
    setBannerPreview(editing?.banner?.url ?? "");
    setSortOrder(String(editing?.sortOrder ?? 0));
    setActivityId(editing?.activityId ?? "");
    setRouteId(editing?.routeId ?? "");
    setArticleId(editing?.articleId ?? "");
    setTitle(editing?.title ?? "");
    setSubtitle(editing?.subtitle ?? "");
    setDescription(editing?.description ?? "");
    setNote(editing?.note ?? "");
  }, [open, editing]);

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

  const modalTitle = useMemo(() => (editing ? "编辑周期推荐" : "新增周期推荐"), [editing]);

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

  const handleSubmit = async () => {
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
        phase: editing?.phase ?? phase,
        type,
        banner: bannerKey.trim(),
        sortOrder: Number(sortOrder) || 0,
        online: editing?.online ?? false,
        activityId: type === "ACTIVITY" ? activityId : null,
        routeId: type === "ROUTE" ? routeId : null,
        articleId: type === "ARTICLE" ? articleId : null,
        title: type === "ACTIVITY" ? null : title.trim(),
        subtitle: type === "ROUTE" ? subtitle.trim() : null,
        description: type === "ARTICLE" ? null : description.trim(),
        note: type === "ACTIVITY" ? note.trim() || null : null,
      };
      if (editing) await updateFeaturedCycleItem(editing.id, payload);
      else await createFeaturedCycleItem(payload);
      toast.success(editing ? "保存成功" : "创建成功");
      onSaved();
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

  const selectClass =
    "border rounded px-3 py-2 text-sm w-full h-11 disabled:bg-gray-100 disabled:text-gray-500";
  const textareaClass = "border rounded px-3 py-2 text-sm w-full min-h-[80px]";
  const inputClass = "border rounded px-3 py-2 text-sm w-full h-11";

  const error = (key: string) =>
    fieldErrors[key] ? <div className="text-error-500 text-xs mt-1">{fieldErrors[key]}</div> : null;

  return (
    <Modal
      isOpen={open}
      onClose={onClose}
      showBackdrop={false}
      className="max-w-[520px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
    >
      <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h2 className="text-lg font-semibold text-gray-800 dark:text-white/90 mb-5">{modalTitle}</h2>
        <div className="space-y-5">
        <div>
          <Label>
            内容类型 <span className="text-error-500">*</span>
          </Label>
          <select
            className={selectClass}
            value={type}
            disabled={Boolean(editing)}
            onChange={(e) => handleTypeChange(e.target.value as FeaturedCycleItemType)}
          >
            {CYCLE_ITEM_TYPES.map((t) => (
              <option key={t} value={t}>
                {CYCLE_ITEM_TYPE_LABELS[t]}
              </option>
            ))}
          </select>
          {editing && (
            <div className="text-xs text-gray-400 mt-1">
              周期推荐创建后所属周期与内容类型不可修改
            </div>
          )}
        </div>

        <div>
          <Label>
            banner 图片 <span className="text-error-500">*</span>
          </Label>
          <ImageUploader
            value={bannerKey}
            previewUrl={bannerPreview}
            onChange={setBannerKey}
            className="h-28 w-48"
          />
          {error("banner")}
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

        {type === "ACTIVITY" && (
          <>
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
          </>
        )}

        {type === "ROUTE" && (
          <>
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
          </>
        )}

        {type === "ARTICLE" && (
          <>
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
              <div className="text-xs text-gray-400 mt-1">选中文章后自动带出文章标题，可修改</div>
              {error("title")}
            </div>
          </>
        )}

        <div className="flex gap-3 pt-2">
          <Button size="sm" disabled={submitting} onClick={() => void handleSubmit()}>
            {submitting ? "提交中..." : editing ? "保存" : "创建"}
          </Button>
          <Button size="sm" variant="outline" disabled={submitting} onClick={onClose}>
            取消
          </Button>
        </div>
      </div>
      </div>
    </Modal>
  );
}
