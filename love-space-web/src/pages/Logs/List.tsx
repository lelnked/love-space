import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import { useToast } from "../../context/ToastContext";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
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
  const toast = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageOperationLogs(buildQuery(filters, page, size));
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

  const handleApply = (values: FilterValues) => {
    setFilters(values);
    setPage(1);
  };

  const handleReset = () => {
    setFilters({});
    setPage(1);
  };

  return (
    <>
      <PageMeta title="操作日志 | Love Space Admin" description="后台操作日志" />
      <div className="space-y-6">
        <ComponentCard title="操作日志">
          <FilterBar
            fields={FILTER_FIELDS}
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
                      时间
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      操作人
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      模块
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      动作
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      对象
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
                          {formatDateTime(it.createdAt)}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.username}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.module}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.action}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.target ?? "-"}
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
