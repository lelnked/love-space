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
- WEB 测试用远程浏览器，访问地址必须用本机 Tailscale IP（如 `http://100.93.172.18:5173/love-space/`），localhost 不可达。

### 后端地址：`VITE_ADMIN_API_BASE`（构建期固化，别搞错）

Vite 的环境变量在 **build 时**就编译进产物，运行时改不了。一个 mode 对应一个 `.env.<mode>` 文件：

| 用途 | 构建命令 | 读取文件 |
|---|---|---|
| 本机开发 / 本机 Playwright 测试 | `npm run dev` | `.env.local` |
| 部署到测试服务器 | `npm run build -- --mode test` | `.env.test` |
| 部署到生产 | `npm run build` | `.env.production`（mode 默认 production） |

**这些 `.env.*` 都被 `.gitignore` 忽略，不在仓库里**，只提交 `.env.*.example` 模板。
换台机器构建前先 `cp .env.production.example .env.production` 并填值。

`vite.config.ts` 在 `build` 时会校验 `.env.<mode>` 文件存在，缺了直接报错中断——**不要绕过它**。
校验的是文件而非变量取值：`.env.local` 在任何 mode 下都会被加载，只查变量有值会被本机配置蒙混过去，
打出一个连着 `100.100.x.x` 的"生产包"。

优先级（高 → 低）：`.env.<mode>.local` > `.env.<mode>` > `.env.local` > `.env`。
所以本机配置放 `.env.local` 是安全的，`--mode test` / production 构建时会被对应的 `.env.<mode>` 覆盖。

构建后再核一眼产物里的地址，比上线后排查便宜：

```bash
grep -o 'https\?://[^"]*' dist/assets/*.js | grep -m1 admin
```

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
参考实现：`src/pages/Cities/List.tsx`（后端全量返回 + 前端切片）、`src/pages/Merchants/List.tsx`（跳独立表单页 + 行点击进详情）。

## 6. 表单页规范（新增/编辑一律独立路由页）

**字段 5 个及以上的新增/编辑必须是独立路由页，不许做成弹窗。** 弹窗在小屏幕高分辨率机器上会被裁掉一截，且没有回退/刷新/分享链接。字段少于 5 个的轻量表单可以留在弹窗里，见第 7 节。参考实现：`src/pages/Merchants/Form.tsx`、`src/pages/FeaturedCycleItems/Form.tsx`。

### 6.1 操作规范

- 新增与编辑**复用同一个 `Form.tsx`**，靠 `useParams().id` 判断模式：`const editing = Boolean(id)`。
- 路由成对注册在 `App.tsx` 的 `<AppLayout />` 内：`/xxx`、`/xxx/create`、`/xxx/:id/edit`。
- **入口**：列表页「新增」用 `<Link to="/xxx/create">`（不是 `<button onClick={navigate}>`），行内「编辑」按钮用 `navigate(\`/xxx/${it.id}/edit\`)`。
- **数据来源**：编辑页自己 `getXxx(id)` 拉数据，**不要靠 `location.state` 或父级 props 把列表行对象传过去**——那样刷新页面就白屏。加载期间渲染 `加载中...`，拉失败 `toast.error` + `navigate` 回列表。
- 列表页的上下文（当前 Tab、筛选条件）需要带进新增页时走 **query 参数**：`/xxx/create?phase=MENSTRUAL`，表单侧用 `useSearchParams()` 读，并对非法值兜底。
- **提交**：`<form onSubmit={handleSubmit} noValidate>` + `e.preventDefault()`，让回车能提交；提交按钮不写 `onClick`，靠 form 默认的 submit。
- 成功后 `toast.success` + `navigate("/xxx")` 回列表；「取消」也是 `navigate("/xxx")`。
- 创建后不可变的字段（所属周期、内容类型这类），编辑态渲染成 `disabled` 控件或只读输入框，并在同一块里用小字说明「创建后不可修改」。
- 校验以后端为准，前端只做必填/长度这类即时提示，不重复实现业务规则。后端 422 的 `errors[]` 按 `field` 映射进 `fieldErrors`，逐字段展示。

### 6.2 样式规范

外壳固定这三层，不要套 `ComponentCard`（那是列表页的皮）：

```tsx
<div>
  <PageMeta title={`${editing ? "编辑" : "新增"}XX | Love Space Admin`} description="..." />
  <h1 className="text-xl font-semibold text-gray-800 dark:text-white/90 mb-4">
    {editing ? "编辑XX" : "新增XX"}
  </h1>
  {loading ? <div className="text-gray-500">加载中...</div> : (
    <form onSubmit={handleSubmit} noValidate className="max-w-4xl space-y-5">
      {/* fieldset 分组 */}
    </form>
  )}
</div>
```

字段按语义分组，每组一个 `fieldset` + `legend`，组内多字段用 `grid grid-cols-1 md:grid-cols-2 gap-4`：

```tsx
const sectionClass = "border border-gray-200 dark:border-gray-800 rounded-lg p-4 bg-white dark:bg-gray-900";
const sectionTitleClass = "text-sm font-semibold text-gray-800 dark:text-white/90 mb-3";
const inputClass = "border rounded px-3 py-2 text-sm w-full h-11";
const selectClass = "border rounded px-3 py-2 text-sm w-full h-11 disabled:bg-gray-100 disabled:text-gray-500";
const textareaClass = "border rounded px-3 py-2 text-sm w-full min-h-[80px]";
```

- 必填标记：`<Label>名称 <span className="text-error-500">*</span></Label>`。
- 字段级错误：`<div className="text-error-500 text-xs mt-1">`，紧跟控件；辅助说明用 `text-xs text-gray-400 mt-1`。
- 底部动作条固定为「提交 + 取消」，提交用 `<Button size="sm" disabled={submitting}>`（文案 `提交中... / 保存 / 创建`），取消是原生 button：

```tsx
<button type="button" onClick={() => navigate("/xxx")} disabled={submitting}
  className="px-4 py-3 text-sm rounded-lg bg-white text-gray-700 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-400 dark:ring-gray-700">
  取消
</button>
```

## 7. 弹窗规范（仅限轻量交互）

**弹窗只用于确认框和字段少于 5 个的轻量表单**（`useConfirm()`、改密码这类）。字段数到 5 个就必须拆成第 6 节的独立路由页——按最终表单的字段总数算，不是按当前迭代加了几个。

真要用弹窗时，用无遮罩卡片风格，参考 `Managers` 页「新增管理员」：

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
