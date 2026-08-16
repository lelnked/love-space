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
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
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
  const confirm = useConfirm();
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
    const next = !item.online;
    // 下架会级联下架该城市下全部商户，先确认
    if (
      !next &&
      !(await confirm({
        title: "下架地图",
        message: `确认下架地图「${item.chineseName}」？\n注意：下架会同时下架该城市下的全部商户和 Banner，该城市的推荐清单、路线、活动也将在 App 端不可见。`,
        confirmText: "下架",
        danger: true,
      }))
    )
      return;
    try {
      await setCityOnline(item.id, next);
      // 乐观更新：仅改本行，避免整表 reload 抖动
      setItems((prev) =>
        prev.map((c) => (c.id === item.id ? { ...c, online: next } : c)),
      );
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleDelete = async (item: CityItem) => {
    if (!(await confirm({ title: "删除地图", message: `确认删除地图「${item.chineseName}」？`, confirmText: "删除", danger: true }))) return;
    try {
      await deleteCity(item.id);
      // 局部移除该行，无需整表 reload
      setItems((prev) => prev.filter((c) => c.id !== item.id));
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<CityItem>[] = [
    {
      key: "chineseName",
      header: "中文名",
      width: "12rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    { key: "englishName", header: "英文名" },
    { key: "chineseProvince", header: "中文省份" },
    {
      key: "online",
      header: "上架",
      width: "7rem",
      render: (it) => (
        <Badge size="sm" color={it.online ? "success" : "error"}>
          {it.online ? "已上架" : "未上架"}
        </Badge>
      ),
    },
    {
      key: "createdAt",
      header: "创建时间",
      width: "13rem",
      render: (it) => formatDateTime(it.createdAt),
    },
    {
      key: "actions",
      header: "操作",
      width: "16rem",
      render: (it) => (
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
      ),
    },
  ];

  return (
    <>
      <PageMeta title="地图管理 | Love Space Admin" description="地图（城市）列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/cities/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增地图
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="地图列表">
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

          <DataTable
            columns={columns}
            rows={pagedItems}
            rowKey={(it) => it.id}
            loading={loading}
          />

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
