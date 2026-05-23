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

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listCities(buildQuery(filters));
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

  const handleToggleOnline = async (item: CityItem) => {
    try {
      await setCityOnline(item.id, !item.online);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (item: CityItem) => {
    if (!window.confirm(`确认删除城市「${item.chineseName}」？`)) return;
    try {
      await deleteCity(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <>
      <PageMeta title="城市管理 | Love Space Admin" description="城市列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/cities/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增城市
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="城市列表">
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

          <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
            <div className="max-w-full overflow-x-auto">
              <Table>
                <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                  <TableRow>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      中文名
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      英文名
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      中文省份
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      上架
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
                          {it.chineseName}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.englishName}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.chineseProvince}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <Badge size="sm" color={it.online ? "success" : "error"}>
                            {it.online ? "已上架" : "未上架"}
                          </Badge>
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {formatDateTime(it.createdAt)}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <div className="flex gap-2">
                            <Link to={`/cities/${it.id}/edit`}>
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
