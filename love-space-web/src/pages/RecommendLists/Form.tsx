import { FormEvent, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import {
  createRecommendList,
  getRecommendList,
  RecommendListUpsertRequest,
  updateRecommendList,
} from "../../api/recommendLists";
import { CityItem, listCities } from "../../api/cities";
import { MerchantItem, pageMerchants } from "../../api/merchants";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

interface SelectedMerchant {
  merchantId: string;
  name: string;
  address: string;
  online: boolean;
  sortOrder: number;
}

export default function RecommendListForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [title, setTitle] = useState("");
  const [introduction, setIntroduction] = useState("");
  const [cityId, setCityId] = useState("");
  const [sortOrder, setSortOrder] = useState<number>(0);
  const [status, setStatus] = useState<string>("ONLINE");
  const [cities, setCities] = useState<CityItem[]>([]);

  const [cityMerchants, setCityMerchants] = useState<MerchantItem[]>([]);
  const [selectedMerchants, setSelectedMerchants] = useState<SelectedMerchant[]>([]);
  const [merchantSearch, setMerchantSearch] = useState("");
  const [showMerchantDropdown, setShowMerchantDropdown] = useState(false);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    void listCities().then(setCities).catch(() => undefined);
  }, []);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getRecommendList(id)
      .then((d) => {
        setTitle(d.title);
        setIntroduction(d.introduction ?? "");
        setCityId(d.cityId);
        setSortOrder(d.sortOrder);
        setStatus(d.status ?? "ONLINE");
        setSelectedMerchants(
          d.merchants.map((m) => ({
            merchantId: m.merchantId,
            name: m.name,
            address: m.address,
            online: m.online,
            sortOrder: m.sortOrder,
          })),
        );
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id, toast]);

  useEffect(() => {
    if (!cityId) {
      setCityMerchants([]);
      return;
    }
    let cancelled = false;
    setLoading(true);
    pageMerchants({ cityId, online: true, size: 200 })
      .then((p) => {
        if (!cancelled) setCityMerchants(p.content);
      })
      .catch(() => {
        if (!cancelled) setCityMerchants([]);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [cityId]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowMerchantDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const filteredMerchants = cityMerchants.filter((m) => {
    const matchesSearch = !merchantSearch.trim() || m.name.toLowerCase().includes(merchantSearch.toLowerCase());
    const notSelected = !selectedMerchants.some((s) => s.merchantId === m.id);
    return matchesSearch && notSelected;
  });

  const handleAddMerchant = (merchantId: string) => {
    const merchant = cityMerchants.find((m) => m.id === merchantId);
    if (!merchant) return;
    const nextSort = selectedMerchants.length
      ? Math.max(...selectedMerchants.map((m) => m.sortOrder)) + 1
      : 1;
    setSelectedMerchants((prev) => [
      ...prev,
      {
        merchantId: merchant.id,
        name: merchant.name,
        address: merchant.address,
        online: merchant.online,
        sortOrder: nextSort,
      },
    ]);
    setMerchantSearch("");
    setShowMerchantDropdown(false);
  };

  const handleRemoveMerchant = (merchantId: string) => {
    setSelectedMerchants((prev) =>
      prev
        .filter((m) => m.merchantId !== merchantId)
        .map((m, idx) => ({ ...m, sortOrder: idx + 1 })),
    );
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!title.trim()) errs.title = "清单标题不能为空";
    if (!editing && !cityId) errs.cityId = "请选择所属城市";
    if (!selectedMerchants.length) errs.merchants = "请至少选择一个商户";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: RecommendListUpsertRequest = {
      title: title.trim(),
      introduction: introduction.trim() || null,
      sortOrder,
      status: status || undefined,
      merchantIds: selectedMerchants.map((m) => m.merchantId),
    };
    if (!editing || cityId) payload.cityId = cityId || undefined;

    setSubmitting(true);
    try {
      if (editing && id) {
        await updateRecommendList(id, payload);
      } else {
        await createRecommendList(payload);
      }

      navigate("/recommend-lists", { replace: true });
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string; errors?: FieldError[] }>;
      const data = ax.response?.data;
      if (data?.errors?.length) {
        const map: Record<string, string> = {};
        for (const fe of data.errors) map[fe.field] = fe.message;
        setFieldErrors(map);
      }
      toast.error(data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑推荐清单" : "新增推荐清单"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} noValidate className="max-w-2xl space-y-5">
          <div>
            <Label>
              标题 <span className="text-error-500">*</span>
            </Label>
            <Input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              error={Boolean(fieldErrors.title)}
              hint={fieldErrors.title}
            />
          </div>
          <div>
            <Label>介绍</Label>
            <textarea
              placeholder="清单介绍（选填）"
              value={introduction}
              onChange={(e) => setIntroduction(e.target.value)}
              className="border rounded px-3 py-2 text-sm w-full min-h-[100px]"
            />
          </div>
          <div>
            <Label>
              所属城市 <span className="text-error-500">*</span>
            </Label>
            <select
              className="border rounded px-3 py-2 text-sm w-full h-11 disabled:bg-gray-100 disabled:text-gray-500"
              value={cityId}
              onChange={(e) => setCityId(e.target.value)}
            >
              <option value="">请选择</option>
              {cities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.chineseName}
                  {c.online ? "" : "（已下架）"}
                </option>
              ))}
            </select>
            {editing && (
              <div className="text-xs text-gray-400 mt-1">修改城市时，请确保清单内现有商户均属于新城市，否则保存会失败</div>
            )}
            {fieldErrors.cityId && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.cityId}</div>
            )}
          </div>
          <div>
            <Label>
              商户 <span className="text-error-500">*</span>
            </Label>
            <div ref={dropdownRef} className="relative">
              <div className="flex flex-wrap gap-2 rounded-lg border border-gray-200 bg-white p-2 focus-within:border-blue-400 focus-within:ring-1 focus-within:ring-blue-100 transition-colors">
                {selectedMerchants.map((m) => (
                  <span
                    key={m.merchantId}
                    className="inline-flex items-center gap-1 rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700"
                  >
                    {m.name}
                    <button
                      type="button"
                      onClick={() => handleRemoveMerchant(m.merchantId)}
                      className="rounded-full p-0.5 text-blue-400 hover:text-blue-600 hover:bg-blue-100 transition-colors"
                    >
                      <svg className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                        <path d="M6.28 5.22a.75.75 0 0 0-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 1 0 1.06 1.06L10 11.06l3.72 3.72a.75.75 0 1 0 1.06-1.06L11.06 10l3.72-3.72a.75.75 0 0 0-1.06-1.06L10 8.94 6.28 5.22Z" />
                      </svg>
                    </button>
                  </span>
                ))}
                <input
                  type="text"
                  value={merchantSearch}
                  onChange={(e) => setMerchantSearch(e.target.value)}
                  onFocus={() => setShowMerchantDropdown(true)}
                  placeholder={selectedMerchants.length === 0 ? "请选择商户" : ""}
                  disabled={!cityId}
                  className="flex-1 min-w-[160px] border-none bg-transparent text-sm outline-none placeholder:text-gray-400 disabled:cursor-not-allowed"
                />
              </div>

              {showMerchantDropdown && cityId && (
                <div className="absolute z-20 mt-1 max-h-56 w-full overflow-auto rounded-lg border border-gray-200 bg-white shadow-lg">
                  {filteredMerchants.length === 0 ? (
                    <div className="px-3 py-2 text-sm text-gray-400">
                      {merchantSearch ? "无匹配商户" : "暂无可选商户"}
                    </div>
                  ) : (
                    filteredMerchants.map((m) => (
                      <button
                        key={m.id}
                        type="button"
                        onClick={() => handleAddMerchant(m.id)}
                        className="flex w-full items-center justify-between px-3 py-2 text-left text-sm hover:bg-gray-50 transition-colors"
                      >
                        <span className="text-gray-800">{m.name}</span>
                        <span className="text-xs text-gray-400">{m.address}</span>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
            {!cityId && (
              <div className="text-xs text-gray-400 mt-1">请先选择所属城市</div>
            )}
            {fieldErrors.merchants && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.merchants}</div>
            )}
          </div>
          <div>
            <Label>排序号</Label>
            <Input
              type="number"
              value={String(sortOrder)}
              onChange={(e) => setSortOrder(Number(e.target.value))}
            />
          </div>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/recommend-lists")}
              disabled={submitting}
              className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
            >
              取消
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
