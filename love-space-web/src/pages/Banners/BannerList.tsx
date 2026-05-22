import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import {
  BannerListItem,
  BannerQuery,
  BannerType,
  deleteBanner,
  listBanners,
  setBannerOnline,
} from "../../api/banners";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "名称", type: "text", placeholder: "模糊匹配" },
  {
    name: "type",
    label: "类型",
    type: "select",
    options: [{ label: "城市", value: "CITY" }],
  },
  {
    name: "online",
    label: "上下架",
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

function buildQuery(filters: FilterValues, page: number, size: number): BannerQuery {
  const q: BannerQuery = { page, size };
  if (filters.keyword) q.keyword = filters.keyword;
  if (filters.type) q.type = filters.type as BannerType;
  if (filters.online === "true") q.online = true;
  else if (filters.online === "false") q.online = false;
  return q;
}

export default function BannerList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [items, setItems] = useState<BannerListItem[]>([]);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listBanners(buildQuery(filters, page, size));
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      setError(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters, page, size]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleToggleOnline = async (item: BannerListItem) => {
    const next = !item.online;
    setItems((prev) => prev.map((it) => (it.id === item.id ? { ...it, online: next } : it)));
    try {
      await setBannerOnline(item.id, next);
    } catch (err) {
      setItems((prev) => prev.map((it) => (it.id === item.id ? { ...it, online: !next } : it)));
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (item: BannerListItem) => {
    if (!window.confirm(`确认删除 banner「${item.name}」？`)) return;
    try {
      await deleteBanner(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">Banner 管理</h1>
        <Link
          to="/banners/new"
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600"
        >
          新增 Banner
        </Link>
      </div>

      <FilterBar
        fields={FILTER_FIELDS}
        initialValues={filters}
        onApply={(v) => {
          setPage(1);
          setFilters(v);
        }}
        onReset={() => {
          setPage(1);
          setFilters({});
        }}
      />

      {error && <div className="text-error-500 text-sm mb-2">{error}</div>}

      <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-800 text-left text-gray-500">
            <tr>
              <th className="px-4 py-3">名称</th>
              <th className="px-4 py-3">类型</th>
              <th className="px-4 py-3">关联城市</th>
              <th className="px-4 py-3">上下架</th>
              <th className="px-4 py-3">更新时间</th>
              <th className="px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-gray-500">加载中...</td>
              </tr>
            )}
            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-gray-500">暂无数据</td>
              </tr>
            )}
            {!loading &&
              items.map((it) => (
                <tr key={it.id} className="border-t border-gray-100 dark:border-gray-800">
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">{it.name}</td>
                  <td className="px-4 py-3">{it.type}</td>
                  <td className="px-4 py-3">{it.linkedCityName ?? "-"}</td>
                  <td className="px-4 py-3">
                    <label className="inline-flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={it.online}
                        onChange={() => handleToggleOnline(it)}
                      />
                      <span className={it.online ? "text-success-500" : "text-gray-400"}>
                        {it.online ? "已上架" : "未上架"}
                      </span>
                    </label>
                  </td>
                  <td className="px-4 py-3">{formatDateTime(it.updatedAt)}</td>
                  <td className="px-4 py-3 space-x-2">
                    <Link
                      to={`/banners/${it.id}/edit`}
                      className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                    >
                      编辑
                    </Link>
                    <button
                      type="button"
                      onClick={() => handleDelete(it)}
                      className="px-3 py-1 text-xs rounded border border-error-300 text-error-500 hover:bg-error-50"
                    >
                      删除
                    </button>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={page}
        size={size}
        total={total}
        totalPages={totalPages}
        onChange={(n) => {
          setPage(n.page);
          setSize(n.size);
        }}
      />
    </div>
  );
}
