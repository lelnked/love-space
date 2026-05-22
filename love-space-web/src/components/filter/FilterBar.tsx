import { ReactNode, useState } from "react";

export type FilterFieldType = "text" | "select" | "date";

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
          ) : (
            <input
              type={f.type === "date" ? "date" : "text"}
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
