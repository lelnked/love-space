import { useRef, useState } from "react";
import { AxiosError } from "axios";

import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";
import { EyeIcon, TrashIcon } from "./imageActionIcons";

/** 列表中的单张图片：objectKey 提交给后端，previewUrl 用于编辑回填预览。 */
export interface ImageListItem {
  objectKey: string;
  previewUrl?: string;
}

interface ImageUploaderListProps {
  /** 当前图片列表。 */
  value: ImageListItem[];
  /** 列表变化（上传 / 删除）回调。 */
  onChange: (items: ImageListItem[]) => void;
  disabled?: boolean;
  /** 按下标提供的每行错误信息。 */
  errors?: (string | undefined)[];
  /** 单个格子额外 class，控制尺寸（默认 h-28 w-28）。 */
  itemClassName?: string;
}

/** 正在上传的临时项：仅本地展示，完成后转为 ImageListItem 并入 value。 */
interface UploadingItem {
  id: number;
  preview: string;
  progress: number;
}

const ACCEPT = "image/png,image/jpeg,image/webp";

/**
 * 多图上传公用组件（Element Plus 照片墙 picture-card 风格）。
 *
 * - 点击末尾「+」格子直接弹出文件选择（支持多选），选完立即并发直传 OSS，
 *   无需先「添加空行」再上传。
 * - 已上传格子显示缩略图，悬停浮出半透明遮罩，提供「预览 / 删除」。
 * - 上传中的格子显示进度遮罩。
 *
 * 受控组件：父级持有 ImageListItem[]，提交时取各项 objectKey。
 */
export default function ImageUploaderList({
  value,
  onChange,
  disabled,
  errors,
  itemClassName,
}: ImageUploaderListProps) {
  const toast = useToast();
  const inputRef = useRef<HTMLInputElement>(null);
  const uidRef = useRef(0);
  const [uploading, setUploading] = useState<UploadingItem[]>([]);
  // 全屏预览的图片地址（空串表示不展示）。
  const [previewSrc, setPreviewSrc] = useState("");

  const cellClass = itemClassName ?? "h-28 w-28";

  const removeAt = (index: number) =>
    onChange(value.filter((_, i) => i !== index));

  const setProgress = (id: number, progress: number) =>
    setUploading((list) =>
      list.map((it) => (it.id === id ? { ...it, progress } : it)),
    );

  const dropUploading = (id: number) =>
    setUploading((list) => list.filter((it) => it.id !== id));

  const handleFiles = async (files: FileList | null) => {
    if (!files || files.length === 0) return;
    const picked = Array.from(files);

    // 为每个文件登记一个上传中占位项（含本地 blob 预览）。
    const tasks = picked.map((file) => {
      const id = ++uidRef.current;
      return { id, file, preview: URL.createObjectURL(file) };
    });
    setUploading((list) => [
      ...list,
      ...tasks.map((t) => ({ id: t.id, preview: t.preview, progress: 0 })),
    ]);

    const results = await Promise.all(
      tasks.map(async (t) => {
        try {
          const objectKey = await uploadToOss(t.file, (p) =>
            setProgress(t.id, Math.round(p * 100)),
          );
          const item: ImageListItem = { objectKey, previewUrl: t.preview };
          return item;
        } catch (err) {
          const ax = err as AxiosError<{ detail?: string }>;
          toast.error(
            ax.response?.data?.detail ??
              (err instanceof Error ? err.message : "上传失败"),
          );
          return null;
        } finally {
          dropUploading(t.id);
        }
      }),
    );

    const added = results.filter((r): r is ImageListItem => r !== null);
    if (added.length > 0) onChange([...value, ...added]);
  };

  const openPicker = () => inputRef.current?.click();

  return (
    <>
      <div className="flex flex-wrap gap-3">
        {value.map((item, i) => (
          <div key={item.objectKey || i} className="flex flex-col gap-1">
            <div
              className={`group relative overflow-hidden rounded-lg border border-gray-200 dark:border-gray-700 ${cellClass}`}
            >
              <img
                src={item.previewUrl}
                alt="预览"
                className="h-full w-full object-cover"
              />
              <div className="absolute inset-0 flex items-center justify-center gap-3 bg-black/50 opacity-0 transition group-hover:opacity-100">
                <button
                  type="button"
                  onClick={() => setPreviewSrc(item.previewUrl ?? "")}
                  className="text-white/90 transition hover:text-white"
                  aria-label="预览图片"
                >
                  <EyeIcon />
                </button>
                {!disabled && (
                  <button
                    type="button"
                    onClick={() => removeAt(i)}
                    className="text-white/90 transition hover:text-white"
                    aria-label="删除图片"
                  >
                    <TrashIcon />
                  </button>
                )}
              </div>
            </div>
            {errors?.[i] && (
              <div className="text-error-500 text-xs">{errors[i]}</div>
            )}
          </div>
        ))}

        {uploading.map((item) => (
          <div
            key={`up-${item.id}`}
            className={`relative overflow-hidden rounded-lg border border-gray-200 dark:border-gray-700 ${cellClass}`}
          >
            <img
              src={item.preview}
              alt="上传中"
              className="h-full w-full object-cover opacity-40"
            />
            <div className="absolute inset-0 flex flex-col items-center justify-center gap-2 px-3">
              <span className="text-xs font-medium text-gray-700 dark:text-gray-200">
                {item.progress}%
              </span>
              <div className="h-1.5 w-full overflow-hidden rounded-full bg-gray-200 dark:bg-gray-700">
                <div
                  className="bg-brand-500 h-full rounded-full transition-all"
                  style={{ width: `${item.progress}%` }}
                />
              </div>
            </div>
          </div>
        ))}

        {!disabled && (
          <button
            type="button"
            onClick={openPicker}
            className={`hover:border-brand-500 hover:text-brand-500 flex flex-col items-center justify-center rounded-lg border border-dashed border-gray-300 text-gray-400 transition dark:border-gray-700 ${cellClass}`}
          >
            <span className="text-3xl leading-none">+</span>
            <span className="mt-1 text-xs">添加图片</span>
          </button>
        )}

        <input
          ref={inputRef}
          type="file"
          accept={ACCEPT}
          multiple
          className="hidden"
          onChange={(e) => {
            void handleFiles(e.target.files);
            e.target.value = ""; // 允许再次选择同一文件
          }}
        />
      </div>

      {previewSrc && (
        <div
          className="fixed inset-0 z-99999 flex items-center justify-center bg-black/80 p-6"
          onClick={() => setPreviewSrc("")}
        >
          <img
            src={previewSrc}
            alt="预览大图"
            className="max-h-full max-w-full rounded-lg object-contain"
          />
        </div>
      )}
    </>
  );
}
