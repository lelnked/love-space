import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { AxiosError } from "axios";

import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";
import { EyeIcon, TrashIcon } from "./imageActionIcons";

interface ImageUploaderProps {
  /** 当前 objectKey（空串表示未选择）。 */
  value: string;
  /** 编辑回填用的签名访问 URL（新上传后会被本地预览覆盖）。 */
  previewUrl?: string;
  /** 上传成功后回传 objectKey；点击删除时回传空串。 */
  onChange: (objectKey: string) => void;
  disabled?: boolean;
  /** 容器额外 class，可控制尺寸/比例（默认 h-40）。 */
  className?: string;
  /** 隐藏图片悬停遮罩里的「删除」按钮（外层自行做删除时用）。 */
  hideRemove?: boolean;
}

/**
 * 单图上传公用组件（Element Plus 照片墙 picture-card 风格）。
 *
 * - 未选择：虚线「+ 上传图片」格子，支持拖拽 / 点击选择。
 * - 上传中：进度遮罩（百分比 + 进度条）。
 * - 已上传：缩略图，悬停浮出半透明遮罩，提供「预览（全屏大图）/ 删除」。
 *
 * 受控组件：父级持有 objectKey（value）与可选签名 previewUrl，上传完成回传新的 objectKey。
 * 仅支持 png/jpeg/webp；失败用全局 toast 提示，不阻塞表单。
 */
export default function ImageUploader({
  value,
  previewUrl,
  onChange,
  disabled,
  className,
  hideRemove,
}: ImageUploaderProps) {
  const toast = useToast();
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  // 新上传后用本地 blob 预览；否则回退到父级传入的签名 URL。
  const [localPreview, setLocalPreview] = useState<string>("");
  // 全屏预览的图片地址（空串表示不展示）。
  const [previewSrc, setPreviewSrc] = useState("");

  const shownPreview = localPreview || (value ? previewUrl ?? "" : "");

  const onDrop = useCallback(
    async (accepted: File[]) => {
      const file = accepted[0];
      if (!file) return;
      setUploading(true);
      setProgress(0);
      try {
        const objectKey = await uploadToOss(file, (p) =>
          setProgress(Math.round(p * 100)),
        );
        setLocalPreview(URL.createObjectURL(file));
        onChange(objectKey);
      } catch (err) {
        const ax = err as AxiosError<{ detail?: string }>;
        toast.error(
          ax.response?.data?.detail ??
            (err instanceof Error ? err.message : "上传失败"),
        );
      } finally {
        setUploading(false);
      }
    },
    [onChange, toast],
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: false,
    disabled: disabled || uploading,
    accept: {
      "image/png": [],
      "image/jpeg": [],
      "image/webp": [],
    },
  });

  const handleRemove = (e: React.MouseEvent) => {
    e.stopPropagation();
    setLocalPreview("");
    onChange("");
  };

  const handlePreview = (e: React.MouseEvent) => {
    e.stopPropagation();
    setPreviewSrc(shownPreview);
  };

  const sizeClass = className ?? "h-40 w-full";

  // 已上传：缩略图 + 悬停遮罩。点击格子本身可重新选择以替换；
  // 遮罩按钮（预览 / 删除）阻止冒泡，避免误触发文件选择。
  if (shownPreview && !uploading) {
    return (
      <>
        <div
          {...getRootProps()}
          className={`group relative cursor-pointer overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700 ${sizeClass}`}
        >
          <input {...getInputProps()} />
          <img
            src={shownPreview}
            alt="预览"
            className="h-full w-full object-cover"
          />
          <div className="absolute inset-0 flex items-center justify-center gap-4 bg-black/50 opacity-0 transition group-hover:opacity-100">
            <button
              type="button"
              onClick={handlePreview}
              className="text-white/90 transition hover:text-white"
              aria-label="预览图片"
            >
              <EyeIcon />
            </button>
            {!disabled && !hideRemove && (
              <button
                type="button"
                onClick={handleRemove}
                className="text-white/90 transition hover:text-white"
                aria-label="删除图片"
              >
                <TrashIcon />
              </button>
            )}
          </div>
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

  return (
    <div
      {...getRootProps()}
      className={`group relative flex cursor-pointer items-center justify-center overflow-hidden rounded-xl border border-dashed transition ${
        isDragActive
          ? "border-brand-500 bg-gray-100 dark:bg-gray-800"
          : "border-gray-300 bg-gray-50 hover:border-brand-500 dark:border-gray-700 dark:bg-gray-900"
      } ${sizeClass}`}
    >
      <input {...getInputProps()} />

      {uploading ? (
        <div className="flex w-full max-w-[180px] flex-col items-center gap-2 px-4">
          <span className="text-sm text-gray-600 dark:text-gray-300">
            上传中 {progress}%
          </span>
          <div className="h-1.5 w-full overflow-hidden rounded-full bg-gray-200 dark:bg-gray-700">
            <div
              className="bg-brand-500 h-full rounded-full transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      ) : (
        <div className="flex flex-col items-center gap-1 px-4 text-center text-gray-400">
          <span className="text-3xl leading-none">+</span>
          <span className="text-xs">
            {isDragActive ? "松手上传" : "上传图片"}
          </span>
        </div>
      )}
    </div>
  );
}
