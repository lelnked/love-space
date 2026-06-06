import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Switch from "../../components/form/switch/Switch";
import Badge from "../../components/ui/badge/Badge";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  CategoryItem,
  createCategory,
  deleteCategory,
  listCategories,
  updateCategory,
} from "../../api/categories";

interface FieldError {
  field: string;
  message: string;
}

/** 分类名称最大长度（code-point，中文/emoji 均按 1 计），与后端 codePointCount ≤ 10 对齐。 */
const MAX_NAME_CODE_POINTS = 10;

/** code-point 长度（emoji / 中文均按 1 计）。 */
function codePointLength(s: string): number {
  return Array.from(s).length;
}

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

export default function CategoryList() {
  const [items, setItems] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const confirm = useConfirm();
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);

  const total = items.length;
  const totalPages = Math.max(1, Math.ceil(total / size));
  const pagedItems = useMemo(
    () => items.slice((page - 1) * size, page * size),
    [items, page, size],
  );

  useEffect(() => {
    if (page > totalPages) setPage(totalPages);
  }, [page, totalPages]);

  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "edit">("create");
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formName, setFormName] = useState("");
  const [formSortOrder, setFormSortOrder] = useState("0");
  const [formOnline, setFormOnline] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formFieldErrors, setFormFieldErrors] = useState<Record<string, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listCategories();
      const sorted = [...data].sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
      setItems(sorted);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setModalMode("create");
    setEditingId(null);
    setFormName("");
    setFormSortOrder("0");
    setFormOnline(false);
    setFormFieldErrors({});
    setModalOpen(true);
  };

  const openEdit = (it: CategoryItem) => {
    setModalMode("edit");
    setEditingId(it.id);
    setFormName(it.name);
    setFormSortOrder(String(it.sortOrder));
    setFormOnline(it.online);
    setFormFieldErrors({});
    setModalOpen(true);
  };

  const closeModal = () => {
    if (submitting) return;
    setModalOpen(false);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFormFieldErrors({});
    const name = formName.trim();
    if (!name) {
      setFormFieldErrors({ name: "名称不能为空" });
      return;
    }
    if (codePointLength(name) > MAX_NAME_CODE_POINTS) {
      setFormFieldErrors({ name: `名称最多 ${MAX_NAME_CODE_POINTS} 个字符` });
      return;
    }
    const sortOrder = Number(formSortOrder);
    if (!Number.isInteger(sortOrder) || sortOrder < 0) {
      setFormFieldErrors({ sortOrder: "排序值需为不小于 0 的整数" });
      return;
    }
    setSubmitting(true);
    try {
      const payload = { name, sortOrder, online: formOnline };
      if (modalMode === "edit") {
        if (!editingId) throw new Error("缺少目标分类");
        await updateCategory(editingId, payload);
      } else {
        await createCategory(payload);
      }
      setModalOpen(false);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string; errors?: FieldError[] }>;
      const data = ax.response?.data;
      if (data?.errors?.length) {
        const map: Record<string, string> = {};
        for (const fe of data.errors) map[fe.field] = fe.message;
        setFormFieldErrors(map);
      }
      toast.error(data?.detail ?? (modalMode === "edit" ? "保存失败" : "创建失败"));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (it: CategoryItem) => {
    if (
      !(await confirm({
        title: "删除分类",
        message: `确认删除分类「${it.name}」？\n注意：删除会下架该分类下所有商户。`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteCategory(it.id);
      // 局部移除该行，无需整表 reload
      setItems((prev) => prev.filter((c) => c.id !== it.id));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<CategoryItem>[] = [
    {
      key: "name",
      header: "名称",
      width: "16rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "sortOrder",
      header: "排序值",
      width: "7rem",
      render: (it) => it.sortOrder,
    },
    {
      key: "online",
      header: "上架",
      width: "7rem",
      render: (it) => (
        <Badge size="sm" color={it.online ? "success" : "error"}>
          {it.online ? "已上架" : "未上架"}
        </Badge>
      ),
    },
    {
      key: "createdAt",
      header: "创建时间",
      width: "13rem",
      render: (it) => formatDateTime(it.createdAt),
    },
    {
      key: "actions",
      header: "操作",
      width: "12rem",
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
      <PageMeta title="分类管理 | Love Space Admin" description="分类列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增分类
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="分类列表">
          <DataTable
            columns={columns}
            rows={pagedItems}
            rowKey={(it) => it.id}
            loading={loading}
          />

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
        onClose={closeModal}
        showBackdrop={false}
        className="max-w-[520px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
      >
        <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
          <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
            {modalMode === "edit" ? "编辑分类" : "新增分类"}
          </h4>
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <Label>
                名称 <span className="text-error-500">*</span>
              </Label>
              <Input
                placeholder={`分类名称（≤${MAX_NAME_CODE_POINTS} 字符）`}
                value={formName}
                onChange={(e) =>
                  setFormName(Array.from(e.target.value).slice(0, MAX_NAME_CODE_POINTS).join(""))
                }
                error={Boolean(formFieldErrors.name)}
                hint={formFieldErrors.name}
              />
            </div>

            <div>
              <Label>排序值</Label>
              <Input
                type="number"
                min="0"
                step={1}
                placeholder="数值越小越靠前（默认 0）"
                value={formSortOrder}
                onChange={(e) => setFormSortOrder(e.target.value)}
                error={Boolean(formFieldErrors.sortOrder)}
                hint={formFieldErrors.sortOrder}
              />
            </div>

            <div>
              <Label>上架</Label>
              <Switch
                label={formOnline ? "已上架" : "未上架"}
                defaultChecked={formOnline}
                onChange={setFormOnline}
              />
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
                {submitting ? "提交中..." : modalMode === "edit" ? "保存" : "创建"}
              </Button>
            </div>
          </form>
        </div>
      </Modal>
    </>
  );
}
