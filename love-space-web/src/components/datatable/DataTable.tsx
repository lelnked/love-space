import { ReactNode } from "react";

export type ColumnAlign = "start" | "center" | "end";

export interface Column<T> {
  /** 唯一键；缺省 render 时按 row[key] 取值 */
  key: string;
  /** 表头内容 */
  header: ReactNode;
  /** 单元格渲染函数；缺省渲染 (row as Record)[key] */
  render?: (row: T) => ReactNode;
  /** 配合 table-fixed 固定列宽，如 "12rem" / "20%" */
  width?: string;
  /** 文本对齐，默认 start */
  align?: ColumnAlign;
  /** 单元格额外 className */
  className?: string;
  /** 表头额外 className */
  headerClassName?: string;
}

export interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  /** 是否处于加载态：有旧数据时叠遮罩、无数据时显示骨架 */
  loading?: boolean;
  /** 空数据文案，默认「暂无数据」 */
  emptyText?: ReactNode;
  /** 整行点击（如跳转详情） */
  onRowClick?: (row: T) => void;
  /** 每行额外 className（可按行计算） */
  rowClassName?: (row: T) => string;
  /** 首次加载骨架行数，默认 8 */
  skeletonRows?: number;
  /** 表体最小高度，防止数据少时坍缩，默认 320px */
  minBodyHeight?: number | string;
}

const ALIGN_CLASS: Record<ColumnAlign, string> = {
  start: "text-start",
  center: "text-center",
  end: "text-end",
};

const HEADER_CELL =
  "px-5 py-3 font-medium text-gray-500 text-theme-xs dark:text-gray-400";
const BODY_CELL = "px-5 py-4 text-gray-700 text-theme-sm dark:text-gray-300";

export default function DataTable<T>({
  columns,
  rows,
  rowKey,
  loading = false,
  emptyText = "暂无数据",
  onRowClick,
  rowClassName,
  skeletonRows = 8,
  minBodyHeight = 320,
}: DataTableProps<T>) {
  const colCount = columns.length;
  // 有旧数据时刷新 => 叠遮罩保留旧行；无数据时加载 => 骨架
  const showOverlay = loading && rows.length > 0;
  const showSkeleton = loading && rows.length === 0;
  const showEmpty = !loading && rows.length === 0;

  return (
    <div className="relative overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
      <div
        className="max-w-full overflow-x-auto"
        style={{ minHeight: minBodyHeight }}
      >
        <table className="min-w-full table-fixed">
          <colgroup>
            {columns.map((col) => (
              <col key={col.key} style={col.width ? { width: col.width } : undefined} />
            ))}
          </colgroup>

          <thead className="border-b border-gray-100 dark:border-white/[0.05]">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={`${HEADER_CELL} ${ALIGN_CLASS[col.align ?? "start"]} ${col.headerClassName ?? ""}`}
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
            {showSkeleton &&
              Array.from({ length: skeletonRows }).map((_, rowIdx) => (
                <tr key={`sk-${rowIdx}`}>
                  {columns.map((col) => (
                    <td key={col.key} className={BODY_CELL}>
                      <div className="h-4 w-full max-w-[8rem] animate-pulse rounded bg-gray-100 dark:bg-white/[0.06]" />
                    </td>
                  ))}
                </tr>
              ))}

            {showEmpty && (
              <tr>
                <td
                  colSpan={colCount}
                  className="px-5 py-10 text-center text-gray-500 text-theme-sm dark:text-gray-400"
                >
                  {emptyText}
                </td>
              </tr>
            )}

            {!showSkeleton &&
              !showEmpty &&
              rows.map((row) => (
                <tr
                  key={rowKey(row)}
                  onClick={onRowClick ? () => onRowClick(row) : undefined}
                  className={`${onRowClick ? "cursor-pointer hover:bg-gray-50 dark:hover:bg-white/[0.03]" : ""} ${rowClassName?.(row) ?? ""}`}
                >
                  {columns.map((col) => (
                    <td
                      key={col.key}
                      className={`${BODY_CELL} ${ALIGN_CLASS[col.align ?? "start"]} ${col.className ?? ""}`}
                    >
                      {col.render
                        ? col.render(row)
                        : ((row as Record<string, unknown>)[col.key] as ReactNode)}
                    </td>
                  ))}
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {showOverlay && (
        <div className="absolute inset-0 z-10 flex items-start justify-center bg-white/60 pt-24 dark:bg-black/40">
          <div className="flex items-center gap-2 text-gray-500 text-theme-sm dark:text-gray-400">
            <span className="h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-brand-500" />
            加载中...
          </div>
        </div>
      )}
    </div>
  );
}
