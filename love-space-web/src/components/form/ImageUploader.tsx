import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { AxiosError } from "axios";

import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";

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
  /** 隐藏图片右上角的内置「删除」按钮（列表场景由外层做行删除时用）。 */
  hideRemove?: boolean;
}

/**
 * 单图上传公用组件：拖拽 / 点击选择 → STS 直传 OSS → 进度条 → 预览。
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

  return (
    <div
      {...getRootProps()}
      className={`group relative flex cursor-pointer items-center justify-center overflow-hidden rounded-xl border border-dashed transition ${
        isDragActive
          ? "border-brand-500 bg-gray-100 dark:bg-gray-800"
          : "border-gray-300 bg-gray-50 hover:border-brand-500 dark:border-gray-700 dark:bg-gray-900"
      } ${className ?? "h-40 w-full"}`}
    >
      <input {...getInputProps()} />

      {uploading ? (
        <div className="flex w-full max-w-[180px] flex-col items-center gap-2 px-4">
          <span className="text-sm text-gray-600 dark:text-gray-300">
            上传中 {progress}%
          </span>
          <div className="h-1.5 w-full overflow-hidden rounded-full bg-gray-200 dark:bg-gray-700">
            <div
              className="h-full rounded-full bg-brand-500 transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      ) : shownPreview ? (
        <>
          <img
            src={shownPreview}
            alt="预览"
            className="h-full w-full object-cover"
          />
          {!disabled && !hideRemove && (
            <button
              type="button"
              onClick={handleRemove}
              className="absolute right-2 top-2 hidden rounded-full bg-black/60 px-2 py-1 text-xs text-white group-hover:block"
            >
              删除
            </button>
          )}
        </>
      ) : (
        <div className="flex flex-col items-center gap-1 px-4 text-center">
          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
            {isDragActive ? "松手上传" : "拖拽图片到此处或点击选择"}
          </span>
          <span className="text-xs text-gray-400">支持 PNG / JPG / WebP</span>
        </div>
      )}
    </div>
  );
}
