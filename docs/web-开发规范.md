# love-space-web 开发规范手册

> 运营管理后台**前端**（React 19 + TypeScript + Vite 6 + Tailwind CSS v4，从 TailAdmin 模板衍生）。
> 只对接 admin 后端（`/api/admin/**`），**不要**直连 app 后端。

## 1. 命令与环境

```bash
cd love-space-web
npm install            # 必须 NODE_ENV=development，否则 devDependencies 被剥掉（tsc 会消失）
npm run dev            # 本地
npm run dev -- --host  # 远程 Playwright 测试必须带 --host
npm run build          # tsc -b && vite build
npm run lint
```

- 部署在子路径 **`/love-space/`**：`<Router basename="/love-space">`，硬跳转要带 `import.meta.env.BASE_URL`。
- 后端地址走 `VITE_ADMIN_API_BASE`（`.env.local.example` / `.env.test`），默认 `http://localhost:8080`。
- WEB 测试用远程浏览器，访问地址必须用本机 Tailscale IP（如 `http://100.93.172.18:5173/love-space/`），localhost 不可达。

## 2. 目录约定

```
src/
├── api/          每个后端模块一个文件（cities.ts / merchants.ts …）+ client.ts + types.ts
├── pages/<模块>/ List.tsx（列表）、Form.tsx（新增/编辑复用同一个组件）
├── components/   filter/FilterBar、pagination/Pagination、datatable/DataTable、
│                 form/（ImageUploader、RichTextEditor、MultiSelect、Select …）、ui/（modal、badge、button …）
├── context/      AuthContext、ToastContext、ConfirmContext、ThemeContext、SidebarContext
├── layout/       AppLayout（外壳）、AppSidebar（菜单注册处）
├── hooks/        useAuth、useModal、useGoBack
├── lib/          ossUpload（OSS 直传）、asset
└── types/        跨模块共享类型（image 等）
```

模板遗留的演示页（Calendar / Charts / UiElements / Forms / Tables）是脚手架，**不是产品功能**，可以直接删或替换，不要照抄它们的写法。

- 路由：`react-router` v7 + `BrowserRouter`。需登录的页面嵌在 `<AppLayout />`（侧栏+顶栏外壳）内，`/signin` 在外；末尾有 catch-all 404。
- 样式：Tailwind v4 经 `@tailwindcss/postcss`，**没有 `tailwind.config.js`**，配置写在 `src/index.css` 里（v4 惯例）。
- 图标：`src/icons/` 经 SVGR 以组件方式 import。
- Node 18+，推荐 20+。

## 3. API 层

每个模块一个文件，只导出类型 + 函数，不含 React 逻辑：

```ts
export interface CityItem { id: string; chineseName: string; backgroundImage: ImageResponse | null; online: boolean; createdAt: string; ... }
export interface CityQuery { online?: boolean | ""; name?: string }
export interface CityUpsertRequest { chineseName: string; backgroundImage?: string | null; ... }

export async function listCities(query: CityQuery = {}): Promise<CityItem[]> {
  const { data } = await apiClient.get<CityItem[]>("/api/admin/cities", { params: buildParams(query) });
  return data;
}
```

- 一律用 `apiClient`（`api/client.ts`）：自动带 `Authorization: Bearer <token>`，401 自动清 token 跳 `/signin`（登录接口自身的 401 除外）。**不要另建 axios 实例、不要用裸 fetch。**
- 命名：`listXxx` / `getXxx` / `createXxx` / `updateXxx` / `deleteXxx` / `setXxxOnline`。
- 分页出参统一用 `api/types.ts` 的 `Page<T>`（`page` 是 **1 基**）。
- 字段名与后端 DTO **逐字对齐**，不在前端改名、不缩写。

## 4. 错误提示

后端错误体是 `{status, error, message, path}`，`client.ts` 已把 `message` 同步到 `detail`。页面统一这样取：

```ts
const ax = err as AxiosError<{ detail?: string }>;
toast.error(ax.response?.data?.detail ?? "加载失败");
```

后端返回的 message 已经是中文用户话术，**直接展示，不要自己再编一套文案覆盖它**。

## 5. 列表页规范

必须由这三件套组成，顺序固定：

1. **`FilterBar`** — 顶部筛选，字段用 `FilterField[]` 声明（`text` / `select` / `date`），自带 **Apply / Reset**。
2. **`DataTable`** — 列用 `Column[]` 声明。
3. **`Pagination`** — 放在**表格右下角**，props `{page, size, total, totalPages, onChange}`；默认每页 **20**，可切 **30**（与后端 size 白名单一致）。

外层套 `ComponentCard`，页面顶部放 `PageMeta`。删除等破坏性操作走 `useConfirm()`，结果反馈走 `useToast()`。
参考实现：`src/pages/Cities/List.tsx`（后端全量返回 + 前端切片）、`src/pages/Managers/List.tsx`（弹窗式表单）。

## 6. 表单页规范

- 新增与编辑**复用同一个 `Form.tsx`**，靠 `useParams().id` 判断模式。
- 路由成对注册：`/xxx`、`/xxx/create`、`/xxx/:id/edit`。
- 校验以后端为准，前端只做必填/长度这类即时提示，不重复实现业务规则。

## 7. 弹窗规范（统一样式）

无遮罩卡片弹窗，参考 `Managers` 页「新增管理员」：

```tsx
<Modal showBackdrop={false} ...>              {/* 必须 showBackdrop={false} */}
  <div className="relative w-full rounded-3xl bg-white p-6 dark:bg-gray-900 lg:p-8 ring-1 ring-gray-200 dark:ring-gray-800">
```

不再使用带遮罩的旧弹窗风格。

## 8. 图片上传

- 用 `components/form/ImageUploader`（单图）/ `ImageUploaderList`（多图），底层是 `lib/ossUpload.ts`：先向 `/api/admin/files/upload-credentials` 要签名，再由浏览器**直传 OSS**。
- 仅支持 `png / jpeg / webp`。
- **提交给后端的是 objectKey**（即 `ImageResponse.id`），不是展示用的签名 URL；回显用后端返回的 `ImageResponse.url`（有时效，不要缓存进 localStorage）。

## 9. 权限与菜单

- 当前用户来自 `AuthContext`（`useAuth()`），角色 `"ADMIN" | "MEMBER"`。
- 菜单在 `layout/AppSidebar.tsx` 注册；受限入口在那里按角色过滤（现有：`/managers` 仅 ADMIN 可见）。
- 前端过滤只是体验，**权限的真正边界在后端**，不要靠隐藏菜单当安全措施。
- 运营账号叫 **Manager**：目录 `src/pages/Managers`、接口 `/api/admin/managers`、登录响应字段 `manager`。**不要用 user 命名。**

## 10. 新增一个页面的清单

1. `src/api/<模块>.ts` — 类型 + 请求函数
2. `src/pages/<模块>/List.tsx`（+ `Form.tsx`）
3. `src/App.tsx` 注册路由（放在 `<AppLayout />` 内）
4. `src/layout/AppSidebar.tsx` 注册菜单项（需要时加角色过滤）
5. `npm run build` 过 tsc

## 11. 交付前

- 后端接口先行：先改 Java controller/DTO，再镜像到前端类型（没有 codegen，靠 `contracts/api-spec.json` 对齐）。见 [admin-开发规范.md](admin-开发规范.md)。
- 行为有变化 → 走 OpenSpec change（见 `.claude/rules/openspec-session-protocol.md`）。
- 跑 WEB 用例：`/run-web-test --change <id>`，用例落 `tests/{domain}/web.md`。
