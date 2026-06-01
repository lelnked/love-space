import { FormEvent, useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import FilterBar, { FilterField, FilterValues } from "../../components/filter/FilterBar";
import Pagination from "../../components/pagination/Pagination";
import { Modal } from "../../components/ui/modal";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import PageMeta from "../../components/common/PageMeta";
import ComponentCard from "../../components/common/ComponentCard";
import Badge from "../../components/ui/badge/Badge";
import { useToast } from "../../context/ToastContext";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "../../components/ui/table";
import {
  createManager,
  disableManager,
  enableManager,
  pageManagers,
  resetPassword,
  ManagerItem,
  ManagerQuery,
} from "../../api/managers";

interface FieldError {
  field: string;
  message: string;
}

const FILTER_FIELDS: FilterField[] = [
  { name: "username", label: "用户名", type: "text", placeholder: "模糊匹配" },
  {
    name: "role",
    label: "角色",
    type: "select",
    options: [
      { label: "管理员", value: "ADMIN" },
      { label: "成员", value: "MEMBER" },
    ],
  },
  {
    name: "enable",
    label: "状态",
    type: "select",
    options: [
      { label: "启用", value: "true" },
      { label: "停用", value: "false" },
    ],
  },
];

function buildQuery(filters: FilterValues, page: number, size: number): ManagerQuery {
  const q: ManagerQuery = { page, size };
  if (filters.username) q.username = filters.username;
  if (filters.role === "ADMIN" || filters.role === "MEMBER") q.role = filters.role;
  if (filters.enable === "true") q.enable = true;
  else if (filters.enable === "false") q.enable = false;
  return q;
}

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

export default function ManagerList() {
  const [filters, setFilters] = useState<FilterValues>({});
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(20);
  const [items, setItems] = useState<ManagerItem[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  const [createOpen, setCreateOpen] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "reset">("create");
  const [resetTargetId, setResetTargetId] = useState<string | null>(null);
  const [createUsername, setCreateUsername] = useState("");
  const [createPassword, setCreatePassword] = useState("");
  const [createNickname, setCreateNickname] = useState("");
  const [createSubmitting, setCreateSubmitting] = useState(false);
  const [createFieldErrors, setCreateFieldErrors] = useState<Record<string, string>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageManagers(buildQuery(filters, page, size));
      setItems(data.content);
      setTotal(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, [filters, page, size, toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleApply = (values: FilterValues) => {
    setFilters(values);
    setPage(1);
  };

  const handleReset = () => {
    setFilters({});
    setPage(1);
  };

  const handleToggleEnable = async (item: ManagerItem) => {
    try {
      if (item.enable) await disableManager(item.id);
      else await enableManager(item.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(ax.response?.data?.detail ?? "操作失败");
    }
  };

  const handleResetPassword = (item: ManagerItem) => {
    setModalMode("reset");
    setResetTargetId(item.id);
    setCreateUsername(item.username);
    setCreateNickname(item.nickname ?? "");
    setCreatePassword("");
    setCreateFieldErrors({});
    setCreateOpen(true);
  };

  const openCreate = () => {
    setModalMode("create");
    setResetTargetId(null);
    setCreateUsername("");
    setCreatePassword("");
    setCreateNickname("");
    setCreateFieldErrors({});
    setCreateOpen(true);
  };

  const closeCreate = () => {
    if (createSubmitting) return;
    setCreateOpen(false);
  };

  const handleCreateSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (createSubmitting) return;
    setCreateFieldErrors({});

    if (createPassword.length < 8) {
      setCreateFieldErrors({ password: "密码至少 8 位" });
      return;
    }
    if (modalMode === "create" && !createUsername.trim()) {
      setCreateFieldErrors({ username: "用户名不能为空" });
      return;
    }

    setCreateSubmitting(true);
    try {
      if (modalMode === "reset") {
        if (resetTargetId == null) throw new Error("缺少目标管理员");
        await resetPassword(resetTargetId, { newPassword: createPassword });
      } else {
        await createManager({
          username: createUsername.trim(),
          password: createPassword,
          nickname: createNickname.trim() || undefined,
        });
      }
      setCreateOpen(false);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string; errors?: FieldError[] }>;
      const data = ax.response?.data;
      if (data?.errors?.length) {
        const map: Record<string, string> = {};
        for (const fe of data.errors) map[fe.field] = fe.message;
        setCreateFieldErrors(map);
      }
      toast.error(data?.detail ?? (modalMode === "reset" ? "重置失败" : "创建失败"));
    } finally {
      setCreateSubmitting(false);
    }
  };

  return (
    <>
      <PageMeta title="管理员管理 | Love Space Admin" description="管理员账号列表与管理" />
      <div className="flex items-center justify-end mb-6">
        <button
          type="button"
          onClick={openCreate}
          className="px-4 py-2 text-sm rounded-lg bg-brand-500 text-white hover:bg-brand-600"
        >
          新增管理员
        </button>
      </div>
      <div className="space-y-6">
        <ComponentCard title="管理员列表">
          <FilterBar
            fields={FILTER_FIELDS}
            initialValues={filters}
            onApply={handleApply}
            onReset={handleReset}
          />

          <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
            <div className="max-w-full overflow-x-auto">
              <Table>
                <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                  <TableRow>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      用户名
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      昵称
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      角色
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      状态
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      创建时间
                    </TableCell>
                    <TableCell isHeader className="px-5 py-3 font-medium text-gray-500 text-start text-theme-xs dark:text-gray-400">
                      操作
                    </TableCell>
                  </TableRow>
                </TableHeader>

                <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                  {loading && (
                    <TableRow>
                      <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                        <div className="col-span-6">加载中...</div>
                      </TableCell>
                    </TableRow>
                  )}
                  {!loading && items.length === 0 && (
                    <TableRow>
                      <TableCell className="px-5 py-6 text-center text-gray-500 text-theme-sm dark:text-gray-400">
                        <div className="col-span-6">暂无数据</div>
                      </TableCell>
                    </TableRow>
                  )}
                  {!loading &&
                    items.map((it) => (
                      <TableRow key={it.id}>
                        <TableCell className="px-5 py-4 sm:px-6 text-start font-medium text-gray-800 text-theme-sm dark:text-white/90">
                          {it.username}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.nickname ?? "-"}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {it.role === "ADMIN" ? "管理员" : "成员"}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <Badge size="sm" color={it.enable ? "success" : "error"}>
                            {it.enable ? "启用" : "停用"}
                          </Badge>
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-500 text-start text-theme-sm dark:text-gray-400">
                          {formatDateTime(it.createdAt)}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-start text-theme-sm">
                          <div className="flex gap-2">
                            {it.username !== "admin" && (
                              <Button size="sm" variant="primary" onClick={() => handleToggleEnable(it)}>
                                {it.enable ? "停用" : "启用"}
                              </Button>
                            )}
                            <Button size="sm" variant="primary" onClick={() => handleResetPassword(it)}>
                              重置密码
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </div>
          </div>

          <Pagination
            page={page}
            size={size}
            total={total}
            totalPages={totalPages}
            onChange={({ page: nextPage, size: nextSize }) => {
              setPage(nextPage);
              setSize(nextSize);
            }}
          />
        </ComponentCard>
      </div>

      <Modal
        isOpen={createOpen}
        onClose={closeCreate}
        showBackdrop={false}
        className="max-w-[520px] m-4 -translate-y-[100px] shadow-2xl ring-1 ring-gray-200 dark:ring-gray-800"
      >
        <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8">
          <h4 className="mb-5 text-xl font-semibold text-gray-800 dark:text-white/90">
            {modalMode === "reset" ? "重置密码" : "新增管理员"}
          </h4>
          <form onSubmit={handleCreateSubmit} className="space-y-5">
            <div>
              <Label>
                用户名 <span className="text-error-500">*</span>
              </Label>
              <Input
                placeholder="登录用户名"
                value={createUsername}
                onChange={(e) => setCreateUsername(e.target.value)}
                disabled={modalMode === "reset"}
                error={Boolean(createFieldErrors.username)}
                hint={createFieldErrors.username}
              />
            </div>
            <div>
              <Label>
                密码 <span className="text-error-500">*</span>
              </Label>
              <Input
                type="password"
                placeholder="至少 8 位"
                value={createPassword}
                onChange={(e) => setCreatePassword(e.target.value)}
                error={Boolean(createFieldErrors.password)}
                hint={createFieldErrors.password}
              />
            </div>
            <div>
              <Label>昵称</Label>
              <Input
                placeholder="可选"
                value={createNickname}
                onChange={(e) => setCreateNickname(e.target.value)}
                disabled={modalMode === "reset"}
                error={Boolean(createFieldErrors.nickname)}
                hint={createFieldErrors.nickname}
              />
            </div>

            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={closeCreate}
                disabled={createSubmitting}
                className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
              >
                取消
              </button>
              <Button size="sm" disabled={createSubmitting}>
                {createSubmitting
                  ? "提交中..."
                  : modalMode === "reset"
                  ? "重置"
                  : "创建"}
              </Button>
            </div>
          </form>
        </div>
      </Modal>
    </>
  );
}
