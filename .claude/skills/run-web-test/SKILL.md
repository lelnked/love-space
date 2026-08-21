---
name: run-web-test
description: 手动触发 web-test-runner 执行 WEB 用例（OpenSpec 版），并自动把浏览器访问地址固定为本机 Tailscale IP 100.93.172.18(而非 localhost)。因为本环境的 Playwright 是远程浏览器，访问不了本机 localhost——前端 URL 与前端调用的后端 API base 都必须用 100.93.172.18 才可达。本 skill 负责：解析执行范围(--change/--module)、探测真实端口、校验前端 API base 是否指向该 IP、调起 web-test-runner 并把 IP 注入执行指令、最后跑生成脚本刷新追溯矩阵(runner 自身不会刷新)。当用户说「跑 WEB / 跑 E2E / 执行 web-test-runner / 手动触发 web 测试」时使用。（原 run-e2e-test，随测试端插件化更名）
metadata:
  author: codeing-test-workflow
  browser_ip: 100.93.172.18
---

# 手动触发 web-test-runner(浏览器 IP 固定为 100.93.172.18，OpenSpec 版)

本 skill 把「调起 web-test-runner 跑 WEB 用例」标准化，关键是解决本环境的固有约束：

> **Playwright 运行在远程机器上(经 Tailscale)。远程浏览器的 `localhost` 不是本机**。因此：
> 1. 浏览器导航的前端地址必须是 `http://100.93.172.18:<前端端口>/love-space/`，**不能用 localhost / 127.0.0.1**。
> 2. 被测前端实例调用后端的 `VITE_ADMIN_API_BASE` 必须指向 `http://100.93.172.18:<后端端口>`，否则浏览器侧请求会 `ERR_CONNECTION_REFUSED`。

默认参数(可被入参覆盖)：
- 浏览器/服务 IP：`100.93.172.18`(本机 Tailscale IP，见 frontmatter `browser_ip`)
- 被测前端 love-space-web：**不按端口号固定优先级，而是探测各实例的 `VITE_ADMIN_API_BASE`，优先选指向目标 IP 的实例**(候选端口 `5173`/`5174`/`5175` 仅作扫描范围)；base 路径 `/love-space/`
- 被测后端 love-space-admin：`8080`(dev)或 `21423`(test profile，e2e 专用库)，REST 前缀 `/api/admin/*`
- baseUrl 必须命中 `tests/modules.md` 白名单；探测到白名单外的实例一律不测。

## 入参

`/run-web-test [--change <change-id> | --module <domain>] [--ip <IP>] [--fe-port <前端端口>] [--be-port <后端端口>] [--cases <TC完整ID,逗号分隔>]`

- `--change <change-id>`：**交付轮**。按 `openspec/changes/<id>/test-cases.md` 受影响清单跑其中的 WEB 用例；缺省时 `openspec/changes/` 下(不含 archive)恰好唯一则提示「默认用 <id>，确认?」，否则要求明确指定。
- `--module <domain>`：**回归轮**。跑 `tests/<domain>/web.md` 全部用例。
- `--ip`：覆盖默认 `100.93.172.18`。
- `--fe-port` / `--be-port`：跳过自动探测，直接指定端口。
- `--cases`：只跑指定用例(完整 TC ID，如 `TC-auth-WEB-001`)。

## 执行流程(主 agent 按此操作)

### 1. 解析执行范围与用例文件

- change 模式：确认 `openspec/changes/<id>/test-cases.md` 存在且清单含 WEB 用例，按 TC ID 的 domain 段定位 `tests/<domain>/web.md`。
- module 模式：确认 `tests/<domain>/web.md` 存在。
- 不存在则报错并提示先走 `/opsx:propose`(test-cases artifact 会生成用例)。

### 2. 探测并锁定前端实例(按 API base 选，不按端口号顺序)

> ⚠️ **核心原则**：同一份代码可能有多个前端实例在跑，它们的 `VITE_ADMIN_API_BASE` 可能一个指 `localhost`(远程浏览器**不可达**)、一个指 IP(可达)。**绝不能按端口号固定优先级选**——必须逐个候选探测其 vite 进程的真实 `VITE_ADMIN_API_BASE`，**优先选指向目标 IP 的那个**。这是踩坑(误选 localhost 实例)后的硬性修正。

设 `IP=<--ip 或 100.93.172.18>`。后端先锁定(任一 HTTP 状态码即视为可达，含 401/404)：

```bash
IP=100.93.172.18
BE_PORT=""
for p in 8080 21423; do
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 2 "http://$IP:$p/actuator/health" 2>/dev/null)
  echo "后端 :$p -> $code"; [ -n "$code" ] && [ "$code" != "000" ] && BE_PORT=$p && break
done
echo "后端锁定 → http://$IP:$BE_PORT (REST 前缀 /api/admin)"
```

前端按 **API base 优先级**选：遍历候选端口，取每个端口对应 vite 进程环境里的 `VITE_ADMIN_API_BASE`，
**第一优先**选指向 `http://$IP:$BE_PORT` 的实例；次选指向该 IP(端口不同)的；**坚决排除**指向 `localhost`/`127.0.0.1` 的实例。

```bash
IP=100.93.172.18
declare -A FE_API   # 端口 -> 该实例的 API base
for pid in $(pgrep -f "vite" 2>/dev/null); do
  args=$(tr '\0' ' ' < /proc/$pid/cmdline 2>/dev/null)
  port=$(echo "$args" | grep -oE -- '--port [0-9]+' | grep -oE '[0-9]+')
  [ -z "$port" ] && port=5173   # 无 --port 参数时取 vite 默认 5173
  api=$(tr '\0' '\n' < /proc/$pid/environ 2>/dev/null | grep '^VITE_ADMIN_API_BASE=' | cut -d= -f2-)
  [ -z "$api" ] && api="(默认 .env/.env.local)"
  FE_API[$port]="$api"
  echo "前端 :$port (pid $pid) -> API base: $api"
done
FE_PORT=""
for p in "${!FE_API[@]}"; do echo "${FE_API[$p]}" | grep -q "http://$IP:$BE_PORT" && FE_PORT=$p && break; done
[ -z "$FE_PORT" ] && for p in "${!FE_API[@]}"; do echo "${FE_API[$p]}" | grep -q "http://$IP:" && FE_PORT=$p && break; done
echo "前端锁定 → http://$IP:$FE_PORT/love-space/  (API base: ${FE_API[$FE_PORT]})"
```

判定规则：
- 排除任何 `localhost`/`127.0.0.1` 实例与白名单外的地址。
- 进程环境读不到时(env 来自 `.env.local` 文件而非进程环境)，以 `love-space-web/.env.local`、`.env` 的 `VITE_ADMIN_API_BASE` 为准核对。
- 若**没有**任何前端实例的 API base 指向该 IP(只有 localhost 实例或前端没起)：**不要凑合用**——停下，进入步骤 3 的「兜底」并提示用户。
- `--fe-port` 给定时直接用该端口，但仍**校验它的 API base 指向 IP**；不达标同样走兜底。

### 3. 复核锁定实例的 API base 可达性(关键一步)

对步骤 2 选中的 `FE_PORT`，确认其 API base 形如 `http://<IP>:<BE_PORT>`、且 `http://$IP:$FE_PORT/love-space/` 返回 200。
(`love-space-web/.env` 的 `VITE_ADMIN_API_BASE` 只是新实例的默认值，**已在跑的实例以其进程环境/启动时的 .env.local 为准**。)

#### 兜底：当前在线前端的 API base 不可达时

- 优先方案：提示用户把 `love-space-web/.env.local` 的 `VITE_ADMIN_API_BASE` 改为 `http://$IP:$BE_PORT` 后重启该前端(`npm run dev -- --host`)，再重跑本 skill。
- 次选(仅在用户同意后)：另起一个临时前端实例，API base 指向 IP：
  ```bash
  cd love-space-web && VITE_ADMIN_API_BASE="http://$IP:$BE_PORT" npm run dev -- --port 5175 --host
  ```
  用完后**主动提醒用户该 :5175 进程仍在运行**，询问是否清理(`lsof -ti:5175 | xargs -r kill`)。不要擅自 kill 用户原有实例。

### 4. 调起 web-test-runner(把 IP 与端口注入指令)

用 Agent 工具，`subagent_type: web-test-runner`，prompt 模板：

```
请执行 <--change <id> 清单内 / tests/<domain>/web.md 全部> 的 WEB 用例<（或 --cases 指定子集）>。

【浏览器基址 — 硬性约束】
Playwright 是远程浏览器，localhost 不可达。所有 browser_navigate 必须以 http://<IP>:<FE_PORT>/love-space/ 开头，
严禁使用 localhost / 127.0.0.1。用例中「访问登录页」即 http://<IP>:<FE_PORT>/love-space/signin。
被测后端为 http://<IP>:<BE_PORT>(REST 前缀 /api/admin，用于核对网络请求，不直接打)。

【执行要求】见 web-test-runner 既定规则：逐例执行、执行前清空 localStorage、关键步骤截图存证到
<test-evidence/<change-id>/ 或 test-evidence/regression/<domain>/><TC完整ID>/、
失败补 failure.png + console-logs.txt、只回写 web.md 状态字段(状态/执行存证/最后更新/失败原因)
不改定义字段、不碰 it.md 与 openspec/ 下文件、前置不满足标「未执行」、
最后给汇总(✅/❌/未执行 + 原因 + 存证路径)。
```

### 5. 刷新追溯矩阵(必做，别忘)

runner 只回写 `web.md`，**不会**更新追溯矩阵。runner 一返回就执行：

```bash
node scripts/generate-traceability-matrix.js --change <change-id>   # 交付轮
node scripts/generate-traceability-matrix.js                        # 回归轮(全局)
```

然后读矩阵的双向覆盖核对结论，纳入汇报。

### 6. 回收结果

向用户汇总：总数 / ✅ / ❌ / 未执行，失败原因，存证路径，本次锁定的 `IP:FE_PORT / BE_PORT`，以及**矩阵已刷新**及其核对结论。交付轮同时把执行结果摘要填回 `openspec/changes/<id>/test-cases.md` 的「执行汇总」节。若步骤 3 另起过临时实例，提醒清理。

## 不做什么

- 不修改 `web.md` 的测试步骤/预期(那是 test-cases artifact 的职责)。
- 不擅自 kill 用户正在运行的服务实例。
- 不在浏览器导航里使用 localhost(本 skill 存在的全部理由)。
- 不测 `tests/modules.md` 白名单外的任何地址。
