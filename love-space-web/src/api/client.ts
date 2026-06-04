import axios, { AxiosError, AxiosInstance } from "axios";

const TOKEN_KEY = "love-space:token";

export function getStoredToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token: string | null): void {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

/**
 * 创建运营后台 API axios 实例。
 * - baseURL 取自 VITE_ADMIN_API_BASE
 * - 请求拦截器自动附加 Authorization: Bearer <token>
 * - 401 响应自动清空 token 并跳转 /signin
 */
function createClient(): AxiosInstance {
  const baseURL = import.meta.env.VITE_ADMIN_API_BASE ?? "http://localhost:8080";
  const instance = axios.create({ baseURL, timeout: 15000 });

  instance.interceptors.request.use((config) => {
    const token = getStoredToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  instance.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      // 后端错误体统一为 { status, error, message, path }（见 GlobalExceptionHandler）。
      // 前端历史代码读取 response.data.detail，这里把后端返回的 message 同步到 detail，
      // 使所有错误弹窗都能展示后端返回的真实 message。
      const data = error.response?.data;
      if (data && typeof data === "object" && !Array.isArray(data)) {
        const body = data as Record<string, unknown>;
        if (body.detail == null && typeof body.message === "string") {
          body.detail = body.message;
        }
      }

      if (error.response?.status === 401) {
        // 登录接口自身的 401（用户名/密码错误、账号停用）交给登录表单展示中文提示，
        // 不在此处全局跳转，否则会抢先导航导致错误提示来不及显示。
        const reqUrl = error.config?.url ?? "";
        const isLoginRequest = reqUrl.includes("/auth/login");
        if (!isLoginRequest) {
          setStoredToken(null);
          if (typeof window !== "undefined") {
            // 子路径部署时 basename 为 import.meta.env.BASE_URL（默认 "/love-space/"），
            // 硬跳转必须带上该前缀，否则会命中 dev server 的 404。
            const signInPath = `${import.meta.env.BASE_URL}signin`.replace(/\/{2,}/g, "/");
            if (window.location.pathname !== signInPath) {
              window.location.href = signInPath;
            }
          }
        }
      }
      return Promise.reject(error);
    },
  );

  return instance;
}

export const apiClient = createClient();
