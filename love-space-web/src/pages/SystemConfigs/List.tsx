import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import Button from "../../components/ui/button/Button";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { SystemConfigItem, listSystemConfigs } from "../../api/systemConfigs";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "key / 名称", type: "text", placeholder: "模糊匹配" },
];

const PREVIEW_LIMIT = 40;

function formatDateTime(value: string | null): string {
  if (!value) return "—";
  return new Date(value).toLocaleString("zh-CN", { hour12: false });
}

export default function SystemConfigList() {
  const navigate = useNavigate();
  const toast = useToast();
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<SystemConfigItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listSystemConfigs({ keyword: filters.keyword || undefined, page, size });
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

  const columns: Column<SystemConfigItem>[] = [
    { key: "key", header: "key", width: "14rem", className: "font-medium text-gray-800 dark:text-white/90" },
    { key: "name", header: "名称", width: "12rem" },
    {
      key: "value",
      header: "value（预览）",
      render: (it) => {
        const text = JSON.stringify(it.value);
        return (
          <span title={text} className="font-mono text-xs">
            {text.length > PREVIEW_LIMIT ? `${text.slice(0, PREVIEW_LIMIT)}…` : text}
          </span>
        );
      },
    },
    { key: "updatedAt", header: "更新时间", width: "11rem", render: (it) => formatDateTime(it.updatedAt ?? it.createdAt) },
    {
      key: "actions",
      header: "操作",
      width: "7rem",
      render: (it) => (
        <Button size="sm" variant="primary" onClick={() => navigate(`/system-configs/${it.id}/edit`)}>
          编辑
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="系统配置 | Love Space Admin" description="系统配置预置项管理" />
      <div className="space-y-6">
        <ComponentCard title="系统配置">
          <FilterBar
            fields={FILTER_FIELDS}
            initialValues={filters}
            onApply={(values) => {
              setFilters(values);
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
