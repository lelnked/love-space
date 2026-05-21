import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import {
  disableManager,
  enableManager,
  pageManagers,
  resetPassword,
  ManagerItem,
  ManagerQuery,
} from "../../api/managers";

const FILTER_FIELDS: FilterField[] = [
  { name: "username", label: "用户名", type: "text", placeholder: "模糊匹配" },
  {
    name: "role",
    label: "角色",
    type: "select",
    options: [
      { label: "管理员", value: "ADMIN" },
      { label: "成员", value: "MEMBER" },
    ],
  },
  {
    name: "enable",
    label: "状态",
    type: "select",
    options: [
      { label: "启用", value: "true" },
      { label: "停用", value: "false" },
    ],
  },
  { name: "createdAtFrom", label: "创建时间起", type: "date" },
  { name: "createdAtTo", label: "创建时间止", type: "date" },
];

function toIsoStart(date: string): string {
  return new Date(`${date}T00:00:00`).toISOString();
}

function toIsoEnd(date: string): string {
  return new Date(`${date}T23:59:59`).toISOString();
}

function buildQuery(filters: FilterValues, page: number, size: number): ManagerQuery {
  const q: ManagerQuery = { page, size };
  if (filters.username) q.username = filters.username;
  if (filters.role === "ADMIN" || filters.role === "MEMBER") q.role = filters.role;
  if (filters.enable === "true") q.enable = true;
  else if (filters.enable === "false") q.enable = false;
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

export default function ManagerList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<ManagerItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await pageManagers(buildQuery(filters, page, size));
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

  const handleToggleEnable = async (item: ManagerItem) => {
    try {
      if (item.enable) await disableManager(item.id);
      else await enableManager(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleResetPassword = async (item: ManagerItem) => {
    const next = window.prompt(`为管理员 ${item.username} 输入新密码（≥8 位）：`);
    if (!next) return;
    if (next.length < 8) {
      alert("密码至少 8 位");
      return;
    }
    try {
      await resetPassword(item.id, { newPassword: next });
      alert("密码已重置");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "重置失败");
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">管理员管理</h1>
        <Link
          to="/managers/create"
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600"
        >
          新增管理员
        </Link>
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
              <th className="px-4 py-3">用户名</th>
              <th className="px-4 py-3">昵称</th>
              <th className="px-4 py-3">角色</th>
              <th className="px-4 py-3">状态</th>
              <th className="px-4 py-3">创建时间</th>
              <th className="px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-gray-500">
                  加载中...
                </td>
              </tr>
            )}
            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-gray-500">
                  暂无数据
                </td>
              </tr>
            )}
            {!loading &&
              items.map((it) => (
                <tr key={it.id} className="border-t border-gray-100 dark:border-gray-800">
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">{it.username}</td>
                  <td className="px-4 py-3">{it.nickname ?? "-"}</td>
                  <td className="px-4 py-3">{it.role === "ADMIN" ? "管理员" : "成员"}</td>
                  <td className="px-4 py-3">
                    <span
                      className={
                        it.enable
                          ? "text-success-500"
                          : "text-gray-400"
                      }
                    >
                      {it.enable ? "启用" : "停用"}
                    </span>
                  </td>
                  <td className="px-4 py-3">{formatDateTime(it.createdAt)}</td>
                  <td className="px-4 py-3 space-x-2">
                    <button
                      type="button"
                      onClick={() => handleToggleEnable(it)}
                      className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                    >
                      {it.enable ? "停用" : "启用"}
                    </button>
                    <button
                      type="button"
                      onClick={() => handleResetPassword(it)}
                      className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                    >
                      重置密码
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
        onChange={({ page: nextPage, size: nextSize }) => {
          setPage(nextPage);
          setSize(nextSize);
        }}
      />
    </div>
  );
}
