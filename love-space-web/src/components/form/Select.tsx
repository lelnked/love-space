import { useState } from "react";
import { ChevronDownIcon } from "../../icons";

export interface SelectOption {
  value: string;
  label: string;
}

interface SelectProps {
  options: SelectOption[];
  /** 受控值；传入即受控，由父级持有。不传则组件内部自管理（defaultValue 初始化） */
  value?: string;
  /** 非受控模式下的初始值 */
  defaultValue?: string;
  onChange?: (value: string) => void;
  /** 占位项文案（value=""，可再次选中回到空值）；不传则不渲染占位项 */
  placeholder?: string;
  disabled?: boolean;
  className?: string;
}

const Select: React.FC<SelectProps> = ({
  options,
  value,
  defaultValue = "",
  onChange,
  placeholder,
  disabled = false,
  className = "",
}) => {
  const [internalValue, setInternalValue] = useState<string>(defaultValue);
  const isControlled = value !== undefined;
  const selectedValue = isControlled ? value : internalValue;

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const next = e.target.value;
    if (!isControlled) setInternalValue(next);
    onChange?.(next);
  };

  return (
    <div className="relative">
      <select
        disabled={disabled}
        className={`h-11 w-full appearance-none rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 pr-11 text-sm shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800 ${
          selectedValue
            ? "text-gray-800 dark:text-white/90"
            : "text-gray-400 dark:text-gray-400"
        } ${
          disabled
            ? "text-gray-500 border-gray-300 opacity-40 bg-gray-100 cursor-not-allowed dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700"
            : ""
        } ${className}`}
        value={selectedValue}
        onChange={handleChange}
      >
        {/* 占位项：可再次选中回到空值，供筛选「全部」/ 表单「请选择」复用 */}
        {placeholder && (
          <option
            value=""
            className="text-gray-700 dark:bg-gray-900 dark:text-gray-400"
          >
            {placeholder}
          </option>
        )}
        {options.map((option) => (
          <option
            key={option.value}
            value={option.value}
            className="text-gray-700 dark:bg-gray-900 dark:text-gray-400"
          >
            {option.label}
          </option>
        ))}
      </select>
      {/* 自定义箭头：appearance-none 抹掉系统箭头后由它顶上，点击经 pointer-events-none 透传到 select */}
      <span className="absolute text-gray-500 -translate-y-1/2 pointer-events-none right-4 top-1/2 dark:text-gray-400">
        <ChevronDownIcon className="size-5" />
      </span>
    </div>
  );
};

export default Select;
