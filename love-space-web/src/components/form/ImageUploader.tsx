import { useRef, useState } from "react";
import { AxiosError } from "axios";

import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";
import { EyeIcon, ReplaceIcon, TrashIcon } from "./imageActionIcons";

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

const ACCEPT = "image/png,image/jpeg,image/webp,image/gif";

/**
 * 单图上传公用组件（Element Plus 照片墙 picture-card 风格）。
 *
 * - 未选择：虚线「+ 上传图片」格子，支持拖拽 / 点击选择。
 * - 上传中：进度遮罩（百分比 + 进度条）。
 * - 已上传：缩略图，悬停浮出半透明遮罩，提供「预览 / 替换 / 删除」。
 *
 * 受控组件：父级持有 objectKey（value）与可选签名 previewUrl，上传完成回传新的 objectKey。
 * 仅支持 png/jpeg/webp/gif；失败用全局 toast 提示，不阻塞表单。
 *
 * 注：点击通过自有 inputRef.click() 触发，不依赖 react-dropzone 的 open()
 * （后者在 React 19 下不可靠）；拖拽用原生 drag 事件实现。
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
  const inputRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  // 新上传后用本地 blob 预览；否则回退到父级传入的签名 URL。
  const [localPreview, setLocalPreview] = useState<string>("");
  // 全屏预览的图片地址（空串表示不展示）。
  const [previewSrc, setPreviewSrc] = useState("");
  const [dragActive, setDragActive] = useState(false);

  const shownPreview = localPreview || (value ? previewUrl ?? "" : "");

  const doUpload = async (file?: File) => {
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
  };

  const openPicker = () => {
    if (!disabled && !uploading) inputRef.current?.click();
  };

  const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    void doUpload(e.target.files?.[0]);
    e.target.value = ""; // 允许再次选择同一文件
  };

  const onDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragActive(false);
    if (disabled || uploading) return;
    void doUpload(e.dataTransfer.files?.[0]);
  };

  const onDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    if (!dragActive) setDragActive(true);
  };

  const onDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setDragActive(false);
  };

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

  return (
    <>
      <input
        ref={inputRef}
        type="file"
        accept={ACCEPT}
        className="hidden"
        onChange={onInputChange}
      />

      {uploading ? (
        <div
          className={`relative flex items-center justify-center overflow-hidden rounded-xl border border-dashed border-gray-300 bg-gray-50 dark:border-gray-700 dark:bg-gray-900 ${sizeClass}`}
        >
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
        </div>
      ) : shownPreview ? (
        // 已上传：缩略图 + 悬停遮罩。点击格子或「替换」按钮可重新选择。
        <div
          onClick={openPicker}
          onDrop={onDrop}
          onDragOver={onDragOver}
          onDragLeave={onDragLeave}
          className={`group relative cursor-pointer overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700 ${sizeClass}`}
        >
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
            {!disabled && (
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  openPicker();
                }}
                className="text-white/90 transition hover:text-white"
                aria-label="替换图片"
              >
                <ReplaceIcon />
              </button>
            )}
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
      ) : (
        // 未选择：虚线「+ 上传图片」格子。
        <div
          onClick={openPicker}
          onDrop={onDrop}
          onDragOver={onDragOver}
          onDragLeave={onDragLeave}
          className={`flex cursor-pointer flex-col items-center justify-center gap-1 overflow-hidden rounded-xl border border-dashed text-center transition ${
            dragActive
              ? "border-brand-500 bg-gray-100 dark:bg-gray-800"
              : "border-gray-300 bg-gray-50 hover:border-brand-500 dark:border-gray-700 dark:bg-gray-900"
          } ${sizeClass}`}
        >
          <span className="text-3xl leading-none text-gray-400">+</span>
          <span className="text-xs text-gray-400">
            {dragActive ? "松手上传" : "上传图片"}
          </span>
        </div>
      )}

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
