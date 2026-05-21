import { useCallback, useEffect, useState } from "react";
import { AxiosError } from "axios";
import {
  CategoryItem,
  createCategory,
  deleteCategory,
  listCategories,
  updateCategory,
} from "../../api/categories";

function formatDateTime(value: string): string {
  try {
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
  } catch {
    return value;
  }
}

export default function CategoryList() {
  const [items, setItems] = useState<CategoryItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [newName, setNewName] = useState("");
  const [creating, setCreating] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingName, setEditingName] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listCategories();
      // 按 createdAt DESC 排序
      const sorted = [...data].sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
      setItems(sorted);
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      setError(ax.response?.data?.detail ?? "加载失败");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const handleCreate = async () => {
    const name = newName.trim();
    if (!name) {
      alert("请输入分类名称");
      return;
    }
    setCreating(true);
    try {
      await createCategory({ name });
      setNewName("");
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "创建失败");
    } finally {
      setCreating(false);
    }
  };

  const startEdit = (it: CategoryItem) => {
    setEditingId(it.id);
    setEditingName(it.name);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditingName("");
  };

  const saveEdit = async (it: CategoryItem) => {
    const name = editingName.trim();
    if (!name) {
      alert("名称不能为空");
      return;
    }
    try {
      await updateCategory(it.id, { name });
      cancelEdit();
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "保存失败");
    }
  };

  const handleDelete = async (it: CategoryItem) => {
    if (!window.confirm(`确认删除分类「${it.name}」？\n注意：删除会下架该分类下所有商户。`)) return;
    try {
      await deleteCategory(it.id);
      await load();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      alert(ax.response?.data?.detail ?? "删除失败");
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">分类管理</h1>

      <div className="flex items-center gap-2 mb-4">
        <input
          type="text"
          placeholder="新增分类名称（≤30 字符）"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          maxLength={30}
          className="border rounded px-3 py-2 text-sm min-w-[260px]"
        />
        <button
          type="button"
          onClick={handleCreate}
          disabled={creating}
          className="px-4 py-2 text-sm rounded bg-brand-500 text-white hover:bg-brand-600 disabled:opacity-50"
        >
          {creating ? "提交中..." : "新增"}
        </button>
      </div>

      {error && <div className="text-error-500 text-sm mb-2">{error}</div>}

      <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-800">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-800 text-left text-gray-500">
            <tr>
              <th className="px-4 py-3">名称</th>
              <th className="px-4 py-3">创建时间</th>
              <th className="px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-gray-500">
                  加载中...
                </td>
              </tr>
            )}
            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-gray-500">
                  暂无数据
                </td>
              </tr>
            )}
            {!loading &&
              items.map((it) => (
                <tr key={it.id} className="border-t border-gray-100 dark:border-gray-800">
                  <td className="px-4 py-3 text-gray-800 dark:text-white/90">
                    {editingId === it.id ? (
                      <input
                        type="text"
                        value={editingName}
                        onChange={(e) => setEditingName(e.target.value)}
                        maxLength={30}
                        className="border rounded px-2 py-1 text-sm"
                      />
                    ) : (
                      it.name
                    )}
                  </td>
                  <td className="px-4 py-3">{formatDateTime(it.createdAt)}</td>
                  <td className="px-4 py-3 space-x-2">
                    {editingId === it.id ? (
                      <>
                        <button
                          type="button"
                          onClick={() => saveEdit(it)}
                          className="px-3 py-1 text-xs rounded bg-brand-500 text-white hover:bg-brand-600"
                        >
                          保存
                        </button>
                        <button
                          type="button"
                          onClick={cancelEdit}
                          className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                        >
                          取消
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          type="button"
                          onClick={() => startEdit(it)}
                          className="px-3 py-1 text-xs rounded border border-gray-300 hover:bg-gray-50"
                        >
                          编辑
                        </button>
                        <button
                          type="button"
                          onClick={() => handleDelete(it)}
                          className="px-3 py-1 text-xs rounded border border-error-300 text-error-500 hover:bg-error-50"
                        >
                          删除
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
