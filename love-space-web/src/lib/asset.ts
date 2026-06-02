/**
 * 拼接 public 目录下静态资源的 URL，自动带上 Vite 的 base 前缀。
 *
 * 部署在子路径（如 `/love-space/`）时，直接写绝对路径 `/images/x.svg` 会丢掉 base 前缀导致 404；
 * 用本函数包一层即可：`asset("images/logo/logo.svg")` → `/love-space/images/logo/logo.svg`。
 *
 * `import.meta.env.BASE_URL` 末尾固定带斜杠（默认 `/`，本项目为 `/love-space/`）。
 */
export function asset(path: string): string {
  return import.meta.env.BASE_URL + path.replace(/^\/+/, "");
}
