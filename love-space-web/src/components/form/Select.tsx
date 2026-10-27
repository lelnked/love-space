import { useEffect, useMemo, useRef, useState } from "react";
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
  const [open, setOpen] = useState(false);
  const [focusedIndex, setFocusedIndex] = useState(-1);
  const wrapperRef = useRef<HTMLDivElement>(null);

  const isControlled = value !== undefined;
  const selectedValue = isControlled ? value : internalValue;

  const allOptions = useMemo<SelectOption[]>(
    () => (placeholder ? [{ value: "", label: placeholder }, ...options] : options),
    [placeholder, options],
  );

  const selectedOption = useMemo(
    () => options.find((opt) => opt.value === selectedValue),
    [options, selectedValue],
  );

  useEffect(() => {
    if (disabled && open) setOpen(false);
  }, [disabled, open]);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  const selectValue = (next: string) => {
    if (!isControlled) setInternalValue(next);
    onChange?.(next);
    setOpen(false);
  };

  const toggleOpen = () => {
    if (disabled) return;
    setOpen((prev) => {
      const next = !prev;
      if (next) {
        const idx = allOptions.findIndex((opt) => opt.value === selectedValue);
        setFocusedIndex(idx >= 0 ? idx : 0);
      }
      return next;
    });
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLButtonElement>) => {
    if (disabled) return;
    switch (e.key) {
      case "Enter":
      case " ":
        e.preventDefault();
        if (!open) {
          toggleOpen();
        } else if (focusedIndex >= 0 && focusedIndex < allOptions.length) {
          selectValue(allOptions[focusedIndex].value);
        }
        break;
      case "Escape":
        if (open) {
          e.preventDefault();
          setOpen(false);
        }
        break;
      case "Tab":
        if (open) setOpen(false);
        break;
      case "ArrowDown":
        e.preventDefault();
        if (!open) {
          toggleOpen();
        } else if (allOptions.length > 0) {
          setFocusedIndex((prev) => (prev < allOptions.length - 1 ? prev + 1 : 0));
        }
        break;
      case "ArrowUp":
        e.preventDefault();
        if (!open) {
          toggleOpen();
        } else if (allOptions.length > 0) {
          setFocusedIndex((prev) => (prev > 0 ? prev - 1 : allOptions.length - 1));
        }
        break;
    }
  };

  const displayLabel = selectedOption ? selectedOption.label : (placeholder ?? "");

  return (
    <div ref={wrapperRef} className="relative w-full">
      <button
        type="button"
        disabled={disabled}
        role="combobox"
        aria-expanded={open}
        aria-haspopup="listbox"
        onClick={toggleOpen}
        onKeyDown={handleKeyDown}
        className={`flex h-11 w-full items-center justify-between rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 pr-11 text-left text-sm shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:focus:border-brand-800 ${
          open ? "border-brand-300 ring-3 ring-brand-500/10 dark:border-brand-800" : ""
        } ${
          selectedOption
            ? "text-gray-800 dark:text-white/90"
            : "text-gray-400 dark:text-gray-400"
        } ${
          disabled
            ? "text-gray-500 border-gray-300 opacity-40 bg-gray-100 cursor-not-allowed dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700"
            : "cursor-pointer"
        } ${className}`}
      >
        <span className="block truncate">{displayLabel}</span>
        <span className="pointer-events-none absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-400">
          <ChevronDownIcon
            className={`size-5 transition-transform duration-150 ${open ? "rotate-180" : ""}`}
          />
        </span>
      </button>

      {open && allOptions.length > 0 && (
        <div
          role="listbox"
          className="custom-scrollbar absolute left-0 top-full z-50 mt-1 max-h-60 w-full overflow-y-auto rounded-lg border border-gray-200 bg-white py-1 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900"
        >
          {allOptions.map((option, index) => {
            const isSelected = option.value === selectedValue;
            const isFocused = index === focusedIndex;
            const isPlaceholder = option.value === "" && placeholder !== undefined;
            return (
              <button
                key={`${option.value}-${index}`}
                type="button"
                role="option"
                aria-selected={isSelected}
                onMouseEnter={() => setFocusedIndex(index)}
                onClick={() => selectValue(option.value)}
                className={`flex w-full items-center justify-between px-4 py-2 text-left text-sm transition-colors ${
                  isSelected
                    ? "bg-brand-50 font-medium text-brand-500 dark:bg-brand-500/[0.12] dark:text-brand-400"
                    : isPlaceholder
                      ? "text-gray-400 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-white/5"
                      : "text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-white/5"
                } ${isFocused && !isSelected ? "bg-gray-100 dark:bg-white/5" : ""}`}
              >
                <span className="truncate">{option.label}</span>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Select;
