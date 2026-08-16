import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import Switch from "../../components/form/switch/Switch";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import {
  ActivityItem,
  ActivityQuery,
  deleteActivity,
  pageActivities,
  setActivityOnline,
} from "../../api/activities";
import { CityItem, listCities } from "../../api/cities";

function buildQuery(filters: FilterValues, page: number, size: number): ActivityQuery {
  const q: ActivityQuery = { page, size };
  if (filters.cityId) q.cityId = filters.cityId;
  if (filters.keyword) q.keyword = filters.keyword;
  return q;
}

export default function ActivityList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<ActivityItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState<CityItem[]>([]);
  // Switch 为非受控组件：切换失败后靠 nonce 变更 key 强制重挂载复位
  const [switchNonce, setSwitchNonce] = useState(0);
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
        label: "所属城市",
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
      const data = await pageActivities(buildQuery(filters, page, size));
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

  const handleToggleOnline = async (it: ActivityItem, next: boolean) => {
    try {
      await setActivityOnline(it.id, next);
      setItems((prev) => prev.map((a) => (a.id === it.id ? { ...a, online: next } : a)));
      toast.success(next ? "已上线" : "已下线");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
      setSwitchNonce((n) => n + 1); // 复位开关
    }
  };

  const handleDelete = async (it: ActivityItem) => {
    if (
      !(await confirm({
        title: "删除活动",
        message: `确认删除活动「${it.title}」？`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteActivity(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<ActivityItem>[] = [
    {
      key: "cover",
      header: "图片",
      width: "7rem",
      render: (it) =>
        it.cover ? (
          <img src={it.cover.url} alt={it.title} className="h-10 w-14 rounded object-cover" />
        ) : (
          "-"
        ),
    },
    {
      key: "title",
      header: "标题",
      width: "14rem",
      className: "font-medium text-gray-800 dark:text-white/90",
    },
    {
      key: "cityId",
      header: "所属城市",
      width: "9rem",
      render: (it) => cityName[it.cityId] ?? "-",
    },
    {
      key: "level",
      header: "级别",
      width: "6rem",
      render: (it) => it.level ?? "-",
    },
    {
      key: "online",
      header: "状态",
      width: "8rem",
      render: (it) => (
        <Switch
          key={`${it.id}-${it.online}-${switchNonce}`}
          label={it.online ? "已上线" : "已下线"}
          defaultChecked={it.online}
          onChange={(checked) => void handleToggleOnline(it, checked)}
        />
      ),
    },
    {
      key: "actions",
      header: "操作",
      width: "11rem",
      render: (it) => (
        <div className="flex gap-2">
          <Link to={`/activities/${it.id}/edit`}>
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
      <PageMeta title="活动管理 | Love Space Admin" description="活动列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <Link
          to="/activities/create"
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增活动
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title="活动列表">
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
