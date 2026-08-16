import { FormEvent, useEffect, useState } from "react";
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
import { CityItem, getCity, listOnlineCities } from "../../api/cities";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

export default function RecommendListForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [title, setTitle] = useState("");
  const [introduction, setIntroduction] = useState("");
  const [cityId, setCityId] = useState("");
  const [sortOrder, setSortOrder] = useState<number>(0);
  const [cities, setCities] = useState<CityItem[]>([]);

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    if (!id) {
      void listOnlineCities().then(setCities).catch(() => undefined);
      return;
    }
    setLoading(true);
    getRecommendList(id)
      .then((d) => {
        setTitle(d.title);
        setIntroduction(d.introduction ?? "");
        setCityId(d.cityId);
        setSortOrder(d.sortOrder);
        // 编辑时城市不可改，只需回显绑定城市名
        void getCity(d.cityId)
          .then((c) => setCities([c]))
          .catch(() => undefined);
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const errs: Record<string, string> = {};
    if (!title.trim()) errs.title = "清单标题不能为空";
    if (!editing && !cityId) errs.cityId = "请选择所属城市";
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: RecommendListUpsertRequest = {
      title: title.trim(),
      introduction: introduction.trim() || null,
      sortOrder,
    };
    if (!editing) payload.cityId = cityId;

    setSubmitting(true);
    try {
      if (editing && id) await updateRecommendList(id, payload);
      else await createRecommendList(payload);
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
              disabled={editing}
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
              <div className="text-xs text-gray-400 mt-1">清单创建后所属城市不可修改</div>
            )}
            {fieldErrors.cityId && (
              <div className="text-error-500 text-xs mt-1">{fieldErrors.cityId}</div>
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
