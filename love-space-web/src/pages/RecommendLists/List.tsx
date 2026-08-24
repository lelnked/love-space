import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useConfirm } from "../../context/ConfirmContext";
import Badge from "../../components/ui/badge/Badge";
import {
  deleteRecommendList,
  pageRecommendLists,
  RecommendListItem,
  RecommendListQuery,
} from "../../api/recommendLists";
import { CityItem, listCities } from "../../api/cities";

function buildQuery(filters: FilterValues, page: number, size: number): RecommendListQuery {
  const q: RecommendListQuery = { page, size };
  if (filters.cityId) q.cityId = filters.cityId;
  if (filters.keyword) q.keyword = filters.keyword;
  return q;
}

export default function RecommendListList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<RecommendListItem[]>([]);
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
      { name: "keyword", label: "标题", type: "text", placeholder: "模糊匹配" },
      {
        name: "cityId",
        label: "所属地图",
        type: "select",
        options: cities.map((c) => ({ label: c.chineseName, value: c.id })),
      },
    ],
    [cities],
  );

  const cityName = useMemo(
    () => Object.fromEntries(cities.map((c) => [c.id, c.chineseName])),
    [cities],
  );

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageRecommendLists(buildQuery(filters, page, size));
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

  const handleDelete = async (it: RecommendListItem) => {
    if (
      !(await confirm({
        title: "删除清单",
        message: `确认删除推荐清单「${it.title}」？\n清单内的商户关联将一并删除，商户本身不受影响。`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteRecommendList(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<RecommendListItem>[] = [
    {
      key: "title",
      header: "标题",
      width: "12rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "cityId",
      header: "所属地图",
      width: "8rem",
      render: (it) => cityName[it.cityId] ?? "-",
    },
    { key: "sortOrder", header: "排序号", width: "6rem" },
    { key: "merchantCount", header: "商户数", width: "6rem" },
    {
      key: "status",
      header: "状态",
      width: "6rem",
      render: (it) => (
        <Badge size="sm" color={it.status === "ONLINE" ? "success" : "error"}>
          {it.status === "ONLINE" ? "已上架" : "已下架"}
        </Badge>
      ),
    },
    {
      key: "actions",
      header: "操作",
      width: "10rem",
      render: (it) => (
        <div className="flex gap-2">
          <Link to={`/recommend-lists/${it.id}/edit`}>
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
      <PageMeta title="推荐清单 | Love Space Admin" description="推荐清单列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/recommend-lists/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增清单
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="清单列表">
          <FilterBar
            fields={filterFields}
            initialValues={filters}
            onApply={handleApply}
            onReset={handleReset}
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
