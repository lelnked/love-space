interface PaginationProps {
  page: number;        // 1 基
  size: number;        // 当前每页大小
  total: number;       // 总条数
  totalPages: number;  // 总页数
  onChange: (next: { page: number; size: number }) => void;
}

const SIZE_OPTIONS = [20, 30];

/**
 * support-tickets 风格的分页器：右下角放置，含上一页 / 下一页 / 页码 / 每页大小切换。
 */
export default function Pagination({ page, size, total, totalPages, onChange }: PaginationProps) {
  const safeTotalPages = Math.max(1, totalPages);

  return (
    <div className="flex items-center justify-end gap-3 mt-4 text-sm text-gray-600">
      <span>
        共 {total} 条 · 第 {page} / {safeTotalPages} 页
      </span>
      <select
        className="border rounded px-2 py-1"
        value={size}
        onChange={(e) => onChange({ page: 1, size: Number(e.target.value) })}
      >
        {SIZE_OPTIONS.map((s) => (
          <option key={s} value={s}>{s}/页</option>
        ))}
      </select>
      <button
        type="button"
        disabled={page <= 1}
        onClick={() => onChange({ page: page - 1, size })}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        上一页
      </button>
      <button
        type="button"
        disabled={page >= safeTotalPages}
        onClick={() => onChange({ page: page + 1, size })}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        下一页
      </button>
    </div>
  );
}
