import { FormEvent, useState } from "react";
import { AxiosError } from "axios";
import Button from "../../components/ui/button/Button";
import { useToast } from "../../context/ToastContext";
import {
  AiGenerationDetail,
  AiRating,
  AiRatingTag,
  RATING_LABEL,
  RATING_TAG_LABEL,
  rateAiGeneration,
} from "../../api/aiGenerations";
import { formatDateTime } from "../AiPrompts/promptVariables";

const REMARK_MAX = 500;

/** 人工评价：每条记录一个评价，保存即覆盖；生成中不可评价。父组件以 key 重挂载来回填。 */
export default function RatingPanel({ detail, onSaved }: { detail: AiGenerationDetail; onSaved: () => Promise<void> }) {
  const toast = useToast();
  const [rating, setRating] = useState<AiRating | null>(detail.rating);
  const [tags, setTags] = useState<AiRatingTag[]>(detail.ratingTags);
  const [remark, setRemark] = useState(detail.ratingRemark ?? "");
  const [submitting, setSubmitting] = useState(false);
  const processing = detail.status === "PROCESSING";
  const disabled = processing || submitting;

  const toggleTag = (tag: AiRatingTag) =>
    setTags((prev) => (prev.includes(tag) ? prev.filter((t) => t !== tag) : [...prev, tag]));

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!rating) return;
    setSubmitting(true);
    try {
      await rateAiGeneration(detail.id, { rating, tags, remark: remark.trim() ? remark : null });
      toast.success("评价已保存");
      await onSaved();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "保存失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-4">
      {processing && <div className="text-sm text-warning-600">生成中的记录不能评价</div>}
      <fieldset disabled={disabled} className="space-y-4">
        <div className="flex flex-wrap items-center gap-4 text-sm">
          <span className="text-gray-500">评价</span>
          {(Object.keys(RATING_LABEL) as AiRating[]).map((r) => (
            <label key={r} className="flex items-center gap-1">
              <input type="radio" name="rating" checked={rating === r} onChange={() => setRating(r)} />
              {RATING_LABEL[r]}
            </label>
          ))}
        </div>
        <div className="text-sm">
          <div className="text-gray-500 mb-2">问题标签</div>
          <div className="flex flex-wrap gap-x-4 gap-y-2">
            {(Object.keys(RATING_TAG_LABEL) as AiRatingTag[]).map((t) => (
              <label key={t} className="flex items-center gap-1">
                <input type="checkbox" checked={tags.includes(t)} onChange={() => toggleTag(t)} />
                {RATING_TAG_LABEL[t]}
              </label>
            ))}
          </div>
        </div>
        <div className="text-sm">
          <div className="text-gray-500 mb-2">备注</div>
          <textarea
            className="border rounded px-3 py-2 text-sm w-full min-h-[80px] disabled:bg-gray-100"
            maxLength={REMARK_MAX}
            value={remark}
            onChange={(e) => setRemark(e.target.value)}
          />
          <div className="text-xs text-gray-400 mt-1 text-right">
            {remark.length}/{REMARK_MAX}
          </div>
        </div>
      </fieldset>
      <div className="flex items-center justify-between">
        <div className="text-xs text-gray-500">
          {detail.rating && `最后评价：${detail.ratedBy ?? "—"} · ${formatDateTime(detail.ratedAt)}`}
        </div>
        <Button size="sm" type="submit" disabled={disabled || !rating}>
          {submitting ? "提交中..." : detail.rating ? "修改评价" : "保存评价"}
        </Button>
      </div>
    </form>
  );
}
