interface PaginationProps {
  page: number;
  size: number;
  total: number;
  totalPages: number;
  onChange: (next: { page: number; size: number }) => void;
}

function buildPageList(current: number, totalPages: number): (number | "...")[] {
  if (totalPages <= 7) {
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }
  const pages: (number | "...")[] = [1];
  const start = Math.max(2, current - 1);
  const end = Math.min(totalPages - 1, current + 1);
  if (start > 2) pages.push("...");
  for (let p = start; p <= end; p++) pages.push(p);
  if (end < totalPages - 1) pages.push("...");
  pages.push(totalPages);
  return pages;
}

export default function Pagination({ page, size, total, totalPages, onChange }: PaginationProps) {
  const safeTotalPages = Math.max(1, totalPages);
  const from = total === 0 ? 0 : (page - 1) * size + 1;
  const to = Math.min(page * size, total);
  const pageList = buildPageList(page, safeTotalPages);

  const goTo = (p: number) => {
    if (p < 1 || p > safeTotalPages || p === page) return;
    onChange({ page: p, size });
  };

  return (
    <div className="flex flex-col items-center justify-between border-t border-gray-200 px-5 py-4 sm:flex-row dark:border-gray-800">
      <div className="pb-3 sm:pb-0">
        <span className="block text-sm font-medium text-gray-500 dark:text-gray-400">
          显示{" "}
          <span className="text-gray-800 dark:text-white/90">{from}</span>
          {" - "}
          <span className="text-gray-800 dark:text-white/90">{to}</span>
          {" 条，共 "}
          <span className="text-gray-800 dark:text-white/90">{total}</span>
          {" 条"}
        </span>
      </div>
      <div className="flex w-full items-center justify-between gap-2 rounded-lg bg-gray-50 p-4 sm:w-auto sm:justify-normal sm:bg-transparent sm:p-0 dark:bg-white/[0.03] dark:sm:bg-transparent">
        <button
          type="button"
          onClick={() => goTo(page - 1)}
          disabled={page <= 1}
          className="shadow-theme-xs flex items-center gap-2 rounded-lg border border-gray-300 bg-white p-2 text-gray-700 hover:bg-gray-50 hover:text-gray-800 disabled:cursor-not-allowed disabled:opacity-50 sm:p-2.5 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:hover:bg-white/[0.03] dark:hover:text-gray-200"
        >
          <svg className="fill-current" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path fillRule="evenodd" clipRule="evenodd" d="M2.58203 9.99868C2.58174 10.1909 2.6549 10.3833 2.80152 10.53L7.79818 15.5301C8.09097 15.8231 8.56584 15.8233 8.85883 15.5305C9.15183 15.2377 9.152 14.7629 8.85921 14.4699L5.13911 10.7472L16.6665 10.7472C17.0807 10.7472 17.4165 10.4114 17.4165 9.99715C17.4165 9.58294 17.0807 9.24715 16.6665 9.24715L5.14456 9.24715L8.85919 5.53016C9.15199 5.23717 9.15184 4.7623 8.85885 4.4695C8.56587 4.1767 8.09099 4.17685 7.79819 4.46984L2.84069 9.43049C2.68224 9.568 2.58203 9.77087 2.58203 9.99715C2.58203 9.99766 2.58203 9.99817 2.58203 9.99868Z" />
          </svg>
        </button>
        <span className="block text-sm font-medium text-gray-700 sm:hidden dark:text-gray-400">
          第 <span>{page}</span> / <span>{safeTotalPages}</span> 页
        </span>
        <ul className="hidden items-center gap-0.5 sm:flex">
          {pageList.map((p, idx) =>
            p === "..." ? (
              <li key={`ellipsis-${idx}`}>
                <span className="flex h-10 w-10 items-center justify-center text-sm font-medium text-gray-500 dark:text-gray-400">
                  ...
                </span>
              </li>
            ) : (
              <li key={p}>
                <button
                  type="button"
                  onClick={() => goTo(p)}
                  className={
                    p === page
                      ? "bg-brand-500 hover:bg-brand-500 flex h-10 w-10 items-center justify-center rounded-lg text-sm font-medium text-white hover:text-white"
                      : "hover:bg-brand-500 flex h-10 w-10 items-center justify-center rounded-lg text-sm font-medium text-gray-700 hover:text-white dark:text-gray-400 dark:hover:text-white"
                  }
                >
                  {p}
                </button>
              </li>
            ),
          )}
        </ul>
        <button
          type="button"
          onClick={() => goTo(page + 1)}
          disabled={page >= safeTotalPages}
          className="shadow-theme-xs flex items-center gap-2 rounded-lg border border-gray-300 bg-white p-2 text-gray-700 hover:bg-gray-50 hover:text-gray-800 disabled:cursor-not-allowed disabled:opacity-50 sm:p-2.5 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:hover:bg-white/[0.03] dark:hover:text-gray-200"
        >
          <svg className="fill-current" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path fillRule="evenodd" clipRule="evenodd" d="M17.4165 9.9986C17.4168 10.1909 17.3437 10.3832 17.197 10.53L12.2004 15.5301C11.9076 15.8231 11.4327 15.8233 11.1397 15.5305C10.8467 15.2377 10.8465 14.7629 11.1393 14.4699L14.8594 10.7472L3.33203 10.7472C2.91782 10.7472 2.58203 10.4114 2.58203 9.99715C2.58203 9.58294 2.91782 9.24715 3.33203 9.24715L14.854 9.24715L11.1393 5.53016C10.8465 5.23717 10.8467 4.7623 11.1397 4.4695C11.4327 4.1767 11.9075 4.17685 12.2003 4.46984L17.1578 9.43049C17.3163 9.568 17.4165 9.77087 17.4165 9.99715C17.4165 9.99763 17.4165 9.99812 17.4165 9.9986Z" />
          </svg>
        </button>
      </div>
    </div>
  );
}
