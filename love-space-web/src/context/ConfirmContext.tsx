import {
  createContext,
  useCallback,
  useContext,
  useRef,
  useState,
  type ReactNode,
} from "react";
import { Modal } from "../components/ui/modal";

interface ConfirmOptions {
  /** 标题，默认「确认操作」。 */
  title?: string;
  /** 正文，支持换行（\n）与富文本。 */
  message: ReactNode;
  /** 确认按钮文案，默认「确定」。 */
  confirmText?: string;
  /** 取消按钮文案，默认「取消」。 */
  cancelText?: string;
  /** 危险操作（如删除）：确认按钮使用红色样式。 */
  danger?: boolean;
}

type ConfirmFn = (options: ConfirmOptions) => Promise<boolean>;

const ConfirmContext = createContext<ConfirmFn | null>(null);

interface InternalState {
  open: boolean;
  options: ConfirmOptions;
}

const EMPTY_OPTIONS: ConfirmOptions = { message: "" };

export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<InternalState>({ open: false, options: EMPTY_OPTIONS });
  const resolver = useRef<((value: boolean) => void) | null>(null);

  const settle = useCallback((result: boolean) => {
    resolver.current?.(result);
    resolver.current = null;
    setState((prev) => ({ ...prev, open: false }));
  }, []);

  const confirm = useCallback<ConfirmFn>((options) => {
    return new Promise<boolean>((resolve) => {
      resolver.current = resolve;
      setState({ open: true, options });
    });
  }, []);

  const { open, options } = state;
  const confirmBtnClass = options.danger
    ? "bg-error-500 hover:bg-error-600"
    : "bg-brand-500 hover:bg-brand-600";

  return (
    <ConfirmContext.Provider value={confirm}>
      {children}
      <Modal
        isOpen={open}
        onClose={() => settle(false)}
        showCloseButton={false}
        showBackdrop={false}
        className="max-w-[420px] m-4 shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
      >
        <div className="w-full rounded-3xl bg-white p-6 dark:bg-gray-900">
          <h4 className="mb-3 text-lg font-semibold text-gray-800 dark:text-white/90">
            {options.title ?? "确认操作"}
          </h4>
          <div className="mb-6 whitespace-pre-line text-sm leading-relaxed text-gray-600 dark:text-gray-400">
            {options.message}
          </div>
          <div className="flex justify-end gap-3">
            <button
              type="button"
              onClick={() => settle(false)}
              className="rounded-lg bg-white px-4 py-2.5 text-sm text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700 dark:hover:bg-gray-700"
            >
              {options.cancelText ?? "取消"}
            </button>
            <button
              type="button"
              onClick={() => settle(true)}
              className={`rounded-lg px-4 py-2.5 text-sm text-white ${confirmBtnClass}`}
            >
              {options.confirmText ?? "确定"}
            </button>
          </div>
        </div>
      </Modal>
    </ConfirmContext.Provider>
  );
}

export function useConfirm(): ConfirmFn {
  const ctx = useContext(ConfirmContext);
  if (!ctx) {
    throw new Error("useConfirm must be used within ConfirmProvider");
  }
  return ctx;
}
