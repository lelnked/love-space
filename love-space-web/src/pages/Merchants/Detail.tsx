import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Badge from "../../components/ui/badge/Badge";
import Button from "../../components/ui/button/Button";
import { Modal } from "../../components/ui/modal";
import Pagination from "../../components/pagination/Pagination";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import { useToast } from "../../context/ToastContext";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
import { getMerchant, MerchantDetail } from "../../api/merchants";
import { CityItem, listCities } from "../../api/cities";
import { CategoryItem, listCategories } from "../../api/categories";
import { listTags, TagItem } from "../../api/tags";
import { PERIOD_LABEL, Period } from "../../api/types";
import {
  createMerchantReview,
  deleteMerchantReview,
  pageMerchantReviews,
  MerchantReviewItem,
  MerchantReviewUpsertRequest,
  updateMerchantReview,
} from "../../api/merchantReviews";

interface FieldError {
  field: string;
  message: string;
}

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

type Tab = "info" | "reviews";

export default function MerchantDetailPage() {
  const { id } = useParams<{ id: string }>();
  const toast = useToast();
  const [tab, setTab] = useState<Tab>("info");

  const [detail, setDetail] = useState<MerchantDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState<CityItem[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [tags, setTags] = useState<TagItem[]>([]);

  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
    void listCategories().then(setCategories).catch(() => undefined);
    void listTags().then(setTags).catch(() => undefined);
  }, []);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getMerchant(id)
      .then(setDetail)
      .catch((err: AxiosError<{ detail?: string }>) =>
        toast.error(err.response?.data?.detail ?? "加载失败"),
      )
      .finally(() => setLoading(false));
  }, [id, toast]);

  const cityName = useMemo(
    () => Object.fromEntries(cities.map((c) => [c.id, c.chineseName])),
    [cities],
  );
  const categoryName = useMemo(
    () => Object.fromEntries(categories.map((c) => [c.id, c.name])),
    [categories],
  );
  const tagName = useMemo(
    () => Object.fromEntries(tags.map((t) => [t.id, t.name])),
    [tags],
  );

  const tabButtonClass = (t: Tab) =>
    tab === t
      ? "shadow-theme-xs text-gray-900 dark:text-white bg-white dark:bg-gray-800"
      : "text-gray-500 dark:text-gray-400";

  return (
    <>
      <PageMeta title="商户详情 | Love Space Admin" description="商户详情与评价管理" />
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">
          {detail ? detail.name : "商户详情"}
        </h1>
        <Link
          to="/merchants"
          className="px-4 py-2 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
        >
          返回列表
        </Link>
      </div>

      <div className="mb-6 flex items-center gap-0.5 rounded-lg bg-gray-100 p-0.5 dark:bg-gray-900 w-fit">
        <button
          type="button"
          onClick={() => setTab("info")}
          className={`px-4 py-2 font-medium rounded-md text-theme-sm hover:text-gray-900 dark:hover:text-white ${tabButtonClass("info")}`}
        >
          商户详情
        </button>
        <button
          type="button"
          onClick={() => setTab("reviews")}
          className={`px-4 py-2 font-medium rounded-md text-theme-sm hover:text-gray-900 dark:hover:text-white ${tabButtonClass("reviews")}`}
        >
          评价管理
        </button>
      </div>

      {tab === "info" && (
        <InfoTab
          loading={loading}
          detail={detail}
          cityName={cityName}
          categoryName={categoryName}
          tagName={tagName}
        />
      )}
      {tab === "reviews" && id && <ReviewsTab merchantId={id} />}
    </>
  );
}

function InfoRow({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex gap-4 py-2 border-b border-gray-100 dark:border-white/[0.05]">
      <div className="w-32 shrink-0 text-gray-500 text-theme-sm dark:text-gray-400">{label}</div>
      <div className="text-gray-800 text-theme-sm dark:text-white/90">{children}</div>
    </div>
  );
}

function InfoTab({
  loading,
  detail,
  cityName,
  categoryName,
  tagName,
}: {
  loading: boolean;
  detail: MerchantDetail | null;
  cityName: Record<string, string>;
  categoryName: Record<string, string>;
  tagName: Record<string, string>;
}) {
  if (loading) return <ComponentCard title="基础信息"><div className="text-gray-500">加载中...</div></ComponentCard>;
  if (!detail) return <ComponentCard title="基础信息"><div className="text-gray-500">暂无数据</div></ComponentCard>;

  return (
    <div className="space-y-6">
      <ComponentCard title="基础信息">
        <div className="flex items-center justify-between mb-2">
          <Badge size="sm" color={detail.online ? "success" : "error"}>
            {detail.online ? "已上架" : "未上架"}
          </Badge>
          <Link to={`/merchants/${detail.id}/edit`}>
            <Button size="sm" variant="primary">编辑商户</Button>
          </Link>
        </div>
        <InfoRow label="名称">{detail.name}</InfoRow>
        <InfoRow label="LOGO">
          {detail.logo ? (
            <img src={detail.logo.url} alt="logo" className="h-16 object-cover rounded border" />
          ) : (
            "-"
          )}
        </InfoRow>
        <InfoRow label="地址">{detail.address}</InfoRow>
        <InfoRow label="经纬度">
          {detail.longitude ?? "-"} , {detail.latitude ?? "-"}
        </InfoRow>
        <InfoRow label="城市">{cityName[detail.cityId] ?? "-"}</InfoRow>
        <InfoRow label="分类">
          {detail.categoryId ? categoryName[detail.categoryId] ?? "-" : "-"}
        </InfoRow>
        <InfoRow label="推荐周期">
          {detail.periods.length
            ? detail.periods.map((p: Period) => PERIOD_LABEL[p]).join("、")
            : "-"}
        </InfoRow>
        <InfoRow label="标签">
          {detail.tagIds.length ? (
            <div className="flex flex-wrap gap-1">
              {detail.tagIds.map((t) => (
                <Badge key={t} size="sm" color="info">
                  {tagName[t] ?? t}
                </Badge>
              ))}
            </div>
          ) : (
            "-"
          )}
        </InfoRow>
        <InfoRow label="权重">{detail.weight}</InfoRow>
      </ComponentCard>

      <ComponentCard title="四维评分">
        <InfoRow label="安全环境分">{detail.safetyEnvironmentScore}</InfoRow>
        <InfoRow label="经营权益分">{detail.businessRightsScore}</InfoRow>
        <InfoRow label="体验友好分">{detail.experienceFriendlyScore}</InfoRow>
        <InfoRow label="社会贡献分">{detail.socialContributionScore}</InfoRow>
      </ComponentCard>

      <ComponentCard title="商户故事">
        <p className="text-gray-800 text-theme-sm dark:text-white/90 whitespace-pre-wrap">
          {detail.story || "-"}
        </p>
      </ComponentCard>

      {detail.images.length > 0 && (
        <ComponentCard title="图片">
          <div className="flex flex-wrap gap-3">
            {detail.images.map((im) => (
              <img
                key={im.id}
                src={im.url}
                alt="商户图片"
                className="h-24 w-24 object-cover rounded border"
              />
            ))}
          </div>
        </ComponentCard>
      )}
    </div>
  );
}

const EMPTY_FORM: MerchantReviewUpsertRequest = {
  nickname: "",
  title: "",
  content: "",
  sortOrder: 0,
  recommended: false,
};

function ReviewsTab({ merchantId }: { merchantId: string }) {
  const toast = useToast();
  const [items, setItems] = useState<MerchantReviewItem[]>([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [open, setOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<MerchantReviewUpsertRequest>(EMPTY_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageMerchantReviews(merchantId, page, size);
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [merchantId, page, size, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFieldErrors({});
    setOpen(true);
  };

  const openEdit = (it: MerchantReviewItem) => {
    setEditingId(it.id);
    setForm({
      nickname: it.nickname,
      title: it.title,
      content: it.content,
      sortOrder: it.sortOrder,
      recommended: it.recommended,
    });
    setFieldErrors({});
    setOpen(true);
  };

  const closeModal = () => {
    if (submitting) return;
    setOpen(false);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!form.nickname.trim()) errs.nickname = "昵称不能为空";
    if (!form.title.trim()) errs.title = "标题不能为空";
    if (!form.content.trim()) errs.content = "内容不能为空";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: MerchantReviewUpsertRequest = {
      nickname: form.nickname.trim(),
      title: form.title.trim(),
      content: form.content.trim(),
      sortOrder: Number(form.sortOrder) || 0,
      recommended: form.recommended,
    };

    setSubmitting(true);
    try {
      if (editingId) await updateMerchantReview(merchantId, editingId, payload);
      else await createMerchantReview(merchantId, payload);
      setOpen(false);
      await load();
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

  const handleDelete = async (it: MerchantReviewItem) => {
    if (!window.confirm(`确认删除评价「${it.title}」？`)) return;
    try {
      await deleteMerchantReview(merchantId, it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <>
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增评价
        </button>
      </div>

      <ComponentCard title="评价列表">
        <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
          <div className="max-w-full overflow-x-auto">
            <Table>
              <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                <TableRow>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    昵称
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    标题
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    内容
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    权重
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    推荐
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    创建时间
                  </TableCell>
                  <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                    操作
                  </TableCell>
                </TableRow>
              </TableHeader>

              <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                {loading && (
                  <TableRow>
                    <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                      加载中...
                    </TableCell>
                  </TableRow>
                )}
                {!loading && items.length === 0 && (
                  <TableRow>
                    <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                      暂无数据
                    </TableCell>
                  </TableRow>
                )}
                {!loading &&
                  items.map((it) => (
                    <TableRow key={it.id}>
                      <TableCell className="px-5 py-4 sm:px-6 text-start font-medium text-gray-800 text-theme-sm dark:text-white/90">
                        {it.nickname}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                        {it.title}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400 max-w-xs truncate">
                        {it.content}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                        {it.sortOrder}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-start text-theme-sm">
                        <Badge size="sm" color={it.recommended ? "success" : "light"}>
                          {it.recommended ? "推荐" : "普通"}
                        </Badge>
                      </TableCell>
                      <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                        {formatDateTime(it.createdAt)}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-start text-theme-sm">
                        <div className="flex gap-2">
                          <Button size="sm" variant="primary" onClick={() => openEdit(it)}>
                            编辑
                          </Button>
                          <Button size="sm" variant="primary" onClick={() => handleDelete(it)}>
                            删除
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </div>
        </div>

        <Pagination
          page={page}
          size={size}
          total={total}
          totalPages={totalPages}
          onChange={({ page: nextPage, size: nextSize }) => {
            setPage(nextPage);
            setSize(nextSize);
          }}
        />
      </ComponentCard>

      <Modal
        isOpen={open}
        onClose={closeModal}
        showBackdrop={false}
        className="max-w-[560px] m-4 -translate-y-[60px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
      >
        <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
          <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
            {editingId ? "编辑评价" : "新增评价"}
          </h4>
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <Label>
                昵称 <span className="text-error-500">*</span>
              </Label>
              <Input
                value={form.nickname}
                onChange={(e) => setForm((f) => ({ ...f, nickname: e.target.value }))}
                error={Boolean(fieldErrors.nickname)}
                hint={fieldErrors.nickname}
              />
            </div>
            <div>
              <Label>
                标题 <span className="text-error-500">*</span>
              </Label>
              <Input
                value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                error={Boolean(fieldErrors.title)}
                hint={fieldErrors.title}
              />
            </div>
            <div>
              <Label>
                内容 <span className="text-error-500">*</span>
              </Label>
              <textarea
                value={form.content}
                onChange={(e) => setForm((f) => ({ ...f, content: e.target.value }))}
                className="border rounded px-3 py-2 text-sm w-full min-h-[120px]"
              />
              {fieldErrors.content && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.content}</div>
              )}
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label>权重</Label>
                <Input
                  type="number"
                  value={String(form.sortOrder)}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, sortOrder: Number(e.target.value) }))
                  }
                />
              </div>
              <div className="flex items-end">
                <label className="inline-flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
                  <input
                    type="checkbox"
                    checked={form.recommended}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, recommended: e.target.checked }))
                    }
                  />
                  推荐
                </label>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={closeModal}
                disabled={submitting}
                className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
              >
                取消
              </button>
              <Button size="sm" disabled={submitting}>
                {submitting ? "提交中..." : editingId ? "保存" : "创建"}
              </Button>
            </div>
          </form>
        </div>
      </Modal>
    </>
  );
}
