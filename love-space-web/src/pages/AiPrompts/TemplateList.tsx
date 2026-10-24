import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Badge from "../../components/ui/badge/Badge";
import Button from "../../components/ui/button/Button";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import {
  AiPromptTemplateItem,
  AiScene,
  findAiSceneBySceneId,
  getAiPromptTemplate,
  listAiPromptTemplates,
  publishAiPromptTemplate,
} from "../../api/ai";
import { formatDateTime } from "./promptVariables";

/** 提示词管理 › 场景下的模板列表（页面 2）。 */
export default function AiPromptTemplateList() {
  const { sceneId = "" } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const confirm = useConfirm();
  const [scene, setScene] = useState<AiScene | null>(null);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AiPromptTemplateItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState<string[]>([]);

  useEffect(() => {
    findAiSceneBySceneId(sceneId)
      .then((s) => {
        if (!s) {
          toast.error(`场景不存在：${sceneId}`);
          navigate("/ai/prompts");
        }
        setScene(s);
      })
      .catch((err) => {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(ax.response?.data?.detail ?? "加载失败");
      });
  }, [sceneId, navigate, toast]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listAiPromptTemplates(sceneId, page, size);
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [sceneId, page, size, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const toggleSelected = (id: string) =>
    setSelected((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));

  const publish = async (it: AiPromptTemplateItem) => {
    const active = items.find((x) => x.active);
    const ok = await confirm({
      title: "发布模板",
      message: active
        ? `发布后 V${it.seq} 立即对新请求生效，V${active.seq} 失效。`
        : `发布后 V${it.seq} 立即对新请求生效。`,
    });
    if (!ok) return;
    try {
      const latest = await getAiPromptTemplate(it.id);
      await publishAiPromptTemplate(it.id, latest.version);
      toast.success(`V${it.seq} 已发布`);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "发布失败");
    }
  };

  const canCompare = selected.length >= 2 && selected.length <= 3;

  const columns: Column<AiPromptTemplateItem>[] = [
    {
      key: "select",
      header: "",
      width: "3rem",
      render: (it) => (
        <input
          type="checkbox"
          aria-label={`选择 V${it.seq}`}
          checked={selected.includes(it.id)}
          onChange={() => toggleSelected(it.id)}
        />
      ),
    },
    { key: "seq", header: "版本", width: "5rem", render: (it) => `V${it.seq}` },
    { key: "name", header: "名称", width: "10rem" },
    {
      key: "remark",
      header: "修改说明",
      render: (it) => (
        <span className="block max-w-[16rem] truncate" title={it.remark ?? ""}>
          {it.remark || "—"}
        </span>
      ),
    },
    {
      key: "active",
      header: "状态",
      width: "7rem",
      render: (it) =>
        it.active ? (
          <Badge size="sm" color="success">
            ●生效中
          </Badge>
        ) : (
          "未生效"
        ),
    },
    {
      key: "tested",
      header: "测试",
      width: "6rem",
      render: (it) =>
        it.tested ? <span className="text-success-600">✓已测</span> : <span className="text-gray-400">✗未测</span>,
    },
    { key: "updatedBy", header: "更新人", width: "7rem", render: (it) => it.updatedBy ?? "—" },
    { key: "updatedAt", header: "更新时间", width: "11rem", render: (it) => formatDateTime(it.updatedAt) },
    {
      key: "actions",
      header: "操作",
      width: "14rem",
      render: (it) => (
        <div className="flex gap-2">
          <Button size="sm" variant="outline" type="button" onClick={() => navigate(`/ai/prompt-templates/${it.id}`)}>
            {it.active ? "查看" : "编辑"}
          </Button>
          <Button
            size="sm"
            variant="outline"
            type="button"
            onClick={() => navigate(`/ai/prompts/${sceneId}/templates/new?copyFrom=${it.id}`)}
          >
            复制
          </Button>
          {!it.active && (
            <span title={it.tested ? "" : "请先测试"}>
              <Button size="sm" variant="primary" type="button" disabled={!it.tested} onClick={() => void publish(it)}>
                发布
              </Button>
            </span>
          )}
        </div>
      ),
    },
  ];

  return (
    <>
      <PageMeta title="提示词模板 | Love Space Admin" description="场景下的提示词模板" />
      <div className="flex items-center justify-between mb-6">
        <span title={canCompare ? "" : "请勾选 2~3 个模板"}>
          <Button
            size="sm"
            variant="outline"
            type="button"
            disabled={!canCompare}
            onClick={() => navigate(`/ai/prompts/${sceneId}/compare?ids=${selected.join(",")}`)}
          >
            对比所选({selected.length})
          </Button>
        </span>
        <Link
          to={`/ai/prompts/${sceneId}/templates/new`}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          + 新建模板
        </Link>
      </div>
      <div className="space-y-6">
        <ComponentCard title={`提示词管理 › ${scene?.name ?? ""} (${sceneId})`}>
          <DataTable
            columns={columns}
            rows={items}
            rowKey={(it) => it.id}
            loading={loading}
            emptyText="暂无模板，点击新建"
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
