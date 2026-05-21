import { createContext, ReactNode, useCallback, useMemo, useState } from "react";
import { apiClient, getStoredToken, setStoredToken } from "../api/client";

export type Role = "ADMIN" | "MEMBER";

export interface AuthUser {
  id: string;
  username: string;
  nickname?: string | null;
  role: Role;
}

interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  login: (username: string, password: string) => Promise<AuthUser>;
  logout: () => void;
  setUser: (user: AuthUser | null) => void;
}

const STORAGE_USER_KEY = "love-space:user";

function loadStoredUser(): AuthUser | null {
  const raw = localStorage.getItem(STORAGE_USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUserState] = useState<AuthUser | null>(loadStoredUser());
  const [token, setTokenState] = useState<string | null>(getStoredToken());

  const setUser = useCallback((next: AuthUser | null) => {
    setUserState(next);
    if (next) localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(next));
    else localStorage.removeItem(STORAGE_USER_KEY);
  }, []);

  const login = useCallback(async (username: string, password: string): Promise<AuthUser> => {
    const { data } = await apiClient.post<{ token: string; user: AuthUser }>(
      "/api/admin/auth/login",
      { username, password },
    );
    setStoredToken(data.token);
    setTokenState(data.token);
    setUser(data.user);
    return data.user;
  }, [setUser]);

  const logout = useCallback(() => {
    setStoredToken(null);
    setTokenState(null);
    setUser(null);
  }, [setUser]);

  const value = useMemo<AuthContextValue>(() => ({
    user, token, login, logout, setUser,
  }), [user, token, login, logout, setUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
