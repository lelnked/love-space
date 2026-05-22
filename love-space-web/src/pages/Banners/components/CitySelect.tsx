import { useEffect, useMemo, useRef, useState } from "react";
import { CityItem, listOnlineCities } from "../../../api/cities";

interface CitySelectProps {
  value: string | null;
  onChange: (cityId: string, city: CityItem) => void;
  disabled?: boolean;
  error?: boolean;
  hint?: string;
}

export default function CitySelect({ value, onChange, disabled, error, hint }: CitySelectProps) {
  const [cities, setCities] = useState<CityItem[]>([]);
  const [keyword, setKeyword] = useState("");
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    listOnlineCities()
      .then((data) => {
        if (!cancelled) setCities(data);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    const onDocClick = (e: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", onDocClick);
    return () => document.removeEventListener("mousedown", onDocClick);
  }, []);

  const filtered = useMemo(() => {
    const kw = keyword.trim().toLowerCase();
    if (!kw) return cities;
    return cities.filter(
      (c) =>
        c.chineseName.toLowerCase().includes(kw) || c.englishName.toLowerCase().includes(kw),
    );
  }, [cities, keyword]);

  const selected = cities.find((c) => c.id === value) ?? null;
  const display = selected ? `${selected.chineseName} (${selected.englishName})` : "";

  return (
    <div ref={wrapperRef} className="relative">
      <input
        type="text"
        readOnly={!open}
        disabled={disabled}
        value={open ? keyword : display}
        placeholder={loading ? "加载城市中..." : "选择城市（支持搜索中文/英文名）"}
        onFocus={() => {
          setOpen(true);
          setKeyword("");
        }}
        onChange={(e) => setKeyword(e.target.value)}
        className={`w-full px-3 py-2 text-sm rounded-lg border bg-white dark:bg-gray-900 ${
          error
            ? "border-error-500 focus:border-error-500"
            : "border-gray-300 dark:border-gray-700 focus:border-brand-500"
        } focus:outline-none`}
      />
      {hint && (
        <p className={`mt-1 text-xs ${error ? "text-error-500" : "text-gray-500"}`}>{hint}</p>
      )}

      {open && (
        <div className="absolute z-10 mt-1 w-full max-h-64 overflow-y-auto rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 shadow-lg">
          {filtered.length === 0 ? (
            <div className="px-3 py-2 text-sm text-gray-500">未找到匹配的城市</div>
          ) : (
            filtered.map((c) => (
              <button
                key={c.id}
                type="button"
                onClick={() => {
                  onChange(c.id, c);
                  setOpen(false);
                  setKeyword("");
                }}
                className="block w-full text-left px-3 py-2 text-sm hover:bg-gray-50 dark:hover:bg-gray-800"
              >
                <span className="text-gray-800 dark:text-white/90">{c.chineseName}</span>
                <span className="ml-2 text-xs text-gray-500">{c.englishName}</span>
              </button>
            ))
          )}
        </div>
      )}
    </div>
  );
}
