import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import DataTable, { Column } from "../../components/datatable/DataTable";
import { useToast } from "../../context/ToastContext";
import { AiSceneOption, listAiModels, listAiPromptTemplates, listAiSceneOptions } from "../../api/ai";
import {
  AiGenerationItem,
  AiGenerationQuery,
  listAiGenerations,
  RATING_LABEL,
  RATING_TAG_LABEL,
  STATUS_LABEL,
} from "../../api/aiGenerations";
import { formatDateTime } from "../AiPrompts/promptVariables";
import { renderStatus, formatLatency, shortId } from "./format";

function ymd(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

/** 最近 7 天（含今天，本地日期）。 */
function lastSevenDays(): FilterValues {
  const today = new Date();
  const start = new Date(today);
  start.setDate(today.getDate() - 6);
  return { startDate: ymd(start), endDate: ymd(today) };
}

const toOptions = (labels: Record<string, string>) =>
  Object.entries(labels).map(([value, label]) => ({ value, label }));

type Option = { label: string; value: string };

function errorMessage(err: unknown, fallback: string): string {
  return (err as AxiosError<{ detail?: string }>).response?.data?.detail ?? fallback;
}

/** AI 管理 › 生成记录。 */
export default function AiGenerationList() {
  const toast = useToast();
  const navigate = useNavigate();
  const [defaults] = useState(lastSevenDays);
  const [filters, setFilters] = useState<FilterValues>(defaults);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<AiGenerationItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [scenes, setScenes] = useState<AiSceneOption[]>([]);
  const [models, setModels] = useState<string[]>([]);
  const [selectedScene, setSelectedScene] = useState("");
  const [templateOptions, setTemplateOptions] = useState<Option[]>([]);

  useEffect(() => {
    listAiSceneOptions()
      .then(setScenes)
      .catch((err) => toast.error(errorMessage(err, "场景加载失败")));
    listAiModels()
      .then(setModels)
      .catch((err) => toast.error(errorMessage(err, "模型加载失败")));
  }, [toast]);

  useEffect(() => {
    if (!selectedScene) return;
    let cancelled = false;
    listAiPromptTemplates(selectedScene, 1, 200)
      .then((p) => {
        if (!cancelled) setTemplateOptions(p.content.map((t) => ({ value: t.id, label: `V${t.seq} ${t.name}` })));
      })
      .catch((err) => toast.error(errorMessage(err, "模板加载失败")));
    return () => {
      cancelled = true;
    };
  }, [selectedScene, toast]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const query = Object.fromEntries(Object.entries(filters).filter(([, v]) => v)) as AiGenerationQuery;
      const data = await listAiGenerations({ ...query, page, size });
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      toast.error(errorMessage(err, "加载失败"));
    } finally {
      setLoading(false);
    }
  }, [filters, page, size, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleFilterChange = (values: FilterValues) => {
    const sceneId = values.sceneId ?? "";
    if (sceneId !== selectedScene) {
      setTemplateOptions([]);
      setSelectedScene(sceneId);
    }
  };

  const fields: FilterField[] = [
    { name: "startDate", label: "开始日期", type: "date", maxField: "endDate" },
    { name: "endDate", label: "结束日期", type: "date", minField: "startDate" },
    { name: "requestId", label: "请求 ID", type: "text", placeholder: "完整 ID，精确匹配" },
    { name: "anonymousUserId", label: "匿名用户 ID", type: "text", placeholder: "完整 ID，精确匹配" },
    {
      name: "sceneId",
      label: "场景",
      type: "select",
      options: scenes.map((s) => ({ value: s.sceneId, label: s.name })),
    },
    {
      name: "templateId",
      label: "模板版本",
      type: "select",
      options: templateOptions,
      dependsOn: "sceneId",
      disabled: !selectedScene,
    },
    { name: "model", label: "模型", type: "select", options: models.map((m) => ({ value: m, label: m })) },
    { name: "status", label: "状态", type: "select", options: toOptions(STATUS_LABEL) },
    {
      name: "rating",
      label: "评价",
      type: "select",
      options: [...toOptions(RATING_LABEL), { value: "NONE", label: "未评价" }],
    },
    { name: "tag", label: "问题标签", type: "select", options: toOptions(RATING_TAG_LABEL) },
  ];

  const columns: Column<AiGenerationItem>[] = [
    { key: "createdAt", header: "时间", width: "11rem", render: (it) => formatDateTime(it.createdAt) },
    {
      key: "id",
      header: "请求 ID",
      width: "7rem",
      render: (it) => <span title={it.id}>{shortId(it.id)}</span>,
    },
    {
      key: "anonymousUserId",
      header: "匿名用户",
      width: "7rem",
      render: (it) => <span title={it.anonymousUserId}>{shortId(it.anonymousUserId)}</span>,
    },
    { key: "sceneName", header: "场景", render: (it) => it.sceneName ?? it.sceneId },
    { key: "templateSeq", header: "版本", width: "5rem", render: (it) => `V${it.templateSeq}` },
    { key: "finalModel", header: "实际模型", render: (it) => it.finalModel ?? "—" },
    { key: "status", header: "状态", width: "7rem", render: (it) => renderStatus(it.status) },
    { key: "latencyMs", header: "耗时", width: "5rem", render: (it) => formatLatency(it.latencyMs) },
    { key: "rating", header: "评价", width: "5rem", render: (it) => (it.rating ? RATING_LABEL[it.rating] : "—") },
  ];

  return (
    <>
      <PageMeta title="生成记录 | Love Space Admin" description="AI 生成记录" />
      <div className="space-y-6">
        <ComponentCard title="生成记录">
          <FilterBar
            fields={fields}
            initialValues={defaults}
            resetValues={defaults}
            onChange={handleFilterChange}
            onApply={(values) => {
              setFilters(values);
              setPage(1);
            }}
            onReset={(values) => {
              setFilters(values);
              setPage(1);
            }}
          />

          <DataTable
            columns={columns}
            rows={items}
            rowKey={(it) => it.id}
            loading={loading}
            emptyText="暂无生成记录"
            onRowClick={(it) => navigate(`/ai/generations/${it.id}`)}
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
