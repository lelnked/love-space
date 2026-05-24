import ImageUploader from "./ImageUploader";

/** 列表中的单张图片：objectKey 提交给后端，previewUrl 用于编辑回填预览。 */
export interface ImageListItem {
  objectKey: string;
  previewUrl?: string;
}

interface ImageUploaderListProps {
  /** 当前图片列表。 */
  value: ImageListItem[];
  /** 列表变化（上传 / 添加 / 删除）回调。 */
  onChange: (items: ImageListItem[]) => void;
  disabled?: boolean;
  /** 按下标提供的每行错误信息。 */
  errors?: (string | undefined)[];
  /** 单个上传格子额外 class。 */
  itemClassName?: string;
}

/**
 * 多图上传公用组件：在 ImageUploader 基础上做「添加行 / 删除行」管理。
 *
 * 受控组件：父级持有 ImageListItem[]，提交时取各项 objectKey。
 * 每个格子复用 ImageUploader（隐藏其内置删除，改由本组件做行级删除）。
 */
export default function ImageUploaderList({
  value,
  onChange,
  disabled,
  errors,
  itemClassName,
}: ImageUploaderListProps) {
  const updateAt = (index: number, objectKey: string) =>
    onChange(value.map((it, i) => (i === index ? { ...it, objectKey } : it)));

  const removeAt = (index: number) =>
    onChange(value.filter((_, i) => i !== index));

  const addRow = () =>
    onChange([...value, { objectKey: "", previewUrl: undefined }]);

  return (
    <div className="flex flex-wrap gap-3">
      {value.map((item, i) => (
        <div key={i} className="flex flex-col gap-1">
          <div className="relative">
            <ImageUploader
              value={item.objectKey}
              previewUrl={item.previewUrl}
              onChange={(objectKey) => updateAt(i, objectKey)}
              disabled={disabled}
              hideRemove
              className={itemClassName ?? "h-28 w-28"}
            />
            {!disabled && (
              <button
                type="button"
                onClick={() => removeAt(i)}
                className="absolute -right-2 -top-2 flex h-5 w-5 items-center justify-center rounded-full bg-error-500 text-xs text-white shadow"
                aria-label="删除图片"
              >
                ×
              </button>
            )}
          </div>
          {errors?.[i] && (
            <div className="text-error-500 text-xs">{errors[i]}</div>
          )}
        </div>
      ))}

      {!disabled && (
        <button
          type="button"
          onClick={addRow}
          className="flex h-28 w-28 flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 text-sm text-gray-500 transition hover:border-brand-500 dark:border-gray-700"
        >
          <span className="text-2xl leading-none">+</span>
          <span className="mt-1 text-xs">添加图片</span>
        </button>
      )}
    </div>
  );
}
