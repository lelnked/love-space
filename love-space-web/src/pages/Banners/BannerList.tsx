import { useCallback, useEffect, useState } from "react";
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
  BannerListItem,
  BannerQuery,
  BannerType,
  deleteBanner,
  pageBanners,
  setBannerOnline,
} from "../../api/banners";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "名称", type: "text", placeholder: "模糊匹配" },
  { name: "positionCode", label: "位置标识", type: "text", placeholder: "模糊匹配" },
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
  if (filters.positionCode) q.positionCode = filters.positionCode;
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
  const toast = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageBanners(buildQuery(filters, page, size));
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

  const handleToggleOnline = async (item: BannerListItem) => {
    const next = !item.online;
    setItems((prev) => prev.map((it) => (it.id === item.id ? { ...it, online: next } : it)));
    try {
      await setBannerOnline(item.id, next);
    } catch (err) {
      setItems((prev) => prev.map((it) => (it.id === item.id ? { ...it, online: !next } : it)));
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (item: BannerListItem) => {
    if (!window.confirm(`确认删除 banner「${item.name}」？`)) return;
    try {
      await deleteBanner(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <>
      <PageMeta title="Banner管理 | Love Space Admin" description="Banner 列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/banners/new"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增 Banner
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="Banner 列表">
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

          <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
            <div className="max-w-full overflow-x-auto">
              <Table>
                <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                  <TableRow>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      名称
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      位置标识
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      类型
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      关联城市
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      上下架
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      更新时间
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
                          {it.positionCode}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.type}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.linkedCityName ?? "-"}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <Badge size="sm" color={it.online ? "success" : "error"}>
                            {it.online ? "已上架" : "未上架"}
                          </Badge>
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {formatDateTime(it.updatedAt)}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <div className="flex gap-2">
                            <Link to={`/banners/${it.id}/edit`}>
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
            onChange={(n) => {
              setPage(n.page);
              setSize(n.size);
            }}
          />
        </ComponentCard>
      </div>
    </>
  );
}
