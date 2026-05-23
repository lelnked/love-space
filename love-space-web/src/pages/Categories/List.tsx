import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
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
    setFormFieldErrors({});
    setModalOpen(true);
  };

  const openEdit = (it: CategoryItem) => {
    setModalMode("edit");
    setEditingId(it.id);
    setFormName(it.name);
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
    setSubmitting(true);
    try {
      if (modalMode === "edit") {
        if (!editingId) throw new Error("缺少目标分类");
        await updateCategory(editingId, { name });
      } else {
        await createCategory({ name });
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
    if (!window.confirm(`确认删除分类「${it.name}」？\n注意：删除会下架该分类下所有商户。`)) return;
    try {
      await deleteCategory(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

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
          <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
            <div className="max-w-full overflow-x-auto">
              <Table>
                <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                  <TableRow>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      名称
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
                  {!loading && pagedItems.length === 0 && (
                    <TableRow>
                      <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                        暂无数据
                      </TableCell>
                    </TableRow>
                  )}
                  {!loading &&
                    pagedItems.map((it) => (
                      <TableRow key={it.id}>
                        <TableCell className="px-5 py-4 sm:px-6 text-start font-medium text-gray-800 text-theme-sm dark:text-white/90">
                          {it.name}
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
                placeholder="分类名称（≤30 字符）"
                value={formName}
                onChange={(e) => setFormName(e.target.value.slice(0, 30))}
                error={Boolean(formFieldErrors.name)}
                hint={formFieldErrors.name}
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
