import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import Switch from "../../components/form/switch/Switch";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  ArticleItem,
  ArticleQuery,
  deleteArticle,
  pageArticles,
  setArticleOnline,
} from "../../api/articles";
import { ArticleCategory, listArticleCategories } from "../../api/articleCategories";

function buildQuery(filters: FilterValues, page: number, size: number): ArticleQuery {
  const q: ArticleQuery = { page, size };
  if (filters.categoryId) q.categoryId = filters.categoryId;
  if (filters.keyword) q.keyword = filters.keyword;
  return q;
}

export default function ArticleList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<ArticleItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<ArticleCategory[]>([]);
  // Switch 为非受控组件：切换失败后靠 nonce 变更 key 强制重挂载复位
  const [switchNonce, setSwitchNonce] = useState(0);
  const toast = useToast();
  const confirm = useConfirm();

  useEffect(() => {
    void listArticleCategories().then(setCategories).catch(() => undefined);
  }, []);

  const filterFields = useMemo<FilterField[]>(
    () => [
      { name: "keyword", label: "标题", type: "text", placeholder: "模糊匹配" },
      {
        name: "categoryId",
        label: "关联栏目",
        type: "select",
        options: categories.map((c) => ({ label: c.name, value: c.id })),
      },
    ],
    [categories],
  );

  const categoryName = useMemo(
    () => Object.fromEntries(categories.map((c) => [c.id, c.name])),
    [categories],
  );

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageArticles(buildQuery(filters, page, size));
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

  const handleToggleOnline = async (it: ArticleItem, next: boolean) => {
    try {
      await setArticleOnline(it.id, next);
      setItems((prev) => prev.map((a) => (a.id === it.id ? { ...a, online: next } : a)));
      toast.success(next ? "已上线" : "已下线");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
      setSwitchNonce((n) => n + 1); // 复位开关
    }
  };

  const handleDelete = async (it: ArticleItem) => {
    if (
      !(await confirm({
        title: "删除文章",
        message: `确认删除文章「${it.title}」？`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteArticle(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<ArticleItem>[] = [
    {
      key: "image",
      header: "图片",
      width: "7rem",
      render: (it) =>
        it.image ? (
          <img src={it.image.url} alt={it.title} className="h-10 w-14 rounded object-cover" />
        ) : (
          "-"
        ),
    },
    {
      key: "title",
      header: "标题",
      width: "14rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "categoryIds",
      header: "关联栏目",
      render: (it) =>
        it.categoryIds.length > 0 ? (
          <div className="flex flex-wrap gap-1">
            {it.categoryIds.map((cid) => (
              <span
                key={cid}
                className="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-600 dark:bg-gray-800 dark:text-gray-300"
              >
                {categoryName[cid] ?? "已删除栏目"}
              </span>
            ))}
          </div>
        ) : (
          "-"
        ),
    },
    {
      key: "sortOrder",
      header: "权重",
      width: "6rem",
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
          <Link to={`/articles/${it.id}/edit`}>
            <Button size="sm" variant="primary">编辑</Button>
          </Link>
          <Button size="sm" variant="primary" onClick={() => handleDelete(it)}>
            删除
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="文章管理 | Love Space Admin" description="文章列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/articles/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增文章
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="文章列表">
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
    </>
  );
}
