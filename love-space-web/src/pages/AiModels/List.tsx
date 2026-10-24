import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { AiScene, listAiScenes } from "../../api/ai";
import ModelConfigModal from "./ModelConfigModal";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "关键字", type: "text", placeholder: "搜索 scene_id / 名称" },
];

/** AI 管理 › 模型配置。 */
export default function AiModelList() {
  const toast = useToast();
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AiScene[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [configuring, setConfiguring] = useState<AiScene | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listAiScenes({ keyword: filters.keyword || undefined, page, size });
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

  const columns: Column<AiScene>[] = [
    { key: "sceneId", header: "scene_id", width: "12rem", className: "font-medium text-gray-800 dark:text-white/90" },
    { key: "name", header: "名称", width: "10rem" },
    {
      key: "primaryModel",
      header: "主模型",
      render: (it) => it.primaryModel ?? <span className="text-warning-600">未配置（⚠ 不可生成）</span>,
    },
    { key: "backupModel", header: "备用模型", render: (it) => it.backupModel ?? "—" },
    {
      key: "actions",
      header: "操作",
      width: "7rem",
      render: (it) => (
        <Button size="sm" variant="primary" type="button" onClick={() => setConfiguring(it)}>
          配置
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="模型配置 | Love Space Admin" description="AI 场景模型配置" />
      <div className="space-y-6">
        <ComponentCard title="AI 管理 › 模型配置">
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
          <DataTable columns={columns} rows={items} rowKey={(it) => it.id} loading={loading} emptyText="暂无场景" />
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
      <ModelConfigModal
        scene={configuring}
        onClose={() => setConfiguring(null)}
        onSaved={() => {
          setConfiguring(null);
          void load();
        }}
      />
    </>
  );
}
