import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import {
  deleteMerchant,
  getMerchant,
  MerchantItem,
  MerchantQuery,
  pageMerchants,
  setMerchantOnline,
} from "../../api/merchants";
import { CityItem, listCities } from "../../api/cities";
import { CategoryItem, listCategories } from "../../api/categories";
import { PERIOD_LABEL, PERIOD_VALUES, Period } from "../../api/types";

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

function buildQuery(filters: FilterValues, page: number, size: number): MerchantQuery {
  const q: MerchantQuery = { page, size };
  if (filters.name) q.name = filters.name;
  if (filters.cityId) q.cityId = filters.cityId;
  if (filters.categoryId) q.categoryId = filters.categoryId;
  if (filters.period) q.period = filters.period as Period;
  if (filters.online === "true") q.online = true;
  else if (filters.online === "false") q.online = false;
  return q;
}

export default function MerchantList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<MerchantItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [cities, setCities] = useState<CityItem[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [scoreCache, setScoreCache] = useState<Record<string, number | null>>({});

  // 预加载城市 / 分类（用于筛选项 + 列表展示）
  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
    void listCategories().then(setCategories).catch(() => undefined);
  }, []);

  const filterFields = useMemo<FilterField[]>(
    () => [
      { name: "name", label: "名称", type: "text", placeholder: "模糊匹配" },
      {
        name: "cityId",
        label: "城市",
        type: "select",
        options: cities.map((c) => ({ label: c.chineseName, value: c.id })),
      },
      {
        name: "categoryId",
        label: "分类",
        type: "select",
        options: categories.map((c) => ({ label: c.name, value: c.id })),
      },
      {
        name: "period",
        label: "推荐周期",
        type: "select",
        options: PERIOD_VALUES.map((p) => ({ label: PERIOD_LABEL[p], value: p })),
      },
      {
        name: "online",
        label: "上架",
        type: "select",
        options: [
          { label: "已上架", value: "true" },
          { label: "未上架", value: "false" },
        ],
      },
    ],
    [cities, categories],
  );

  const cityName = useMemo(
    () => Object.fromEntries(cities.map((c) => [c.id, c.chineseName])),
    [cities],
  );
  const categoryName = useMemo(
    () => Object.fromEntries(categories.map((c) => [c.id, c.name])),
    [categories],
  );

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await pageMerchants(buildQuery(filters, page, size));
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
      // 拉取每行的安全环境分（列表项不带，调用 detail）
      setScoreCache({});
      const next: Record<string, number | null> = {};
      await Promise.all(
        data.content.map(async (m) => {
          try {
            const d = await getMerchant(m.id);
            next[m.id] = d.safetyEnvironmentScore;
          } catch {
            next[m.id] = null;
          }
        }),
      );
      setScoreCache(next);
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

  const handleToggleOnline = async (it: MerchantItem) => {
    try {
      await setMerchantOnline(it.id, !it.online);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (it: MerchantItem) => {
    if (!window.confirm(`确认删除商户「${it.name}」？`)) return;
    try {
      await deleteMerchant(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90">商户管理</h1>
        <Link
          to="/merchants/create"
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600"
        >
          新增商户
        </Link>
      </div>

      <FilterBar
        fields={filterFields}
        initialValues={filters}
        onApply={handleApply}
        onReset={handleReset}
      />

      {error && <div className="text-error-500 text-sm mb-2">{error}</div>}

      <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-800 text-left text-gray-500">
            <tr>
              <th className="px-4 py-3">名称</th>
              <th className="px-4 py-3">城市</th>
              <th className="px-4 py-3">分类</th>
              <th className="px-4 py-3">上架</th>
              <th className="px-4 py-3">权重</th>
              <th className="px-4 py-3">安全环境分</th>
              <th className="px-4 py-3">创建时间</th>
              <th className="px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={8} className="px-4 py-6 text-center text-gray-500">
                  加载中...
                </td>
              </tr>
            )}
            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={8} className="px-4 py-6 text-center text-gray-500">
                  暂无数据
                </td>
              </tr>
            )}
            {!loading &&
              items.map((it) => (
                <tr key={it.id} className="border-t border-gray-100 dark:border-gray-800">
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">{it.name}</td>
                  <td className="px-4 py-3">{cityName[it.cityId] ?? "-"}</td>
                  <td className="px-4 py-3">
                    {it.categoryId ? categoryName[it.categoryId] ?? "-" : "-"}
                  </td>
                  <td className="px-4 py-3">
                    <span className={it.online ? "text-success-500" : "text-gray-400"}>
                      {it.online ? "已上架" : "未上架"}
                    </span>
                  </td>
                  <td className="px-4 py-3">{it.weight}</td>
                  <td className="px-4 py-3">
                    {scoreCache[it.id] === undefined
                      ? "-"
                      : scoreCache[it.id] === null
                        ? "-"
                        : scoreCache[it.id]}
                  </td>
                  <td className="px-4 py-3">{formatDateTime(it.createdAt)}</td>
                  <td className="px-4 py-3 space-x-2">
                    <Link
                      to={`/merchants/${it.id}/edit`}
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
