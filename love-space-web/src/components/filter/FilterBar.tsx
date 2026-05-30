import { ReactNode, useEffect, useRef, useState } from "react";
import flatpickr from "flatpickr";
import "flatpickr/dist/flatpickr.css";
import { CalenderIcon } from "../../icons";

export type FilterFieldType = "text" | "select" | "date";

/**
 * 日期筛选项：用 flatpickr 提供日历弹窗，输出 "YYYY-MM-DD" 字符串。
 * 受外部 value 控制，value 被清空（Reset）时同步清空日历。
 */
function DateFilterInput({
  value,
  placeholder,
  onChange,
}: {
  value: string;
  placeholder?: string;
  onChange: (value: string) => void;
}) {
  const inputRef = useRef<HTMLInputElement>(null);
  const fpRef = useRef<flatpickr.Instance | null>(null);

  useEffect(() => {
    if (!inputRef.current) return;
    const fp = flatpickr(inputRef.current, {
      dateFormat: "Y-m-d",
      static: true,
      monthSelectorType: "static",
      onChange: (_dates, dateStr) => onChange(dateStr),
    });
    fpRef.current = Array.isArray(fp) ? fp[0] : fp;
    return () => fpRef.current?.destroy();
    // 仅初始化一次；onChange 通过稳定的 setValues 更新，无需重建实例
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    const fp = fpRef.current;
    if (!fp) return;
    if (value !== (inputRef.current?.value ?? "")) {
      if (value) fp.setDate(value, false);
      else fp.clear();
    }
  }, [value]);

  return (
    <div className="relative">
      <input
        ref={inputRef}
        placeholder={placeholder}
        className="border rounded px-3 py-2 pr-9 text-sm min-w-[160px]"
      />
      <span className="absolute text-gray-400 -translate-y-1/2 pointer-events-none right-2 top-1/2">
        <CalenderIcon className="size-5" />
      </span>
    </div>
  );
}

export interface FilterFieldOption {
  label: string;
  value: string;
}

export interface FilterField {
  name: string;
  label: string;
  type: FilterFieldType;
  placeholder?: string;
  options?: FilterFieldOption[];
}

export type FilterValues = Record<string, string>;

interface FilterBarProps {
  fields: FilterField[];
  initialValues?: FilterValues;
  onApply: (values: FilterValues) => void;
  onReset?: () => void;
  rightSlot?: ReactNode;
}

/**
 * 通用筛选条：渲染一组输入，提供 Apply / Reset 两个动作。
 * - Apply：把当前内部值回调给 onApply
 * - Reset：清空本地值并触发 onReset
 */
export default function FilterBar({ fields, initialValues, onApply, onReset, rightSlot }: FilterBarProps) {
  const [values, setValues] = useState<FilterValues>(initialValues ?? {});

  const update = (name: string, value: string) =>
    setValues((prev) => ({ ...prev, [name]: value }));

  const reset = () => {
    setValues({});
    onReset?.();
  };

  return (
    <div className="flex flex-wrap items-end gap-3 mb-4">
      {fields.map((f) => (
        <div key={f.name} className="flex flex-col">
          <label className="text-xs text-gray-500 mb-1">{f.label}</label>
          {f.type === "select" ? (
            <select
              className="border rounded px-3 py-2 text-sm min-w-[140px]"
              value={values[f.name] ?? ""}
              onChange={(e) => update(f.name, e.target.value)}
            >
              <option value="">全部</option>
              {f.options?.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          ) : f.type === "date" ? (
            <DateFilterInput
              value={values[f.name] ?? ""}
              placeholder={f.placeholder}
              onChange={(v) => update(f.name, v)}
            />
          ) : (
            <input
              type="text"
              placeholder={f.placeholder}
              className="border rounded px-3 py-2 text-sm min-w-[160px]"
              value={values[f.name] ?? ""}
              onChange={(e) => update(f.name, e.target.value)}
            />
          )}
        </div>
      ))}
      <div className="flex gap-2">
        {rightSlot}
        <button
          type="button"
          onClick={() => onApply(values)}
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600"
        >
          Apply
        </button>
        <button
          type="button"
          onClick={reset}
          className="px-4 py-2 text-sm rounded border border-gray-300 text-gray-700 hover:bg-gray-50"
        >
          Reset
        </button>
      </div>
    </div>
  );
}
