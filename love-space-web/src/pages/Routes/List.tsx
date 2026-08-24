import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { deleteRoute, pageRoutes, RouteItem, RouteQuery } from "../../api/routes";
import { CityItem, listCities } from "../../api/cities";

function buildQuery(filters: FilterValues, page: number, size: number): RouteQuery {
  const q: RouteQuery = { page, size };
  if (filters.keyword) q.keyword = filters.keyword;
  return q;
}

export default function RouteList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<RouteItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState<CityItem[]>([]);
  const toast = useToast();
  const confirm = useConfirm();

  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
  }, []);

  const filterFields = useMemo<FilterField[]>(
    () => [
      { name: "keyword", label: "主标题", type: "text", placeholder: "模糊匹配" },
    ],
    [],
  );

  const cityName = useMemo(
    () => Object.fromEntries(cities.map((c) => [c.id, c.chineseName])),
    [cities],
  );

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageRoutes(buildQuery(filters, page, size));
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

  const handleDelete = async (it: RouteItem) => {
    if (
      !(await confirm({
        title: "删除路线",
        message: `确认删除路线「${it.title}」？\n路线下的地点信息将一并删除。`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteRoute(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<RouteItem>[] = [
    {
      key: "thumbnail",
      header: "缩略图",
      width: "7rem",
      render: (it) => (
        <img src={it.thumbnail.url} alt={it.title} className="h-10 w-14 rounded object-cover" />
      ),
    },
    {
      key: "title",
      header: "主标题",
      width: "14rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "cityId",
      header: "所属地图",
      width: "9rem",
      render: (it) => cityName[it.cityId] ?? "-",
    },
    { key: "ambassadorName", header: "大使", width: "9rem" },
    { key: "sortOrder", header: "排序号", width: "6rem" },
    {
      key: "actions",
      header: "操作",
      width: "11rem",
      render: (it) => (
        <div className="flex gap-2">
          <Link to={`/routes/${it.id}/edit`}>
            <Button size="sm" variant="primary">编辑</Button>
          </Link>
          <Button size="sm" variant="primary" onClick={() => handleDelete(it)}>
            删除
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="路线管理 | Love Space Admin" description="路线列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/routes/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增路线
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="路线列表">
          <FilterBar
            fields={filterFields}
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

          <DataTable columns={columns} rows={items} rowKey={(it) => it.id} loading={loading} />

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
