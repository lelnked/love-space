import { FormEvent, useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import ImageUploader from "../../components/form/ImageUploader";
import Switch from "../../components/form/switch/Switch";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  AmbassadorItem,
  AmbassadorQuery,
  createAmbassador,
  deleteAmbassador,
  pageAmbassadors,
  setAmbassadorOnline,
  updateAmbassador,
} from "../../api/ambassadors";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "名称", type: "text", placeholder: "模糊匹配" },
];

interface FieldError {
  field: string;
  message: string;
}

export default function AmbassadorList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AmbassadorItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  // Switch 为非受控组件：切换失败后靠 nonce 变更 key 强制重挂载复位
  const [switchNonce, setSwitchNonce] = useState(0);
  const toast = useToast();
  const confirm = useConfirm();

  // 弹窗表单状态
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [name, setName] = useState("");
  const [avatarKey, setAvatarKey] = useState("");
  const [avatarPreview, setAvatarPreview] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [weight, setWeight] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const q: AmbassadorQuery = { page, size };
      if (filters.keyword) q.keyword = filters.keyword;
      const data = await pageAmbassadors(q);
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
    setName("");
    setAvatarKey("");
    setAvatarPreview("");
    setTags([]);
    setWeight(0);
    setFieldErrors({});
    setModalOpen(true);
  };

  const openEdit = (it: AmbassadorItem) => {
    setEditingId(it.id);
    setName(it.name);
    setAvatarKey(it.avatar?.id ?? "");
    setAvatarPreview(it.avatar?.url ?? "");
    setTags(it.tags);
    setWeight(it.weight);
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const cleanTags = tags.map((t) => t.trim()).filter(Boolean);
    const errs: Record<string, string> = {};
    if (!name.trim()) errs.name = "名称不能为空";
    if (!avatarKey.trim()) errs.avatar = "请上传头像";
    if (cleanTags.length > 3) errs.tags = "标签最多 3 条";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    setSubmitting(true);
    try {
      const payload = { avatar: avatarKey.trim(), name: name.trim(), tags: cleanTags, weight };
      if (editingId) await updateAmbassador(editingId, payload);
      else await createAmbassador(payload);
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

  const handleToggleOnline = async (it: AmbassadorItem, next: boolean) => {
    try {
      await setAmbassadorOnline(it.id, next);
      setItems((prev) => prev.map((a) => (a.id === it.id ? { ...a, online: next } : a)));
      toast.success(next ? "已上线" : "已下线");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
      setSwitchNonce((n) => n + 1); // 复位开关
    }
  };

  const handleDelete = async (it: AmbassadorItem) => {
    if (
      !(await confirm({
        title: "删除大使",
        message: `确认删除爱女大使「${it.name}」？`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteAmbassador(it.id);
      await load();
    } catch (err) {
      // 被路线引用时后端返回 400 中文提示，直接展示
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<AmbassadorItem>[] = [
    {
      key: "avatar",
      header: "头像",
      width: "6rem",
      render: (it) =>
        it.avatar ? (
          <img src={it.avatar.url} alt={it.name} className="h-10 w-10 rounded-full object-cover" />
        ) : (
          "-"
        ),
    },
    {
      key: "name",
      header: "名称",
      width: "12rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "tags",
      header: "标签",
      render: (it) =>
        it.tags.length > 0 ? (
          <div className="flex flex-wrap gap-1">
            {it.tags.map((t) => (
              <span
                key={t}
                className="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-600 dark:bg-gray-800 dark:text-gray-300"
              >
                {t}
              </span>
            ))}
          </div>
        ) : (
          "-"
        ),
    },
    { key: "weight", header: "权重", width: "6rem" },
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
      <PageMeta title="爱女大使 | Love Space Admin" description="爱女大使列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增大使
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="大使列表">
          <FilterBar
            fields={FILTER_FIELDS}
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
          <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
            {editingId ? "编辑大使" : "新增大使"}
          </h4>
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <Label>
                头像 <span className="text-error-500">*</span>
              </Label>
              <ImageUploader
                value={avatarKey}
                previewUrl={avatarPreview}
                onChange={setAvatarKey}
                className="h-28 w-28"
              />
              {fieldErrors.avatar && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.avatar}</div>
              )}
            </div>
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
              <Label>标签（最多 3 条）</Label>
              <div className="space-y-2">
                {tags.map((t, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <Input
                      value={t}
                      onChange={(e) =>
                        setTags((prev) => prev.map((x, j) => (j === i ? e.target.value : x)))
                      }
                    />
                    <Button
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() => setTags((prev) => prev.filter((_, j) => j !== i))}
                    >
                      删除
                    </Button>
                  </div>
                ))}
                {tags.length < 3 && (
                  <Button
                    type="button"
                    size="sm"
                    variant="outline"
                    onClick={() => setTags((prev) => [...prev, ""])}
                  >
                    添加标签
                  </Button>
                )}
              </div>
              {fieldErrors.tags && (
                <div className="text-error-500 text-xs mt-1">{fieldErrors.tags}</div>
              )}
            </div>
            <div>
              <Label>权重</Label>
              <Input
                type="number"
                value={String(weight)}
                onChange={(e) => setWeight(Number(e.target.value))}
                error={Boolean(fieldErrors.weight)}
                hint={fieldErrors.weight}
              />
            </div>

            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={() => setModalOpen(false)}
                disabled={submitting}
                className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
              >
                取消
              </button>
              <Button size="sm" disabled={submitting} type="submit">
                {submitting ? "提交中..." : editingId ? "保存" : "创建"}
              </Button>
            </div>
          </form>
        </div>
      </Modal>
    </>
  );
}
