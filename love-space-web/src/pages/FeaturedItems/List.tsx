import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import ImageUploader from "../../components/form/ImageUploader";
import Switch from "../../components/form/switch/Switch";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  FeaturedItem,
  FeaturedItemQuery,
  createFeaturedItem,
  deleteFeaturedItem,
  pageFeaturedItems,
  setFeaturedItemOnline,
  updateFeaturedItem,
} from "../../api/featuredItems";
import { CityItem, listCities } from "../../api/cities";

interface FieldError {
  field: string;
  message: string;
}

export default function FeaturedItemList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<FeaturedItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState<CityItem[]>([]);
  // Switch 为非受控组件：切换失败后靠 nonce 变更 key 强制重挂载复位
  const [switchNonce, setSwitchNonce] = useState(0);
  const toast = useToast();
  const confirm = useConfirm();

  // 弹窗表单状态
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [cityId, setCityId] = useState("");
  const [bannerKey, setBannerKey] = useState("");
  const [bannerPreview, setBannerPreview] = useState("");
  const [description, setDescription] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
  }, []);

  const filterFields = useMemo<FilterField[]>(
    () => [
      {
        name: "cityId",
        label: "关联地图",
        type: "select",
        options: cities.map((c) => ({ label: c.chineseName, value: c.id })),
      },
    ],
    [cities],
  );

  const cityName = useMemo(
    () => Object.fromEntries(cities.map((c) => [c.id, c.chineseName])),
    [cities],
  );

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const q: FeaturedItemQuery = { page, size };
      if (filters.cityId) q.cityId = filters.cityId;
      const data = await pageFeaturedItems(q);
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters, page, size, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setEditingId(null);
    setCityId("");
    setBannerKey("");
    setBannerPreview("");
    setDescription("");
    setFieldErrors({});
    setModalOpen(true);
  };

  const openEdit = (it: FeaturedItem) => {
    setEditingId(it.id);
    setCityId(it.cityId);
    setBannerKey(it.banner?.id ?? "");
    setBannerPreview(it.banner?.url ?? "");
    setDescription(it.description ?? "");
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!cityId) errs.cityId = "请选择关联地图";
    if (!bannerKey.trim()) errs.banner = "请上传 banner 图片";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        cityId,
        banner: bannerKey.trim(),
        description: description.trim() || null,
      };
      if (editingId) await updateFeaturedItem(editingId, payload);
      else await createFeaturedItem(payload);
      setModalOpen(false);
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

  const handleToggleOnline = async (it: FeaturedItem, next: boolean) => {
    try {
      await setFeaturedItemOnline(it.id, next);
      setItems((prev) => prev.map((a) => (a.id === it.id ? { ...a, online: next } : a)));
      toast.success(next ? "已上线" : "已下线");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
      setSwitchNonce((n) => n + 1); // 复位开关
    }
  };

  const handleDelete = async (it: FeaturedItem) => {
    if (
      !(await confirm({
        title: "删除精选推荐",
        message: `确认删除关联「${cityName[it.cityId] ?? it.cityId}」的精选推荐？`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteFeaturedItem(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<FeaturedItem>[] = [
    {
      key: "banner",
      header: "banner",
      width: "7rem",
      render: (it) =>
        it.banner ? (
          <img
            src={it.banner.url}
            alt={cityName[it.cityId] ?? "banner"}
            className="h-10 w-14 rounded object-cover"
          />
        ) : (
          "-"
        ),
    },
    {
      key: "cityId",
      header: "关联地图",
      width: "10rem",
      render: (it) => cityName[it.cityId] ?? "-",
    },
    {
      key: "description",
      header: "说明",
      render: (it) => it.description || "-",
    },
    {
      key: "online",
      header: "状态",
      width: "8rem",
      render: (it) => (
        <Switch
          key={`${it.id}-${it.online}-${switchNonce}`}
          label={it.online ? "已上线" : "已下线"}
          defaultChecked={it.online}
          onChange={(checked) => void handleToggleOnline(it, checked)}
        />
      ),
    },
    {
      key: "actions",
      header: "操作",
      width: "11rem",
      render: (it) => (
        <div className="flex gap-2">
          <Button size="sm" variant="primary" onClick={() => openEdit(it)}>
            编辑
          </Button>
          <Button size="sm" variant="primary" onClick={() => handleDelete(it)}>
            删除
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="精选推荐 | Love Space Admin" description="精选·地图上新推荐列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增精选推荐
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="精选推荐列表">
          <FilterBar
            fields={filterFields}
            initialValues={filters}
            onApply={(v) => {
              setFilters(v);
              setPage(1);
            }}
            onReset={() => {
              setFilters({});
              setPage(1);
            }}
          />

          <DataTable columns={columns} rows={items} rowKey={(it) => it.id} loading={loading} />

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
      </div>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        showBackdrop={false}
        className="max-w-[520px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
      >
        <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
        <h2 className="text-lg font-semibold text-gray-800 dark:text-white/90 mb-5">
          {editingId ? "编辑精选推荐" : "新增精选推荐"}
        </h2>
        <div className="space-y-5">
          <div>
            <Label>
              关联地图 <span className="text-error-500">*</span>
            </Label>
            <select
              className="border rounded px-3 py-2 text-sm w-full h-11 disabled:bg-gray-100 disabled:text-gray-500"
              value={cityId}
              onChange={(e) => setCityId(e.target.value)}
              disabled={Boolean(editingId)}
            >
              <option value="">请选择</option>
              {cities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.chineseName}
                  {c.online ? "" : "（已下架）"}
                </option>
              ))}
            </select>
            {editingId && (
              <div className="text-xs text-gray-400 mt-1">精选推荐创建后关联地图不可修改</div>
            )}
            {fieldErrors.cityId && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.cityId}</div>
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
            {fieldErrors.banner && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.banner}</div>
            )}
          </div>
          <div>
            <Label>推荐说明</Label>
            <textarea
              placeholder="推荐说明（选填）"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="border rounded px-3 py-2 text-sm w-full min-h-[80px]"
            />
          </div>

          <div className="flex gap-3 pt-2">
            <Button size="sm" disabled={submitting} onClick={() => void handleSubmit()}>
              {submitting ? "提交中..." : editingId ? "保存" : "创建"}
            </Button>
            <Button
              size="sm"
              variant="outline"
              disabled={submitting}
              onClick={() => setModalOpen(false)}
            >
              取消
            </Button>
          </div>
        </div>
        </div>
      </Modal>
    </>
  );
}
