import { ReactNode, useEffect, useRef, useState } from "react";
import flatpickr from "flatpickr";
import "flatpickr/dist/flatpickr.css";
import { CalenderIcon } from "../../icons";
import Select from "../form/Select";

export type FilterFieldType = "text" | "select" | "date";

/**
 * 日期筛选项：用 flatpickr 提供日历弹窗，输出 "YYYY-MM-DD" 字符串。
 * 受外部 value 控制，value 被清空（Reset）时同步清空日历；minDate / maxDate 限制可选范围。
 */
function DateFilterInput({
  value,
  placeholder,
  minDate,
  maxDate,
  onChange,
}: {
  value: string;
  placeholder?: string;
  minDate?: string;
  maxDate?: string;
  onChange: (value: string) => void;
}) {
  const inputRef = useRef<HTMLInputElement>(null);
  const fpRef = useRef<flatpickr.Instance | null>(null);
  // flatpickr 只初始化一次，经 ref 调用最新的 onChange，避免闭包里的筛选值过期
  const onChangeRef = useRef(onChange);
  useEffect(() => {
    onChangeRef.current = onChange;
  });

  useEffect(() => {
    if (!inputRef.current) return;
    const fp = flatpickr(inputRef.current, {
      dateFormat: "Y-m-d",
      static: true,
      monthSelectorType: "static",
      onChange: (_dates, dateStr) => onChangeRef.current(dateStr),
    });
    fpRef.current = Array.isArray(fp) ? fp[0] : fp;
    return () => fpRef.current?.destroy();
  }, []);

  useEffect(() => {
    const fp = fpRef.current;
    if (!fp) return;
    if (value !== (inputRef.current?.value ?? "")) {
      if (value) fp.setDate(value, false);
      else fp.clear();
    }
  }, [value]);

  useEffect(() => {
    fpRef.current?.set("minDate", minDate || null);
  }, [minDate]);

  useEffect(() => {
    fpRef.current?.set("maxDate", maxDate || null);
  }, [maxDate]);

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
  /** 仅 date：以该字段的当前值作为可选下限 */
  minField?: string;
  /** 仅 date：以该字段的当前值作为可选上限 */
  maxField?: string;
  /** 该字段值变化时清空本字段（如模板版本依赖场景） */
  dependsOn?: string;
  disabled?: boolean;
}

export type FilterValues = Record<string, string>;

interface FilterBarProps {
  fields: FilterField[];
  initialValues?: FilterValues;
  onApply: (values: FilterValues) => void;
  /** Reset 后的值，默认 {}；同时作为 onReset 的参数 */
  resetValues?: FilterValues;
  onReset?: (values: FilterValues) => void;
  /** 内部值变化（含 Reset）时回调 */
  onChange?: (values: FilterValues) => void;
  rightSlot?: ReactNode;
}

/**
 * 通用筛选条：渲染一组输入，提供 Apply / Reset 两个动作。
 * - Apply：把当前内部值回调给 onApply
 * - Reset：回到 resetValues（默认清空）并触发 onReset
 */
export default function FilterBar({
  fields,
  initialValues,
  onApply,
  resetValues,
  onReset,
  onChange,
  rightSlot,
}: FilterBarProps) {
  const [values, setValues] = useState<FilterValues>(initialValues ?? {});

  const change = (next: FilterValues) => {
    setValues(next);
    onChange?.(next);
  };

  const update = (name: string, value: string) => {
    const next = { ...values, [name]: value };
    for (const f of fields) if (f.dependsOn === name) next[f.name] = "";
    change(next);
  };

  const reset = () => {
    const next = resetValues ?? {};
    change(next);
    onReset?.(next);
  };

  return (
    <div className="flex flex-wrap items-end gap-3 mb-4">
      {fields.map((f) => (
        <div key={f.name} className="flex flex-col w-40">
          <label className="text-xs text-gray-500 mb-1">{f.label}</label>
          {f.type === "select" ? (
            <Select
              disabled={f.disabled}
              placeholder="全部"
              value={values[f.name] ?? ""}
              onChange={(v) => update(f.name, v)}
              options={f.options ?? []}
            />
          ) : f.type === "date" ? (
            <DateFilterInput
              value={values[f.name] ?? ""}
              placeholder={f.placeholder}
              minDate={f.minField ? values[f.minField] : undefined}
              maxDate={f.maxField ? values[f.maxField] : undefined}
              onChange={(v) => update(f.name, v)}
            />
          ) : (
            <input
              type="text"
              placeholder={f.placeholder}
              disabled={f.disabled}
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
          查询
        </button>
        <button
          type="button"
          onClick={reset}
          className="px-4 py-2 text-sm rounded border border-gray-300 text-gray-700 hover:bg-gray-50"
        >
          重置
        </button>
      </div>
    </div>
  );
}
