import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { AiScene, listAiScenes } from "../../api/ai";
import { formatDateTime } from "./promptVariables";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "关键字", type: "text", placeholder: "搜索 scene_id / 名称" },
];

/** AI 管理 › 提示词管理（页面 1：按场景看当前生效模板）。 */
export default function AiPromptSceneList() {
  const toast = useToast();
  const navigate = useNavigate();
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AiScene[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

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
      key: "activeTemplate",
      header: "当前生效版本",
      render: (it) =>
        it.activeTemplate ? (
          `V${it.activeTemplate.seq} ${it.activeTemplate.name}`
        ) : (
          <span className="text-warning-600">未发布（⚠ 不可生成）</span>
        ),
    },
    { key: "publishedBy", header: "发布人", width: "8rem", render: (it) => it.activeTemplate?.publishedBy ?? "—" },
    {
      key: "publishedAt",
      header: "发布时间",
      width: "12rem",
      render: (it) => formatDateTime(it.activeTemplate?.publishedAt),
    },
    {
      key: "actions",
      header: "操作",
      width: "8rem",
      render: (it) => (
        <Button size="sm" variant="primary" type="button" onClick={() => navigate(`/ai/prompts/${it.sceneId}`)}>
          管理模板
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="提示词管理 | Love Space Admin" description="AI 提示词模板管理" />
      <div className="space-y-6">
        <ComponentCard title="AI 管理 › 提示词管理">
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
    </>
  );
}
