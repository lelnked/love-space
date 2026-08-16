import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Button from "../../components/ui/button/Button";
import Badge from "../../components/ui/badge/Badge";
import ComponentCard from "../../components/common/ComponentCard";
import PageMeta from "../../components/common/PageMeta";
import {
  getRecommendList,
  replaceRecommendListMerchants,
  RecommendListDetail,
} from "../../api/recommendLists";
import { MerchantItem, pageMerchants } from "../../api/merchants";
import { useToast } from "../../context/ToastContext";

interface Row {
  merchantId: string;
  name: string;
  address: string;
  online: boolean;
  sortOrder: number;
}

export default function RecommendListMerchants() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  const [detail, setDetail] = useState<RecommendListDetail | null>(null);
  const [rows, setRows] = useState<Row[]>([]);
  const [cityMerchants, setCityMerchants] = useState<MerchantItem[]>([]);
  const [pickId, setPickId] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getRecommendList(id)
      .then((d) => {
        setDetail(d);
        setRows(
          d.merchants.map((m) => ({
            merchantId: m.merchantId,
            name: m.name,
            address: m.address,
            online: m.online,
            sortOrder: m.sortOrder,
          })),
        );
        // ponytail: 一次拉本城市前 200 个商户做候选，超出再做搜索分页
        return pageMerchants({ cityId: d.cityId, size: 200 }).then((p) =>
          setCityMerchants(p.content),
        );
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const candidates = useMemo(
    () => cityMerchants.filter((m) => !rows.some((r) => r.merchantId === m.id)),
    [cityMerchants, rows],
  );

  const handleAdd = () => {
    const merchant = cityMerchants.find((m) => m.id === pickId);
    if (!merchant) return;
    const nextSort = rows.length ? Math.max(...rows.map((r) => r.sortOrder)) + 1 : 1;
    setRows((prev) => [
      ...prev,
      {
        merchantId: merchant.id,
        name: merchant.name,
        address: merchant.address,
        online: merchant.online,
        sortOrder: nextSort,
      },
    ]);
    setPickId("");
  };

  const handleRemove = (merchantId: string) =>
    setRows((prev) => prev.filter((r) => r.merchantId !== merchantId));

  const handleSortChange = (merchantId: string, value: number) =>
    setRows((prev) =>
      prev.map((r) => (r.merchantId === merchantId ? { ...r, sortOrder: value } : r)),
    );

  const handleSave = async () => {
    if (!id || submitting) return;
    setSubmitting(true);
    try {
      const d = await replaceRecommendListMerchants(
        id,
        rows.map((r) => ({ merchantId: r.merchantId, sortOrder: r.sortOrder })),
      );
      setRows(
        d.merchants.map((m) => ({
          merchantId: m.merchantId,
          name: m.name,
          address: m.address,
          online: m.online,
          sortOrder: m.sortOrder,
        })),
      );
      toast.success("保存成功");
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <PageMeta title="清单商户维护 | Love Space Admin" description="推荐清单商户维护" />
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        清单商户维护{detail ? `：${detail.title}` : ""}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <div className="max-w-4xl space-y-6">
          <ComponentCard title="添加商户">
            <div className="flex items-end gap-3">
              <div className="flex-1">
                <Label>本城市商户</Label>
                <select
                  className="border rounded px-3 py-2 text-sm w-full h-11"
                  value={pickId}
                  onChange={(e) => setPickId(e.target.value)}
                >
                  <option value="">请选择商户</option>
                  {candidates.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.name}
                      {m.online ? "" : "（未上架）"}
                    </option>
                  ))}
                </select>
              </div>
              <Button size="sm" onClick={handleAdd} disabled={!pickId}>
                添加
              </Button>
            </div>
          </ComponentCard>

          <ComponentCard title="清单内商户">
            {rows.length === 0 ? (
              <div className="text-sm text-gray-400 py-6 text-center">暂无商户</div>
            ) : (
              <div className="space-y-2">
                {[...rows]
                  .sort((a, b) => a.sortOrder - b.sortOrder)
                  .map((r) => (
                    <div
                      key={r.merchantId}
                      className="flex items-center gap-4 border border-gray-200 dark:border-gray-800 rounded-lg px-4 py-3"
                    >
                      <div className="flex-1 min-w-0">
                        <div className="font-medium text-gray-800 dark:text-white/90">
                          {r.name}
                        </div>
                        <div className="text-xs text-gray-400 truncate">{r.address}</div>
                      </div>
                      <Badge size="sm" color={r.online ? "success" : "error"}>
                        {r.online ? "已上架" : "未上架"}
                      </Badge>
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-500">排序号</span>
                        <input
                          type="number"
                          className="border rounded px-2 py-1 text-sm w-20"
                          value={r.sortOrder}
                          onChange={(e) =>
                            handleSortChange(r.merchantId, Number(e.target.value))
                          }
                        />
                      </div>
                      <Button size="sm" variant="primary" onClick={() => handleRemove(r.merchantId)}>
                        移除
                      </Button>
                    </div>
                  ))}
              </div>
            )}
          </ComponentCard>

          <div className="flex gap-3">
            <Button size="sm" onClick={handleSave} disabled={submitting}>
              {submitting ? "保存中..." : "保存"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/recommend-lists")}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              返回
            </button>
          </div>
        </div>
      )}
    </>
  );
}
