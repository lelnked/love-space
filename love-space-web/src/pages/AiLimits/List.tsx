import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { AiLimit, listAiLimits } from "../../api/ai";
import { formatDateTime } from "../AiPrompts/promptVariables";
import LimitEditModal from "./LimitEditModal";

/** AI 管理 › 限流配置。固定两行，无筛选与分页。 */
export default function AiLimitList() {
  const toast = useToast();
  const [items, setItems] = useState<AiLimit[]>([]);
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState<AiLimit | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setItems(await listAiLimits());
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const columns: Column<AiLimit>[] = [
    { key: "name", header: "名称", className: "font-medium text-gray-800 dark:text-white/90" },
    { key: "value", header: "上限", width: "10rem", render: (it) => (it.value === -1 ? "不限制" : `${it.value} 次`) },
    { key: "updatedBy", header: "最后修改人", width: "10rem", render: (it) => it.updatedBy ?? "—" },
    {
      key: "updatedAt",
      header: "修改时间",
      width: "12rem",
      render: (it) => formatDateTime(it.updatedAt),
    },
    {
      key: "actions",
      header: "操作",
      width: "7rem",
      render: (it) => (
        <Button size="sm" variant="primary" type="button" onClick={() => setEditing(it)}>
          编辑
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="限流配置 | Love Space Admin" description="AI 生成接口每日额度" />
      <div className="space-y-6">
        <ComponentCard title="AI 管理 › 限流配置">
          <DataTable columns={columns} rows={items} rowKey={(it) => it.key} loading={loading} emptyText="暂无配置" />
          <div className="text-xs text-gray-400">提示：-1 表示不限制，保存后立即生效</div>
        </ComponentCard>
      </div>
      <LimitEditModal
        limit={editing}
        onClose={() => setEditing(null)}
        onSaved={() => {
          setEditing(null);
          void load();
        }}
      />
    </>
  );
}
