import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import {
  CityItem,
  CityQuery,
  deleteCity,
  listCities,
  setCityOnline,
} from "../../api/cities";

const FILTER_FIELDS: FilterField[] = [
  { name: "name", label: "中文名", type: "text", placeholder: "模糊匹配" },
  {
    name: "online",
    label: "上架状态",
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

function buildQuery(filters: FilterValues): CityQuery {
  const q: CityQuery = {};
  if (filters.name) q.name = filters.name;
  if (filters.online === "true") q.online = true;
  else if (filters.online === "false") q.online = false;
  return q;
}

export default function CityList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [items, setItems] = useState<CityItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listCities(buildQuery(filters));
      setItems(data);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      setError(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleToggleOnline = async (item: CityItem) => {
    try {
      await setCityOnline(item.id, !item.online);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (item: CityItem) => {
    if (!window.confirm(`确认删除城市「${item.chineseName}」？`)) return;
    try {
      await deleteCity(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">城市管理</h1>
        <Link
          to="/cities/create"
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600"
        >
          新增城市
        </Link>
      </div>

      <FilterBar
        fields={FILTER_FIELDS}
        initialValues={filters}
        onApply={(v) => setFilters(v)}
        onReset={() => setFilters({})}
      />

      {error && <div className="text-error-500 text-sm mb-2">{error}</div>}

      <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-800 text-left text-gray-500">
            <tr>
              <th className="px-4 py-3">中文名</th>
              <th className="px-4 py-3">英文名</th>
              <th className="px-4 py-3">中文省份</th>
              <th className="px-4 py-3">上架</th>
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
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">{it.chineseName}</td>
                  <td className="px-4 py-3">{it.englishName}</td>
                  <td className="px-4 py-3">{it.chineseProvince}</td>
                  <td className="px-4 py-3">
                    <span className={it.online ? "text-success-500" : "text-gray-400"}>
                      {it.online ? "已上架" : "未上架"}
                    </span>
                  </td>
                  <td className="px-4 py-3">{formatDateTime(it.createdAt)}</td>
                  <td className="px-4 py-3 space-x-2">
                    <Link
                      to={`/cities/${it.id}/edit`}
                      className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                    >
                      编辑
                    </Link>
                    <button
                      type="button"
                      onClick={() => handleToggleOnline(it)}
                      className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                    >
                      {it.online ? "下架" : "上架"}
                    </button>
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
    </div>
  );
}
