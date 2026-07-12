---
name: "love-space-deploy"
description: "把 love-space 的前端 / admin 后端 / app 后端构建并部署到生产服务器 root@119.29.108.66。支持单独部署某一端或全量部署。"
argument-hint: "[web|admin|app|all] [版本号]  例：admin 1.2.0   /   all 20260601   /   web"
user-invocable: true
disable-model-invocation: false
---

## User Input

```text
$ARGUMENTS
```

解析 `$ARGUMENTS`：

- 第 1 个 token 是部署目标，取值 `web` / `admin` / `app` / `all`，缺省视为 `all`。
- 第 2 个 token 是版本号（仅后端用，作为 docker 镜像 tag），缺省时按 `日期+小时` 自动生成（例 `20260601-1530`），并在执行前向用户确认。
- 其他文字视为附加说明，不要据此改变流程；如有矛盾向用户澄清。

部署目标 = `web` 时不需要版本号，忽略第 2 个 token。

## 必读信息

- 目标服务器：`root@119.29.108.66`
- 服务器目录布局：
  - `/app/loveSpace/admin/`  存放 admin jar + Dockerfile
  - `/app/loveSpace/app/`    存放 app jar + Dockerfile
  - `/app/loveSpace/web/`    nginx 直接读取的前端静态文件
  - `/app/loveSpace/deploy/` 部署脚本
- admin 监听 8080，app 监听 8081，都用 host 网络模式；数据库走 `172.16.16.12:8954/love_space`。
- 前端是静态文件，覆盖即生效，**不需要重启 nginx**。修改了 nginx 配置才需要 `nginx -t && nginx -s reload`。
- admin 容器先启动，负责跑 Liquibase 迁移；app 后启动。`deploy-apps.sh` 已经按这个顺序处理。
- OSS / JWT 等敏感环境变量已经在服务器 `~/.bashrc` 配置好了，**首次部署之外不要再写入**。如果用户没主动提，不要去碰 `~/.bashrc`。

## 执行前检查

在动手之前，按下面的清单核对，缺项**必须先问用户**或先修复，不要硬干：

1. 当前工作目录是仓库根（含 `love-space-web/`、`love-space-admin/`、`love-space-app/`、`deploy/` 四个子目录）。不在则 `cd` 过去。
2. 确认 `ssh root@119.29.108.66 "echo ok"` 能通；不通就提醒用户检查网络 / VPN / SSH key，停止后续动作。
3. 后端部署确认本地 Java 25 + Maven Wrapper 可用（`./mvnw -v`）。
4. 前端部署确认本地 Node 已装依赖（缺 `node_modules` 时跑 `npm install`，否则跳过）。
5. 后端部署且用户**没**给版本号时，先把要用的版本号告诉用户（按规则生成的或最近一次部署 +0.0.1），等用户确认或改写。
6. **不要** 主动 `git pull` / `git status` / 切分支；按当前工作树构建。如果工作树有改动，提醒用户但不强制。

## 流程

按解析出的目标分派，下面四个分支**互不重复**，需要 `all` 时按 `web → admin → app` 串行执行（前端先发，让 nginx 立刻拿到新静态资源；admin 在 app 之前以便先跑 Liquibase）。

每一步要把执行的命令直接打出来，并在每个外部命令前用一句中文说明它在做什么。**禁止**用 `&&` 把多个高风险命令拼一行，方便失败时定位。

### 通用：上传脚本（如果 `deploy/` 内容有变更）

仅当本地 `deploy/` 目录在最近一次提交之后又被改过、或用户明确要求时执行：

```bash
ssh root@119.29.108.66 "mkdir -p /app/loveSpace/deploy"
scp deploy/* root@119.29.108.66:/app/loveSpace/deploy/
ssh root@119.29.108.66 "chmod +x /app/loveSpace/deploy/*.sh"
```

否则跳过，不要每次都重传。

### 分支 A：`web`（前端）

```bash
cd love-space-web
# 第一次或 package.json/lock 改过才需要 install
[ -d node_modules ] || npm install
npm run build
# 把新静态资源覆盖到服务器 nginx 目录
ssh root@119.29.108.66 "mkdir -p /app/loveSpace/web"
scp -r dist/* root@119.29.108.66:/app/loveSpace/web/
```

不重启 nginx。完成后简单告知用户：构建大小、上传文件数。

### 分支 B：`admin`（admin 后端）

```bash
cd love-space-admin
./mvnw clean package -DskipTests
# 找到刚打好的 jar
ls -1t target/love-space-admin-*.jar | head -1
# 上传 jar 与 Dockerfile（Dockerfile 改动不频繁，但每次带上更稳）
ssh root@119.29.108.66 "mkdir -p /app/loveSpace/admin"
scp target/love-space-admin-*.jar root@119.29.108.66:/app/loveSpace/admin/
scp Dockerfile root@119.29.108.66:/app/loveSpace/admin/
# 在服务器上调度构建+重启（脚本会同时处理 admin 和 app；只更 admin 时也会重建 app，可接受）
ssh root@119.29.108.66 "cd /app/loveSpace/deploy && bash deploy-apps.sh <版本号>"
```

> 注意：现有 `deploy-apps.sh` 是 admin+app 一起重建。如果用户只要更 admin、不希望 app 跟着重启，必须先告诉用户这个副作用并让其确认。

### 分支 C：`app`（app 后端）

```bash
cd love-space-app
./mvnw clean package -DskipTests
ls -1t target/love-space-app-*.jar | head -1
ssh root@119.29.108.66 "mkdir -p /app/loveSpace/app"
scp target/love-space-app-*.jar root@119.29.108.66:/app/loveSpace/app/
scp Dockerfile root@119.29.108.66:/app/loveSpace/app/
ssh root@119.29.108.66 "cd /app/loveSpace/deploy && bash deploy-apps.sh <版本号>"
```

同样的副作用提示见分支 B。

### 分支 D：`all`

按顺序：

1. 走分支 A（前端）。
2. 本地分别 `cd love-space-admin && ./mvnw clean package -DskipTests` 和 `cd love-space-app && ./mvnw clean package -DskipTests`。两端 jar 都打完再传，避免传一半失败。
3. 上传两端 jar + Dockerfile 到 `/app/loveSpace/{admin,app}/`。
4. 服务器一次性执行 `bash deploy-apps.sh <版本号>`，由它顺序构建并重启 admin、app。

## 部署后验证

完成构建/上传/重启后，必须做这几件事，缺一不可：

```bash
# admin / app 容器状态
ssh root@119.29.108.66 "docker ps --filter name=love-space- --format 'table {{.Names}}\t{{.Status}}\t{{.Image}}'"
# admin 健康（Liquibase 也在这里报错）
ssh root@119.29.108.66 "curl -fsS http://127.0.0.1:8080/actuator/health || curl -fsS -I http://127.0.0.1:8080/"
# app 健康
ssh root@119.29.108.66 "curl -fsS http://127.0.0.1:8081/actuator/health || curl -fsS -I http://127.0.0.1:8081/"
# 前端
ssh root@119.29.108.66 "curl -fsSI https://tripleyourlife.com/ | head -1"
```

任意一项失败：

- 立即拉日志（`docker logs --tail 200 love-space-admin` / `love-space-app`），把关键报错原文贴回给用户。
- **不要**自动回滚或自动重启；先描述问题、可能原因、可选方案，等用户决定。

## 失败处理与回滚

- 本地 `mvn package` 失败：直接停下来，不要把旧 jar 传上去。
- `scp` 失败：检查 SSH，重试一次；仍失败则报告。
- 容器起不来 / Liquibase 失败：把 `docker logs` 末尾错误整段贴出，并告知用户：
  - 回滚需要把 `/app/loveSpace/admin/` 或 `/app/loveSpace/app/` 下的旧 jar 重新放进去（服务器上保留前一版 jar 的责任在用户/运维），再重新 `bash deploy-apps.sh <旧版本>`；
  - 不要自行 `docker rm` / `docker volume rm` / 改 Liquibase changelog。
- nginx 配置校验 (`nginx -t`) 失败时，**不要**执行 `nginx -s reload`，先给用户看错误。

## 报告

部署成功后，给用户一段简短总结，包含：

- 部署目标、版本号
- 用了多少时间（粗略）
- 各端健康检查结果
- 服务器上当前镜像 tag（`docker ps` 输出的镜像列）
- 如果跳过了某步骤（例如没传 deploy/、没重新 npm install），说清楚

不要堆叠完整命令日志；用户问起再展开。

## 参考

- `部署步骤.md`：原始流程文档，权威依据。
- `deploy/deploy-apps.sh`：服务器端构建+重启脚本，所有镜像名/容器名/端口/JVM 参数以它为准。
- `DEPLOY.md`：PostgreSQL 初始化与 OSS 配置，本 skill 不覆盖。
- `CLAUDE.md`：项目结构与命名约定（运营账号叫 Manager，表前缀 `loves_`）。
