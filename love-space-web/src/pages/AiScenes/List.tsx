import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Badge from "../../components/ui/badge/Badge";
import Button from "../../components/ui/button/Button";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import { AiScene, listAiScenes, setAiSceneEnabled } from "../../api/ai";
import { formatDateTime } from "../AiPrompts/promptVariables";
import SceneFormModal from "./SceneFormModal";

const FILTER_FIELDS: FilterField[] = [
  { name: "keyword", label: "关键字", type: "text", placeholder: "搜索 scene_id / 名称" },
  {
    name: "enabled",
    label: "状态",
    type: "select",
    options: [
      { label: "启用", value: "true" },
      { label: "禁用", value: "false" },
    ],
  },
];

/** AI 管理 › 业务配置。 */
export default function AiSceneList() {
  const toast = useToast();
  const confirm = useConfirm();
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AiScene[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listAiScenes({
        keyword: filters.keyword || undefined,
        enabled: filters.enabled ? filters.enabled === "true" : undefined,
        page,
        size,
      });
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

  const closeModal = useCallback(() => setModalOpen(false), []);

  const openCreate = () => {
    setEditingId(null);
    setModalOpen(true);
  };

  const openEdit = (it: AiScene) => {
    setEditingId(it.id);
    setModalOpen(true);
  };

  const toggleEnabled = async (it: AiScene) => {
    const next = !it.enabled;
    const ok = await confirm({
      title: next ? "启用场景" : "禁用场景",
      message: next ? `启用后 App 可按 ${it.sceneId} 生成内容。` : `禁用后 App 生成接口将拒绝 ${it.sceneId}。`,
      danger: !next,
    });
    if (!ok) return;
    try {
      await setAiSceneEnabled(it.id, next);
      toast.success(next ? "已启用" : "已禁用");
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const columns: Column<AiScene>[] = [
    { key: "sceneId", header: "scene_id", width: "12rem", className: "font-medium text-gray-800 dark:text-white/90" },
    { key: "name", header: "名称", width: "10rem" },
    {
      key: "description",
      header: "说明",
      render: (it) => (
        <span className="block max-w-[24rem] truncate" title={it.description ?? ""}>
          {it.description || "—"}
        </span>
      ),
    },
    {
      key: "enabled",
      header: "状态",
      width: "7rem",
      render: (it) => (
        <Badge size="sm" color={it.enabled ? "success" : "light"}>
          {it.enabled ? "●启用" : "○禁用"}
        </Badge>
      ),
    },
    { key: "createdAt", header: "创建时间", width: "12rem", render: (it) => formatDateTime(it.createdAt) },
    {
      key: "actions",
      header: "操作",
      width: "10rem",
      render: (it) => (
        <div className="flex gap-2">
          <Button size="sm" variant="outline" type="button" onClick={() => openEdit(it)}>
            编辑
          </Button>
          <Button size="sm" variant="primary" type="button" onClick={() => void toggleEnabled(it)}>
            {it.enabled ? "禁用" : "启用"}
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="业务配置 | Love Space Admin" description="AI 业务场景管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          + 新增场景
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="AI 管理 › 业务配置">
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
      <SceneFormModal
        open={modalOpen}
        editingId={editingId}
        onClose={closeModal}
        onSaved={() => {
          setModalOpen(false);
          void load();
        }}
      />
    </>
  );
}
