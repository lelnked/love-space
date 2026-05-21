import { FormEvent, useState } from "react";
import { useNavigate } from "react-router";
import { AxiosError } from "axios";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import Button from "../../components/ui/button/Button";
import { createUser } from "../../api/users";

interface FieldError {
  field: string;
  message: string;
}

export default function UserCreate() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setError(null);
    setFieldErrors({});

    if (!username.trim()) {
      setFieldErrors({ username: "用户名不能为空" });
      return;
    }
    if (password.length < 8) {
      setFieldErrors({ password: "密码至少 8 位" });
      return;
    }

    setSubmitting(true);
    try {
      await createUser({
        username: username.trim(),
        password,
        nickname: nickname.trim() || undefined,
      });
      navigate("/users", { replace: true });
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string; errors?: FieldError[] }>;
      const data = ax.response?.data;
      if (data?.errors?.length) {
        const map: Record<string, string> = {};
        for (const fe of data.errors) map[fe.field] = fe.message;
        setFieldErrors(map);
      }
      setError(data?.detail ?? "创建失败");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
        新增用户
      </h1>

      <form onSubmit={handleSubmit} className="max-w-lg space-y-5">
        <div>
          <Label>
            用户名 <span className="text-error-500">*</span>
          </Label>
          <Input
            placeholder="登录用户名"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            error={Boolean(fieldErrors.username)}
            hint={fieldErrors.username}
          />
        </div>
        <div>
          <Label>
            密码 <span className="text-error-500">*</span>
          </Label>
          <Input
            type="password"
            placeholder="至少 8 位"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={Boolean(fieldErrors.password)}
            hint={fieldErrors.password}
          />
        </div>
        <div>
          <Label>昵称</Label>
          <Input
            placeholder="可选"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            error={Boolean(fieldErrors.nickname)}
            hint={fieldErrors.nickname}
          />
        </div>

        {error && <div className="text-error-500 text-sm">{error}</div>}

        <div className="flex gap-3">
          <Button
            size="sm"
            disabled={submitting}
          >
            {submitting ? "提交中..." : "创建"}
          </Button>
          <button
            type="button"
            onClick={() => navigate("/users")}
            disabled={submitting}
            className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700"
          >
            取消
          </button>
        </div>
      </form>
    </div>
  );
}
