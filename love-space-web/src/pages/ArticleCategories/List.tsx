import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import ImageUploader from "../../components/form/ImageUploader";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  ArticleCategory,
  createArticleCategory,
  deleteArticleCategory,
  listArticleCategories,
  updateArticleCategory,
} from "../../api/articleCategories";

interface FieldError {
  field: string;
  message: string;
}

export default function ArticleCategoryList() {
  const [items, setItems] = useState<ArticleCategory[]>([]);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const confirm = useConfirm();

  // 弹窗表单状态
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [name, setName] = useState("");
  const [iconKey, setIconKey] = useState("");
  const [iconPreview, setIconPreview] = useState("");
  const [sortOrder, setSortOrder] = useState("0");
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setItems(await listArticleCategories());
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
    setEditingId(null);
    setName("");
    setIconKey("");
    setIconPreview("");
    setSortOrder("0");
    setFieldErrors({});
    setModalOpen(true);
  };

  const openEdit = (it: ArticleCategory) => {
    setEditingId(it.id);
    setName(it.name);
    setIconKey(it.icon?.id ?? "");
    setIconPreview(it.icon?.url ?? "");
    setSortOrder(String(it.sortOrder));
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!name.trim()) errs.name = "栏目名称不能为空";
    if (!iconKey.trim()) errs.icon = "请上传栏目 icon";
    const sortValue = Number(sortOrder);
    if (!Number.isInteger(sortValue) || sortValue < 0) errs.sortOrder = "权重需为非负整数";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    setSubmitting(true);
    try {
      const payload = { name: name.trim(), icon: iconKey.trim(), sortOrder: sortValue };
      if (editingId) await updateArticleCategory(editingId, payload);
      else await createArticleCategory(payload);
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

  const handleDelete = async (it: ArticleCategory) => {
    if (
      !(await confirm({
        title: "删除栏目",
        message: `确认删除栏目「${it.name}」？删除后文章数据保留，仅解除与该栏目的关联。`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteArticleCategory(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<ArticleCategory>[] = [
    {
      key: "icon",
      header: "icon",
      width: "6rem",
      render: (it) =>
        it.icon ? (
          <img src={it.icon.url} alt={it.name} className="h-10 w-10 rounded object-cover" />
        ) : (
          "-"
        ),
    },
    {
      key: "name",
      header: "名称",
      width: "14rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "sortOrder",
      header: "权重",
      width: "6rem",
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
      <PageMeta title="文章栏目 | Love Space Admin" description="文章栏目列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增栏目
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="栏目列表">
          <DataTable columns={columns} rows={items} rowKey={(it) => it.id} loading={loading} />
        </ComponentCard>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} className="max-w-lg m-4 p-6">
        <h2 className="text-lg font-semibold text-gray-800 dark:text-white/90 mb-5">
          {editingId ? "编辑栏目" : "新增栏目"}
        </h2>
        <div className="space-y-5">
          <div>
            <Label>
              icon <span className="text-error-500">*</span>
            </Label>
            <ImageUploader
              value={iconKey}
              previewUrl={iconPreview}
              onChange={setIconKey}
              className="h-28 w-28"
            />
            {fieldErrors.icon && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.icon}</div>
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
            <Label>权重（数字越小越靠前）</Label>
            <Input
              type="number"
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
              error={Boolean(fieldErrors.sortOrder)}
              hint={fieldErrors.sortOrder}
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
      </Modal>
    </>
  );
}
