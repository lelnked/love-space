# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 开发规范手册（动手前必读）

改动某一端的代码前，**先读对应手册**，里面是该端的分层、命名、接口/DTO/异常/分页/安全约定与交付清单：

| 改动落在 | 必读 |
|---|---|
| `love-space-web/`（后台前端） | [docs/web-开发规范.md](docs/web-开发规范.md) |
| `love-space-admin/`（后台后端） | [docs/admin-开发规范.md](docs/admin-开发规范.md) |
| `love-space-app/`（App 后端） | [docs/app-开发规范.md](docs/app-开发规范.md) |

一次改动跨端就把涉及的几份都读了。业务规则真源是 `openspec/specs/`，接口契约真源是 `contracts/api-spec.json`，手册只讲写法，不复制需求。

各端的命名、分层、样式、排序等硬约定**只写在手册里**，本文件不再复制一份。唯一在这里重复的跨端铁律：
**运营账号统一叫 Manager**（实体 `Manager` / 表 `loves_manager` / 路径 `/api/admin/managers` / 前端 `src/pages/Managers` / 登录响应字段 `manager`），三端都不要用 `user` 命名运营账号。

## 项目组成

单个 git 仓库，三个互相独立的子项目（两个后端是兄弟关系，**不是**同一个父 POM 的模块）：

| 目录 | 是什么 | 技术栈 |
|---|---|---|
| `love-space-web/` | 运营后台前端 | React 19 + TS + Vite 6 + Tailwind v4（TailAdmin 模板衍生） |
| `love-space-admin/` | 运营后台后端 | Spring Boot 4.0.6 / Java 25 / Maven，包 `com.loves.space` |
| `love-space-app/` | 移动端 App 后端 | Spring Boot 4.0.6 / Java 25 / Maven，包 `com.space.app` |

前端只连 admin 后端，App 客户端（不在本仓库）只连 app 后端。**两个后端的 controller 不互相引用**，接口面各自独立。

真源与文档：

| 找什么 | 去哪 |
|---|---|
| 各端怎么写代码、怎么跑、怎么测 | `docs/{web,admin,app}-开发规范.md` |
| 业务规则（行为真源） | `openspec/specs/` |
| 接口契约 | `contracts/api-spec.json`（app 端另有 `love-space-app/docs/openapi.json`） |
| 测试域注册表与用例 | `tests/modules.md`、`tests/{domain}/` |
| 原始产品需求 | `需求文档.pdf`、`二期需求开发文档.md` |
| 部署正式环境 | `docs/部署正式环境.md`（构建 → 上传 → 跑脚本） |
| 部署测试环境 | `docs/部署测试环境.md`（目标 `lo_test` → 47.109.27.132） |
| 服务器首次初始化 | `docs/部署服务器初始化.md`（PostgreSQL / nginx / SSL / OSS） |
| 部署脚本本身 | `deploy/`（`DEPLOY_ENV=prod\|test` 选环境，差异全在 `deploy/.env.<环境>`） |

## 跨端协作

- 一个改动同时涉及 web 与 admin 时：**先改 Java 的 controller/DTO，再镜像前端类型**。没有 codegen，靠 `contracts/api-spec.json` 对齐。
- 外层是单个 git 仓库，一次 commit 可以跨子项目；commit message 加前缀 `web:` / `admin:` / `app:`。
- 行为有变化的改动走 OpenSpec change 流程，见 `.claude/rules/openspec-session-protocol.md`（每个会话自动注入）。
