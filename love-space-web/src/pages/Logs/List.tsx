import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import { OperationLogItem, OperationLogQuery, pageOperationLogs } from "../../api/logs";

const MODULE_OPTIONS = [
  { label: "用户", value: "user" },
  { label: "城市", value: "city" },
  { label: "分类", value: "category" },
  { label: "标签", value: "tag" },
  { label: "商家", value: "merchant" },
  { label: "认证", value: "auth" },
];

const FILTER_FIELDS: FilterField[] = [
  { name: "username", label: "操作人", type: "text", placeholder: "模糊匹配" },
  { name: "module", label: "模块", type: "select", options: MODULE_OPTIONS },
  { name: "createdAtFrom", label: "时间起", type: "date" },
  { name: "createdAtTo", label: "时间止", type: "date" },
];

function toIsoStart(date: string): string {
  return new Date(`${date}T00:00:00`).toISOString();
}

function toIsoEnd(date: string): string {
  return new Date(`${date}T23:59:59`).toISOString();
}

function buildQuery(filters: FilterValues, page: number, size: number): OperationLogQuery {
  const q: OperationLogQuery = { page, size };
  if (filters.username) q.username = filters.username;
  if (filters.module) q.module = filters.module;
  if (filters.createdAtFrom) q.createdAtFrom = toIsoStart(filters.createdAtFrom);
  if (filters.createdAtTo) q.createdAtTo = toIsoEnd(filters.createdAtTo);
  return q;
}

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

export default function LogList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<OperationLogItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await pageOperationLogs(buildQuery(filters, page, size));
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

  const handleApply = (values: FilterValues) => {
    setFilters(values);
    setPage(1);
  };

  const handleReset = () => {
    setFilters({});
    setPage(1);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">操作日志</h1>
      </div>

      <FilterBar
        fields={FILTER_FIELDS}
        initialValues={filters}
        onApply={handleApply}
        onReset={handleReset}
      />

      {error && <div className="text-error-500 text-sm mb-2">{error}</div>}

      <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-800 text-left text-gray-500">
            <tr>
              <th className="px-4 py-3">时间</th>
              <th className="px-4 py-3">操作人</th>
              <th className="px-4 py-3">模块</th>
              <th className="px-4 py-3">动作</th>
              <th className="px-4 py-3">对象</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-gray-500">
                  加载中...
                </td>
              </tr>
            )}
            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-gray-500">
                  暂无数据
                </td>
              </tr>
            )}
            {!loading &&
              items.map((it) => (
                <tr key={it.id} className="border-t border-gray-100 dark:border-gray-800">
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">
                    {formatDateTime(it.createdAt)}
                  </td>
                  <td className="px-4 py-3">{it.username}</td>
                  <td className="px-4 py-3">{it.module}</td>
                  <td className="px-4 py-3">{it.action}</td>
                  <td className="px-4 py-3">{it.target ?? "-"}</td>
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
        onChange={({ page: nextPage, size: nextSize }) => {
          setPage(nextPage);
          setSize(nextSize);
        }}
      />
    </div>
  );
}
