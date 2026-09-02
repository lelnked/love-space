import { FormEvent, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import ImageUploaderList, { ImageListItem } from "../../components/form/ImageUploaderList";
import Checkbox from "../../components/form/input/Checkbox";
import RichTextEditor, {
  type RichTextEditorRef,
} from "../../components/form/RichTextEditor";
import {
  ACTIVITY_LEVELS,
  ACTIVITY_PERIOD_LABEL,
  ActivityItineraryItem,
  ActivityLevel,
  ActivityUpsertRequest,
  createActivity,
  getActivity,
  updateActivity,
} from "../../api/activities";
import { PERIOD_VALUES, Period } from "../../api/types";
import { useToast } from "../../context/ToastContext";

interface FieldError {
  field: string;
  message: string;
}

export default function ActivityForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editing = Boolean(id);

  const [images, setImages] = useState<ImageListItem[]>([]);
  const [title, setTitle] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [periods, setPeriods] = useState<Period[]>([]);
  const [level, setLevel] = useState<ActivityLevel | "">("");
  const [subtitle, setSubtitle] = useState("");
  const [introduction, setIntroduction] = useState("");
  const [editorNote, setEditorNote] = useState("");
  const [gatheringPlace, setGatheringPlace] = useState("");
  const [dismissalPlace, setDismissalPlace] = useState("");
  const [transportation, setTransportation] = useState("");
  const [visa, setVisa] = useState("");
  const [landscape, setLandscape] = useState("");
  const [itinerary, setItinerary] = useState<ActivityItineraryItem[]>([]);
  const [detailHtml, setDetailHtml] = useState("");
  const editorRef = useRef<RichTextEditorRef>(null);


  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const toast = useToast();

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getActivity(id)
      .then((d) => {
        setImages(d.images.map((im) => ({ objectKey: im.id, previewUrl: im.url })));
        setTitle(d.title);
        setTags(d.tags);
        setPeriods(d.periods);
        setLevel(d.level ?? "");
        setSubtitle(d.subtitle ?? "");
        setIntroduction(d.introduction ?? "");
        setEditorNote(d.editorNote ?? "");
        setGatheringPlace(d.gatheringPlace ?? "");
        setDismissalPlace(d.dismissalPlace ?? "");
        setTransportation(d.transportation ?? "");
        setVisa(d.visa ?? "");
        setLandscape(d.landscape ?? "");
        setItinerary(d.itinerary);
        setDetailHtml(d.detailHtml ?? "");
      })
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const togglePeriod = (p: Period) =>
    setPeriods((prev) => (prev.includes(p) ? prev.filter((x) => x !== p) : [...prev, p]));

  const updateItinerary = (index: number, patch: Partial<ActivityItineraryItem>) =>
    setItinerary((prev) => prev.map((it, i) => (i === index ? { ...it, ...patch } : it)));

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setFieldErrors({});

    const cleanTags = tags.map((t) => t.trim()).filter(Boolean);
    const errs: Record<string, string> = {};
    if (!title.trim()) errs.title = "活动标题不能为空";
    if (images.length === 0) errs.images = "至少上传 1 张图片";
    itinerary.forEach((it, i) => {
      if (!it.title.trim()) errs[`itinerary.${i}.title`] = "子条目标题不能为空";
      if (!it.content.trim()) errs[`itinerary.${i}.content`] = "子条目内容不能为空";
    });
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }

    const payload: ActivityUpsertRequest = {
      images: images.map((it) => it.objectKey.trim()),
      title: title.trim(),
      tags: cleanTags,
      periods,
      level: level || null,
      subtitle: subtitle.trim() || null,
      introduction: introduction.trim() || null,
      editorNote: editorNote.trim() || null,
      gatheringPlace: gatheringPlace.trim() || null,
      dismissalPlace: dismissalPlace.trim() || null,
      transportation: transportation.trim() || null,
      visa: visa.trim() || null,
      landscape: landscape.trim() || null,
      itinerary: itinerary.map((it) => ({ title: it.title.trim(), content: it.content.trim() })),
      detailHtml: editorRef.current?.getHtmlForSubmit() || detailHtml || null,
    };

    setSubmitting(true);
    try {
      if (editing && id) await updateActivity(id, payload);
      else await createActivity(payload);
      navigate("/activities", { replace: true });
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

  const sectionClass =
    "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900";
  const sectionTitleClass = "text-sm font-semibold text-gray-800 dark:text-white/90 mb-3";

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        {editing ? "编辑活动" : "新增活动"}
      </h1>

      {loading ? (
        <div className="text-gray-500">加载中...</div>
      ) : (
        <form onSubmit={handleSubmit} noValidate className="max-w-4xl space-y-5">
          {/* 1. 基础信息 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>基础信息</legend>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                <Label>副标题</Label>
                <Input
                  value={subtitle}
                  onChange={(e) => setSubtitle(e.target.value)}
                />
              </div>
            </div>
          </fieldset>

          {/* 2. 图片 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>图片（至少 1 张）</legend>
            {fieldErrors.images && (
              <div className="text-error-500 text-xs mb-2">{fieldErrors.images}</div>
            )}
            <ImageUploaderList value={images} onChange={setImages} />
          </fieldset>

          {/* 3. 标签 / 周期 / 级别 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>标签 / 适合周期 / 级别</legend>
            <div className="mb-4">
              <Label>标签</Label>
              <div className="space-y-2">
                {tags.map((t, i) => (
                  <div key={i} className="flex items-center gap-2 max-w-md">
                    <Input
                      value={t}
                      onChange={(e) =>
                        setTags((prev) => prev.map((x, j) => (j === i ? e.target.value : x)))
                      }
                    />
                    <Button
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() => setTags((prev) => prev.filter((_, j) => j !== i))}
                    >
                      删除
                    </Button>
                  </div>
                ))}
                <Button
                  type="button"
                  size="sm"
                  variant="outline"
                  onClick={() => setTags((prev) => [...prev, ""])}
                >
                  添加标签
                </Button>
              </div>
            </div>
            <div className="mb-4">
              <Label>适合周期</Label>
              <div className="flex flex-wrap gap-4">
                {PERIOD_VALUES.map((p) => (
                  <Checkbox
                    key={p}
                    label={ACTIVITY_PERIOD_LABEL[p]}
                    checked={periods.includes(p)}
                    onChange={() => togglePeriod(p)}
                  />
                ))}
              </div>
            </div>
            <div>
              <Label>级别</Label>
              <div className="flex flex-wrap items-center gap-4 text-sm text-gray-700 dark:text-gray-300">
                <label className="flex items-center gap-1.5">
                  <input
                    type="radio"
                    name="level"
                    checked={level === ""}
                    onChange={() => setLevel("")}
                  />
                  不设置
                </label>
                {ACTIVITY_LEVELS.map((l) => (
                  <label key={l} className="flex items-center gap-1.5">
                    <input
                      type="radio"
                      name="level"
                      checked={level === l}
                      onChange={() => setLevel(l)}
                    />
                    {l}
                  </label>
                ))}
              </div>
            </div>
          </fieldset>

          {/* 4. 文本信息 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>文本信息</legend>
            <div className="space-y-4">
              <div>
                <Label>简介</Label>
                <textarea
                  placeholder="活动简介（选填）"
                  value={introduction}
                  onChange={(e) => setIntroduction(e.target.value)}
                  className="border rounded px-3 py-2 text-sm w-full min-h-[100px]"
                />
              </div>
              <div>
                <Label>编辑说</Label>
                <textarea
                  placeholder="编辑说（选填）"
                  value={editorNote}
                  onChange={(e) => setEditorNote(e.target.value)}
                  className="border rounded px-3 py-2 text-sm w-full min-h-[100px]"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <Label>集合地</Label>
                  <Input
                    value={gatheringPlace}
                    onChange={(e) => setGatheringPlace(e.target.value)}
                  />
                </div>
                <div>
                  <Label>解散地</Label>
                  <Input
                    value={dismissalPlace}
                    onChange={(e) => setDismissalPlace(e.target.value)}
                  />
                </div>
                <div>
                  <Label>交通</Label>
                  <Input
                    value={transportation}
                    onChange={(e) => setTransportation(e.target.value)}
                  />
                </div>
                <div>
                  <Label>签证</Label>
                  <Input value={visa} onChange={(e) => setVisa(e.target.value)} />
                </div>
                <div>
                  <Label>景观</Label>
                  <Input
                    value={landscape}
                    onChange={(e) => setLandscape(e.target.value)}
                  />
                </div>
              </div>
            </div>
          </fieldset>

          {/* 5. 路线子条目 */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>路线子条目</legend>
            <div className="space-y-4">
              {itinerary.map((it, i) => (
                <div
                  key={i}
                  className="border border-gray-200 dark:border-gray-800 rounded-lg p-3 space-y-3"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">子条目 {i + 1}</span>
                    <Button
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() => setItinerary((prev) => prev.filter((_, j) => j !== i))}
                    >
                      删除子条目
                    </Button>
                  </div>
                  <div>
                    <Label>
                      标题 <span className="text-error-500">*</span>
                    </Label>
                    <Input
                      value={it.title}
                      onChange={(e) => updateItinerary(i, { title: e.target.value })}
                      error={Boolean(fieldErrors[`itinerary.${i}.title`])}
                      hint={fieldErrors[`itinerary.${i}.title`]}
                    />
                  </div>
                  <div>
                    <Label>
                      内容 <span className="text-error-500">*</span>
                    </Label>
                    <textarea
                      placeholder="子条目内容"
                      value={it.content}
                      onChange={(e) => updateItinerary(i, { content: e.target.value })}
                      className="border rounded px-3 py-2 text-sm w-full min-h-[80px]"
                    />
                    {fieldErrors[`itinerary.${i}.content`] && (
                      <div className="text-error-500 text-xs mt-1">
                        {fieldErrors[`itinerary.${i}.content`]}
                      </div>
                    )}
                  </div>
                </div>
              ))}
              <Button
                type="button"
                size="sm"
                variant="outline"
                onClick={() => setItinerary((prev) => [...prev, { title: "", content: "" }])}
              >
                添加子条目
              </Button>
            </div>
          </fieldset>

          {/* 6. 活动详情说明（富文本） */}
          <fieldset className={sectionClass}>
            <legend className={sectionTitleClass}>活动详情说明</legend>
            <RichTextEditor ref={editorRef} initialValue={detailHtml} />
          </fieldset>

          <div className="flex gap-3">
            <Button size="sm" disabled={submitting}>
              {submitting ? "提交中..." : editing ? "保存" : "创建"}
            </Button>
            <button
              type="button"
              onClick={() => navigate("/activities")}
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
