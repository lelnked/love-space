import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import ImageUploader from "../../components/form/ImageUploader";
import Checkbox from "../../components/form/input/Checkbox";
import ArticleRichTextEditor from "../../components/form/ArticleRichTextEditor";
import {
  ArticleUpsertRequest,
  createArticle,
  getArticle,
  updateArticle,
} from "../../api/articles";
import { ArticleCategory, listArticleCategories } from "../../api/articleCategories";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

export default function ArticleForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [imageKey, setImageKey] = useState("");
  const [imagePreview, setImagePreview] = useState("");
  const [title, setTitle] = useState("");
  const [subtitle, setSubtitle] = useState("");
  const [sortOrder, setSortOrder] = useState("0");
  const [categoryIds, setCategoryIds] = useState<string[]>([]);
  const [contentHtml, setContentHtml] = useState("");

  const [categories, setCategories] = useState<ArticleCategory[]>([]);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    void listArticleCategories().then(setCategories).catch(() => undefined);
    if (!id) return;
    setLoading(true);
    getArticle(id)
      .then((d) => {
        setImageKey(d.image?.id ?? "");
        setImagePreview(d.image?.url ?? "");
        setTitle(d.title);
        setSubtitle(d.subtitle ?? "");
        setSortOrder(String(d.sortOrder));
        setCategoryIds(d.categoryIds);
        setContentHtml(d.contentHtml ?? "");
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const toggleCategory = (cid: string) =>
    setCategoryIds((prev) =>
      prev.includes(cid) ? prev.filter((x) => x !== cid) : [...prev, cid],
    );

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!imageKey.trim()) errs.image = "请上传文章图片";
    if (!title.trim()) errs.title = "文章标题不能为空";
    const sortValue = Number(sortOrder);
    if (!Number.isInteger(sortValue) || sortValue < 0) errs.sortOrder = "权重需为非负整数";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: ArticleUpsertRequest = {
      image: imageKey.trim(),
      title: title.trim(),
      subtitle: subtitle.trim() || null,
      contentHtml: contentHtml || null,
      sortOrder: sortValue,
      categoryIds,
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateArticle(id, payload);
      else await createArticle(payload);
      navigate("/articles", { replace: true });
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
        {editing ? "编辑文章" : "新增文章"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} noValidate className="max-w-4xl space-y-5">
          {/* 1. 基础信息 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>基础信息</legend>
            <div className="mb-4">
              <Label>
                文章图片 <span className="text-error-500">*</span>
              </Label>
              <ImageUploader
                value={imageKey}
                previewUrl={imagePreview}
                onChange={setImageKey}
                className="h-28 w-40"
              />
              {fieldErrors.image && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.image}</div>
              )}
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label>
                  标题 <span className="text-error-500">*</span>
                </Label>
                <Input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  error={Boolean(fieldErrors.title)}
                  hint={fieldErrors.title}
                />
              </div>
              <div>
                <Label>副标题</Label>
                <Input value={subtitle} onChange={(e) => setSubtitle(e.target.value)} />
              </div>
              <div>
                <Label>权重（数字越小越靠前）</Label>
                <Input
                  type="number"
                  value={sortOrder}
                  onChange={(e) => setSortOrder(e.target.value)}
                  error={Boolean(fieldErrors.sortOrder)}
                  hint={fieldErrors.sortOrder}
                />
              </div>
            </div>
          </fieldset>

          {/* 2. 关联栏目（多选） */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>关联栏目（多选）</legend>
            {categories.length === 0 ? (
              <div className="text-sm text-gray-500">暂无栏目，请先在「文章栏目」创建</div>
            ) : (
              <div className="flex flex-wrap gap-4">
                {categories.map((c) => (
                  <Checkbox
                    key={c.id}
                    label={c.name}
                    checked={categoryIds.includes(c.id)}
                    onChange={() => toggleCategory(c.id)}
                  />
                ))}
              </div>
            )}
            {fieldErrors.categoryIds && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.categoryIds}</div>
            )}
          </fieldset>

          {/* 3. 文章内容（富文本） */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>文章内容</legend>
            <ArticleRichTextEditor initialValue={contentHtml} onChange={setContentHtml} />
          </fieldset>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/articles")}
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
