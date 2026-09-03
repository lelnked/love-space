---
name: "love-space-deploy"
description: "把 love-space 的前端 / admin 后端 / app 后端构建并部署到指定环境（test 或 prod）。支持单独部署某一端或全量部署。"
argument-hint: "<test|prod> [web|admin|app|all] [版本号]  例：prod admin 1.2.0  /  test all  /  prod web"
user-invocable: true
disable-model-invocation: false
---

## User Input

```text
$ARGUMENTS
```

解析 `$ARGUMENTS`：

- **第 1 个 token 是目标环境**，取值 `test` / `prod`。**没给就停下来问用户，不要猜、不要默认 prod。**
  用户用自然语言说「部署测试环境」「发生产」也算给了，映射到 `test` / `prod`。
- 第 2 个 token 是部署目标，取值 `web` / `admin` / `app` / `all`，缺省视为 `all`。
- 第 3 个 token 是版本号（仅后端用，作为 docker 镜像 tag），缺省时按 `日期+小时` 自动生成（例 `20260601-1530`），并在执行前向用户确认。
- 其他文字视为附加说明，不要据此改变流程；如有矛盾向用户澄清。

部署目标 = `web` 时不需要版本号，忽略第 3 个 token。

## 环境矩阵（本 skill 唯一的环境相关硬编码）

| | `test` | `prod` |
|---|---|---|
| 脚本环境变量 | `DEPLOY_ENV=test` | `DEPLOY_ENV=prod` |
| 环境变量文件（**在服务器上**，不在打包机） | `<部署根>/deploy/.env.test` | `<部署根>/deploy/.env.prod` |
| 前端构建命令 | `npm run build -- --mode test` | `npm run build` |
| 前端环境文件（**在打包机上**，构建期读取） | `love-space-web/.env.test` | `love-space-web/.env.production` |
| 环境文档（**执行前必读**） | `docs/部署测试环境.md` | `docs/部署正式环境.md` |

**SSH 登录方式、部署根目录、各端上传路径、部署脚本位置、对外访问地址，全部以环境文档为准**，
本文件不复制一份。开工前先读对应文档，把这几项取出来（下文用 `<SSH>`、`<部署根>` 指代）。
读不到或文档与实际不符 → 停下来问用户，不要凭印象填主机名。

## 通用事实（与环境无关）

- 三端产物：前端 `love-space-web/dist/`、admin `love-space-admin/target/*.jar`、app `love-space-app/target/*.jar`（jar 旁带 `Dockerfile`）。
- 服务器目录布局：`<部署根>/{web,admin,app,deploy}`，日志在 `<部署根>/logs/{admin,app}`。
- admin 监听 8080、app 监听 8081，host 网络模式。
- **admin 必须先于 app 启动**（admin 跑 Liquibase 建表），`deploy-apps.sh` 已保证顺序。
- 前端是静态文件，**覆盖即生效，不需要重启 nginx**。只有改了 nginx 配置才 `nginx -t && nginx -s reload`。
- 环境差异（DB 地址、JWT secret、API key、OSS、端口、容器名）**全部在服务器的 `deploy/.env.<环境>` 里**。
  日常发版不要动这个文件，也不要往 `~/.bashrc` 写环境变量。
- `deploy-apps.sh` 一次重建 admin + app 两个容器。只更一端时也会重启另一端 —— 先告知用户再执行。

## 执行前检查（任一项不满足 → 立即停止，给出提示，不要硬干）

按顺序核对，**每一项都要真的跑命令确认，不要凭上下文假设**：

1. **环境已明确**（`test` 或 `prod`）。没有就问。
2. **已读对应环境文档**，取到 `<SSH>` 与 `<部署根>`。
3. **工作目录是仓库根**（含 `love-space-web/`、`love-space-admin/`、`love-space-app/`、`deploy/`）。不在则 `cd` 过去。
4. **SSH 可达**：
   ```bash
   ssh <SSH> "echo ok"
   ```
   不通 → 停止，提示检查网络 / VPN / SSH key / `~/.ssh/config` 的主机别名。
5. **服务器上的环境变量文件存在**（缺了脚本会直接报错，提前拦住）：
   ```bash
   ssh <SSH> "test -f <部署根>/deploy/.env.<环境>" \
     || echo "❌ 服务器缺 <部署根>/deploy/.env.<环境>"
   ```
   缺失 → **停止部署**，提示用户在**服务器上**创建：
   `scp deploy/.env.example <SSH>:<部署根>/deploy/` → `ssh <SSH>` → `cd <部署根>/deploy`
   → `cp .env.example .env.<环境>` → 填值。
   该文件含密钥、属于服务器、不在 git 里，**不要替用户编造值，也不要在打包机上生成后传上去**。
6. **部署 web 时，本地前端环境变量文件存在**：
   ```bash
   test -f love-space-web/.env.production   # prod
   test -f love-space-web/.env.test         # test
   ```
   缺失 → **停止部署**，提示：`cp love-space-web/.env.<对应>.example love-space-web/.env.<对应>` 后填值。
   **绝不能跳过这步继续 build** —— Vite 缺文件不会报错，`VITE_ADMIN_API_BASE` 会静默回落到
   `http://localhost:8080`，打出来的包在服务器上所有接口都连不上。
7. **部署后端时**，本地 Java 25 + Maven Wrapper 可用（`./mvnw -v`）。
8. **部署前端时**，本地已装依赖（缺 `node_modules` 时 `npm install`，注意需 `NODE_ENV=development`）。
9. **后端且用户没给版本号**：先把要用的版本号告诉用户，等确认或改写。
10. **不要**主动 `git pull` / 切分支；按当前工作树构建。工作树有改动就提醒用户，但不强制。

## 流程

按目标分派，`all` 时按 **web → admin → app** 串行（前端先发；admin 在 app 之前以便先跑 Liquibase）。

每一步把要执行的命令直接打出来，并在每个外部命令前用一句中文说明它在做什么。
**禁止**用 `&&` 把多个高风险命令拼一行，方便失败时定位。

### 通用：上传部署脚本（当本地 `deploy/` 有变更或用户明确要求时执行）

```bash
ssh <SSH> "mkdir -p <部署根>/deploy"
scp deploy/.env.example <SSH>:<部署根>/deploy/
scp deploy/*.sh <SSH>:<部署根>/deploy/
ssh <SSH> "chmod +x <部署根>/deploy/*.sh"
```

- **传 `deploy/.env.example` + `deploy/*.sh`**：模板可以覆盖；脚本变更时两样都传。
- **不要传 `deploy/.env.<环境>`**：它属于目标服务器、含密钥、不在 git 里，不要从打包机覆盖。
- 如果用户要求“重新上传 deploy 目录”，按上面三条命令执行，不要只传 `*.sh` 而漏掉 `.env.example`。

### 分支 A：`web`（前端）

```bash
cd love-space-web
[ -d node_modules ] || npm install          # 首次或 package.json/lock 改过

# 构建：命令按环境选（见环境矩阵），mode 决定读哪个 .env
npm run build                               # prod
npm run build -- --mode test                # test

# 核对产物里编译进去的后端地址，确认不是 localhost
grep -o 'https\?://[^"]*' dist/assets/*.js | grep -m1 admin

# 覆盖到服务器 nginx 目录
ssh <SSH> "mkdir -p <部署根>/web"
scp -r dist/* <SSH>:<部署根>/web/
```

若上一步 grep 出来的地址不是该环境该有的（prod 应是线上域名，test 应是测试环境地址），
**停止上传**，说明打错包了，让用户检查对应的 `.env` 文件。

不重启 nginx。完成后告知用户：构建大小、上传文件数、产物里的 API base。

### 分支 B：`admin`（admin 后端）

```bash
cd love-space-admin
./mvnw clean package -DskipTests
ls -1t target/love-space-admin-*.jar | head -1        # 确认刚打好的 jar
ssh <SSH> "mkdir -p <部署根>/admin"
scp target/love-space-admin-*.jar <SSH>:<部署根>/admin/
scp Dockerfile <SSH>:<部署根>/admin/
ssh <SSH> "cd <部署根>/deploy && DEPLOY_ENV=<环境> bash deploy-apps.sh <版本号>"
```

> `deploy-apps.sh` 会连 app 一起重建。只想更 admin 时，先把这个副作用告诉用户并让其确认。

### 分支 C：`app`（app 后端）

同分支 B，把 `admin` 换成 `app`（jar 名 `love-space-app-*.jar`，目录 `<部署根>/app/`），
最后一条 `deploy-apps.sh` 命令完全相同。同样的副作用提示。

### 分支 D：`all`

1. 走分支 A（前端）。
2. 本地把 admin、app 两个 jar **都打完**再传，避免传一半失败。
3. 上传两端 jar + Dockerfile。
4. 服务器执行一次 `DEPLOY_ENV=<环境> bash deploy-apps.sh <版本号>`，由它顺序重建 admin、app。

## 部署后验证

```bash
# 容器状态
ssh <SSH> "docker ps --filter name=love-space- --format 'table {{.Names}}\t{{.Status}}\t{{.Image}}'"
# admin 健康（Liquibase 失败也在这里暴露）
ssh <SSH> "curl -fsS http://127.0.0.1:8080/actuator/health || curl -fsS -I http://127.0.0.1:8080/"
# app 健康
ssh <SSH> "curl -fsS http://127.0.0.1:8081/actuator/health || curl -fsS -I http://127.0.0.1:8081/"
# 前端（对外地址见环境文档）
ssh <SSH> "curl -fsSI <该环境前端地址> | head -1"
```

任意一项失败：

- 立即拉日志（`docker logs --tail 200 love-space-admin` / `love-space-app`），把关键报错原文贴回给用户。
- **不要**自动回滚或自动重启；先描述问题、可能原因、可选方案，等用户决定。

## 失败处理与回滚

- 本地 `mvn package` 失败：停下来，不要把旧 jar 传上去。
- 前端 `npm run build` 失败：最常见报错是 `vite.config.ts: Cannot find module 'node:fs'`，这是 `@types/node` 缺失或 `tsconfig.json` 没正确引用 Node 类型导致的。应先修复类型依赖再重试，不要把缺失类型声明的包打出去。
- 前端 build 出的 API base 不对：停下来，不要上传，先修 `.env`。
- `scp` 失败：检查 SSH，重试一次；仍失败则报告。
- 容器起不来 / Liquibase 失败：把 `docker logs` 末尾错误整段贴出，并告知用户：
  - 回滚要把 `<部署根>/{admin,app}/` 下的旧 jar 放回去（保留旧 jar 是用户/运维的责任），
    再 `DEPLOY_ENV=<环境> bash deploy-apps.sh <旧版本>`；
  - 不要自行 `docker rm` / `docker volume rm` / 改 Liquibase changelog。
- `nginx -t` 失败时**不要** `nginx -s reload`，先给用户看错误。

## 报告

部署成功后给一段简短总结：

- 目标环境 + 部署目标 + 版本号
- 前端产物里的 API base（证明打的是对应环境的包）
- 各端健康检查结果
- 服务器上当前镜像 tag
- 跳过了哪些步骤（没传 deploy/、没重新 npm install 等）

不要堆叠完整命令日志；用户问起再展开。

## 参考

- `docs/部署正式环境.md` / `docs/部署测试环境.md`：各环境的登录方式、目录、上传位置、访问地址 —— **环境事实的真源**。
- `docs/部署服务器初始化.md`：PostgreSQL / nginx / SSL / OSS 首次初始化，本 skill 不覆盖。
- `deploy/deploy-apps.sh`：服务器端构建+重启脚本，镜像名/容器名/端口/JVM 参数以它为准；`deploy/.env.example` 是环境变量模板。
- `docs/web-开发规范.md`：前端构建 mode 与 `.env` 的对应关系。
