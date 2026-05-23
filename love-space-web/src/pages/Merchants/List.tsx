import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Badge from "../../components/ui/badge/Badge";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
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
  const toast = useToast();
  const [cities, setCities] = useState<CityItem[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [scoreCache, setScoreCache] = useState<Record<string, number | null>>({});

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
    try {
      const data = await pageMerchants(buildQuery(filters, page, size));
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
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
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters, page, size, toast]);

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
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (it: MerchantItem) => {
    if (!window.confirm(`确认删除商户「${it.name}」？`)) return;
    try {
      await deleteMerchant(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <>
      <PageMeta title="商户管理 | Love Space Admin" description="商户列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/merchants/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增商户
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="商户列表">
          <FilterBar
            fields={filterFields}
            initialValues={filters}
            onApply={handleApply}
            onReset={handleReset}
          />

          <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
            <div className="max-w-full overflow-x-auto">
              <Table>
                <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                  <TableRow>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      名称
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      城市
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      分类
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      上架
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      权重
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      安全环境分
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
                  {!loading && items.length === 0 && (
                    <TableRow>
                      <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                        暂无数据
                      </TableCell>
                    </TableRow>
                  )}
                  {!loading &&
                    items.map((it) => (
                      <TableRow key={it.id}>
                        <TableCell className="px-5 py-4 sm:px-6 text-start font-medium text-gray-800 text-theme-sm dark:text-white/90">
                          {it.name}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {cityName[it.cityId] ?? "-"}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.categoryId ? categoryName[it.categoryId] ?? "-" : "-"}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <Badge size="sm" color={it.online ? "success" : "error"}>
                            {it.online ? "已上架" : "未上架"}
                          </Badge>
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.weight}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {scoreCache[it.id] === undefined
                            ? "-"
                            : scoreCache[it.id] === null
                              ? "-"
                              : scoreCache[it.id]}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {formatDateTime(it.createdAt)}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <div className="flex gap-2">
                            <Link to={`/merchants/${it.id}/edit`}>
                              <Button size="sm" variant="primary">编辑</Button>
                            </Link>
                            <Button size="sm" variant="primary" onClick={() => handleToggleOnline(it)}>
                              {it.online ? "下架" : "上架"}
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
    </>
  );
}
