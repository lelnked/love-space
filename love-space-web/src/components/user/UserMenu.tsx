import { useNavigate } from "react-router";
import { useAuth } from "../../hooks/useAuth";

/**
 * 顶部当前用户下拉：展示昵称 / 用户名 + 角色徽标，并提供退出登录入口。
 */
export default function UserMenu() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const onLogout = () => {
    logout();
    navigate("/signin", { replace: true });
  };

  return (
    <div className="flex items-center gap-3">
      <div className="flex flex-col items-end">
        <span className="text-sm font-medium text-gray-900">
          {user.nickname || user.username}
        </span>
        <span className="text-xs text-gray-500">{user.role}</span>
      </div>
      <button
        type="button"
        onClick={onLogout}
        className="text-sm px-3 py-1 rounded border border-gray-300 hover:bg-gray-50"
      >
        退出登录
      </button>
    </div>
  );
}
