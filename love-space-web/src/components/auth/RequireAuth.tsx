import { ReactNode } from "react";
import { Navigate, useLocation } from "react-router";
import { useAuth } from "../../hooks/useAuth";

/**
 * 守卫：未登录用户访问受保护路由时跳转 /signin。
 */
export default function RequireAuth({ children }: { children: ReactNode }) {
  const { token, user } = useAuth();
  const location = useLocation();
  if (!token || !user) {
    return <Navigate to="/signin" replace state={{ from: location }} />;
  }
  return <>{children}</>;
}
