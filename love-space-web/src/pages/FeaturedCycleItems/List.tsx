import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Button from "../../components/ui/button/Button";
import Switch from "../../components/form/switch/Switch";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { PERIOD_LABEL, PERIOD_VALUES, type Period } from "../../api/types";
import {
  CYCLE_ITEM_TYPE_LABELS,
  FeaturedCycleItem,
  deleteFeaturedCycleItem,
  pageFeaturedCycleItems,
  setFeaturedCycleItemOnline,
} from "../../api/featuredCycleItems";
import FeaturedCycleItemForm from "./Form";

export default function FeaturedCycleItemList() {
  const [phase, setPhase] = useState<Period>("MENSTRUAL");
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<FeaturedCycleItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState("");
  // Switch 为非受控组件：切换失败后靠 nonce 变更 key 强制重挂载复位
  const [switchNonce, setSwitchNonce] = useState(0);
  const toast = useToast();
  const confirm = useConfirm();

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<FeaturedCycleItem | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError("");
    try {
      const data = await pageFeaturedCycleItems({ phase, page, size });
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      setLoadError(ax.response?.data?.detail ?? "加载失败");
      setItems([]);
    } finally {
      setLoading(false);
    }
  }, [phase, page, size]);

  useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setEditing(null);
    setModalOpen(true);
  };

  const openEdit = (it: FeaturedCycleItem) => {
    setEditing(it);
    setModalOpen(true);
  };

  const handleToggleOnline = async (it: FeaturedCycleItem, next: boolean) => {
    try {
      await setFeaturedCycleItemOnline(it.id, next);
      setItems((prev) => prev.map((a) => (a.id === it.id ? { ...a, online: next } : a)));
      toast.success(next ? "已上线" : "已下线");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
      setSwitchNonce((n) => n + 1); // 复位开关
    }
  };

  const handleDelete = async (it: FeaturedCycleItem) => {
    if (
      !(await confirm({
        title: "删除周期推荐",
        message: `确认删除「${PERIOD_LABEL[it.phase]}」下的这条${CYCLE_ITEM_TYPE_LABELS[it.type]}推荐？`,
        confirmText: "删除",
        danger: true,
      }))
    )
      return;
    try {
      await deleteFeaturedCycleItem(it.id);
      toast.success("已删除");
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "删除失败");
    }
  };

  const columns: Column<FeaturedCycleItem>[] = [
    {
      key: "banner",
      header: "banner",
      width: "7rem",
      render: (it) =>
        it.banner ? (
          <img src={it.banner.url} alt="banner" className="h-10 w-14 rounded object-cover" />
        ) : (
          "-"
        ),
    },
    {
      key: "type",
      header: "内容类型",
      width: "9rem",
      render: (it) => (
        <span className="rounded bg-gray-100 px-2 py-1 text-xs dark:bg-white/10">
          {CYCLE_ITEM_TYPE_LABELS[it.type]}
        </span>
      ),
    },
    {
      key: "title",
      header: "标题",
      // 活动类无独立标题，展示关联活动名
      render: (it) => it.title || it.relatedTitle || "-",
    },
    {
      key: "relatedTitle",
      header: "关联内容",
      width: "12rem",
      render: (it) =>
        it.relatedTitle ?? <span className="text-error-500 text-xs">已删除</span>,
    },
    {
      key: "sortOrder",
      header: "排序号",
      width: "6rem",
      render: (it) => it.sortOrder,
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
          <Button size="sm" variant="primary" onClick={() => openEdit(it)}>
            编辑
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
      <PageMeta title="周期推荐 | Love Space Admin" description="精选·你的周期活动推荐配置" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增周期推荐
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="周期推荐列表">
          <nav className="flex gap-2 border-b border-gray-200 dark:border-white/10">
            {PERIOD_VALUES.map((p) => (
              <button
                key={p}
                type="button"
                onClick={() => {
                  setPhase(p);
                  setPage(1);
                }}
                className={`-mb-px border-b-2 px-4 py-2 text-sm ${
                  phase === p
                    ? "border-brand-500 text-brand-500"
                    : "border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400"
                }`}
              >
                {PERIOD_LABEL[p]}
              </button>
            ))}
          </nav>

          {loadError ? (
            <div className="py-10 text-center">
              <div className="text-sm text-error-500">{loadError}</div>
              <Button size="sm" variant="outline" onClick={() => void load()} className="mt-3">
                重试
              </Button>
            </div>
          ) : (
            <>
              <DataTable
                columns={columns}
                rows={items}
                rowKey={(it) => it.id}
                loading={loading}
                emptyText="该周期暂无推荐"
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
            </>
          )}
        </ComponentCard>
      </div>

      <FeaturedCycleItemForm
        open={modalOpen}
        phase={phase}
        editing={editing}
        onClose={() => setModalOpen(false)}
        onSaved={() => {
          setModalOpen(false);
          void load();
        }}
      />
    </>
  );
}
