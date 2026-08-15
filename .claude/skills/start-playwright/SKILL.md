---
name: start-playwright
description: 启动本项目 love-space-admin（Spring Boot:8080）和 love-space-web（Vite:5173），就绪后用 playwright MCP 打开浏览器访问前端。仅当用户要求"启动 playwright / 启动项目并打开浏览器 / start-playwright"时使用，仅适用于 love-space 项目。
---

# start-playwright

在本项目（`love-space`）一键启动 admin 后端、web 前端，并用 playwright MCP 打开浏览器进行可视化调试 / WEB 用例执行。

## 适用范围

仅适用于本项目。涉及的子服务：

- **love-space-admin** — Spring Boot 4 后端，端口 `8080`，REST 前缀 `/api/admin/*`（test profile 实例为 `21423`，e2e 专用库）
- **love-space-web** — React 19 + Vite 前端，端口 `5173`，base 路径 `/love-space/`，本机地址 `http://localhost:5173/love-space/`
- （可选）**love-space-app** — App 后端，端口 `8081`，仅在 WEB 用例不涉及时不必启动

> 前端通过 `.env` / `.env.local` 的 `VITE_ADMIN_API_BASE` 访问 admin 后端（浏览器侧发起请求，必须指向 `http://100.100.117.79:<后端端口>`）。后端依赖 PostgreSQL（连接见 `love-space-admin/src/main/resources/application.yml` 与环境变量，参考根目录 `.env.example`）。

> **重要 — playwright MCP 的访问地址**：远程浏览器运行在不同的网络环境，无法用 `localhost` 访问本机服务，必须用本机 Tailscale IP `100.100.117.79`。因此 playwright 打开前端的地址是 `http://100.100.117.79:5173/love-space/`，且 Vite 必须以 `--host` 启动绑定 `0.0.0.0`。

## 执行步骤

按顺序执行，逐步确认成功后再进行下一步。

### 1. 启动后端 love-space-admin

用 **后台模式**（`run_in_background: true`）运行，避免阻塞：

```bash
cd /home/lanshuangping/personal/love-space/love-space-admin && ./mvnw spring-boot:run
```

### 2. 启动前端 love-space-web

同样用 **后台模式** 运行（必须带 `--host`）：

```bash
cd /home/lanshuangping/personal/love-space/love-space-web && npm run dev -- --host
```

### 3. 等待两个服务就绪

轮询后端端口与前端页面，直到都返回成功（Spring Boot 冷启动较慢，最多重试约 60 次，每次间隔 2s）：

```bash
# 后端：任一返回成功即就绪
curl -fsS http://localhost:8080/actuator/health || curl -fsS -o /dev/null -w "%{http_code}" http://localhost:8080/api/admin/auth/login

# 前端：期望 HTTP 200（playwright 走 Tailscale IP，这里一并验证该 IP 可达）
curl -fsS -o /dev/null -w "%{http_code}" http://localhost:5173/love-space/
curl -fsS -o /dev/null -w "%{http_code}" http://100.100.117.79:5173/love-space/
```

若失败：检查后台任务输出（端口占用、PostgreSQL 未启动、缺环境变量见 `.env.example`、web 依赖未装 `npm install` 等），修复后重试。

### 4. 用 playwright MCP 打开浏览器

服务就绪后，用 playwright MCP 导航到前端：

- 调用 `browser_navigate`，URL = `http://100.100.117.79:5173/love-space/`（**必须用 Tailscale IP，不能用 localhost**）
- 然后可用 `browser_snapshot` 查看页面结构，按用户后续需求进行点击 / 填表 / 截图等操作

## 完成后

向用户报告：
- 后端 / 前端是否就绪（含访问地址）
- 浏览器已打开的页面
- 服务以后台任务运行，需停止时可终止对应后台任务

## 注意事项

- 服务以后台任务长驻运行，不要用前台阻塞命令启动。
- 若端口 `8080` 或 `5173` 已被占用，说明服务可能已在运行，先检测端口/页面再决定是否重启。
- 前端必须以 `--host` 绑定 `0.0.0.0`，否则远程浏览器通过 `100.100.117.79` 访问不到（只绑 `127.0.0.1` 的实例是无法访问的反面案例）。
- 前端 URL 带 base 路径 `/love-space/`，直接访问 `http://…:5173/` 会 404。
