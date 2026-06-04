import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import { useToast } from "../../context/ToastContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  createTag,
  deleteTag,
  listTags,
  setTagOnline,
  TagItem,
  TagQuery,
  updateTag,
} from "../../api/tags";

const FILTER_FIELDS: FilterField[] = [
  { name: "name", label: "名称", type: "text", placeholder: "模糊匹配" },
  {
    name: "online",
    label: "状态",
    type: "select",
    options: [
      { label: "已上架", value: "true" },
      { label: "未上架", value: "false" },
    ],
  },
];

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

function buildQuery(filters: FilterValues): TagQuery {
  const q: TagQuery = {};
  if (filters.name) q.name = filters.name;
  if (filters.online === "true") q.online = true;
  else if (filters.online === "false") q.online = false;
  return q;
}

/** code-point 长度（emoji / 中文均按 1 计）。 */
function codePointLength(s: string): number {
  return Array.from(s).length;
}

export default function TagList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [items, setItems] = useState<TagItem[]>([]);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const [newName, setNewName] = useState("");
  const [creating, setCreating] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingName, setEditingName] = useState("");
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

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listTags(buildQuery(filters));
      setItems(data);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleCreate = async () => {
    const name = newName.trim();
    if (!name) {
      toast.warning("请输入标签名称");
      return;
    }
    if (codePointLength(name) > 6) {
      toast.warning("标签名称最多 6 个字符");
      return;
    }
    setCreating(true);
    try {
      await createTag({ name });
      setNewName("");
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ??"创建失败");
    } finally {
      setCreating(false);
    }
  };

  const startEdit = (it: TagItem) => {
    setEditingId(it.id);
    setEditingName(it.name);
  };
  const cancelEdit = () => {
    setEditingId(null);
    setEditingName("");
  };
  const saveEdit = async (it: TagItem) => {
    const name = editingName.trim();
    if (!name) return;
    if (codePointLength(name) > 6) {
      toast.warning("标签名称最多 6 个字符");
      return;
    }
    try {
      await updateTag(it.id, { name });
      cancelEdit();
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ??"保存失败");
    }
  };

  const handleToggleOnline = async (it: TagItem) => {
    const next = !it.online;
    try {
      await setTagOnline(it.id, next);
      // 乐观更新：仅改本行，避免整表 reload 抖动
      setItems((prev) => prev.map((t) => (t.id === it.id ? { ...t, online: next } : t)));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ??"操作失败");
    }
  };

  const handleDelete = async (it: TagItem) => {
    if (!window.confirm(`确认删除标签「${it.name}」？`)) return;
    try {
      await deleteTag(it.id);
      // 局部移除该行，无需整表 reload
      setItems((prev) => prev.filter((t) => t.id !== it.id));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ??"删除失败");
    }
  };

  const btnClass =
    "px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600 disabled:opacity-50";

  const columns: Column<TagItem>[] = [
    {
      key: "name",
      header: "名称",
      width: "16rem",
      className: "text-gray-800 dark:text-white/90",
      render: (it) =>
        editingId === it.id ? (
          <input
            type="text"
            value={editingName}
            onChange={(e) => setEditingName(e.target.value)}
            className="border rounded px-2 py-1 text-sm"
          />
        ) : (
          it.name
        ),
    },
    {
      key: "online",
      header: "上架",
      width: "8rem",
      render: (it) => (
        <span className={it.online ? "text-success-500" : "text-gray-400"}>
          {it.online ? "已上架" : "未上架"}
        </span>
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
      width: "16rem",
      render: (it) =>
        editingId === it.id ? (
          <div className="flex gap-2">
            <button type="button" onClick={() => saveEdit(it)} className={btnClass}>
              保存
            </button>
            <button type="button" onClick={cancelEdit} className={btnClass}>
              取消
            </button>
          </div>
        ) : (
          <div className="flex gap-2">
            <button type="button" onClick={() => handleToggleOnline(it)} className={btnClass}>
              {it.online ? "下架" : "上架"}
            </button>
            <button type="button" onClick={() => startEdit(it)} className={btnClass}>
              编辑
            </button>
            <button type="button" onClick={() => handleDelete(it)} className={btnClass}>
              删除
            </button>
          </div>
        ),
    },
  ];

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">标签管理</h1>

      <div className="flex items-center gap-2 mb-4">
        <input
          type="text"
          placeholder="新增标签名称（≤6 字符）"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          className="border rounded px-3 py-2 text-sm min-w-[240px]"
        />
        <button
          type="button"
          onClick={handleCreate}
          disabled={creating}
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600 disabled:opacity-50"
        >
          {creating ? "提交中..." : "新增"}
        </button>
      </div>

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
    </div>
  );
}
