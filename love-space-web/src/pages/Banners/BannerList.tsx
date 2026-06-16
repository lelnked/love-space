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
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useConfirm } from "../../context/ConfirmContext";
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
  const confirm = useConfirm();

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
    if (!(await confirm({ title: "删除 Banner", message: `确认删除 banner「${item.name}」？`, confirmText: "删除", danger: true }))) return;
    try {
      await deleteBanner(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<BannerListItem>[] = [
    {
      key: "name",
      header: "名称",
      width: "12rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    { key: "positionCode", header: "位置标识" },
    {
      key: "type",
      header: "类型",
      width: "8rem",
      render: (it) => (it.type === "CITY" ? "城市" : it.type),
    },
    {
      key: "linkedCityName",
      header: "关联城市",
      render: (it) => it.linkedCityName ?? "-",
    },
    {
      key: "sortOrder",
      header: "排序",
      width: "6rem",
      render: (it) => it.sortOrder,
    },
    {
      key: "online",
      header: "上下架",
      width: "8rem",
      render: (it) => (
        <Badge size="sm" color={it.online ? "success" : "error"}>
          {it.online ? "已上架" : "未上架"}
        </Badge>
      ),
    },
    {
      key: "updatedAt",
      header: "更新时间",
      width: "13rem",
      render: (it) => formatDateTime(it.updatedAt),
    },
    {
      key: "actions",
      header: "操作",
      width: "16rem",
      render: (it) => (
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
      ),
    },
  ];

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

          <DataTable
            columns={columns}
            rows={items}
            rowKey={(it) => it.id}
            loading={loading}
          />

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
