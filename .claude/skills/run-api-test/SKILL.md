---
name: run-api-test
description: 手动触发 api-test-runner 执行 IT(集成/接口)用例（OpenSpec 版），并在 runner 回写 tests/{domain}/it.md 后自动跑生成脚本刷新追溯矩阵(runner 自身不刷新)。本 skill 只做编排：解析执行范围(--change/--module)、前置校验(it.md/api-spec.json/baseUrl 白名单/后端可达)、调起 api-test-runner agent、刷新矩阵、汇总；真正的 HTTP 执行与断言在 api-test-runner agent 本体里。与 run-web-test 对称。当用户说「跑 IT / 跑接口测试 / 执行 api-test-runner / 后端先行验证」时使用。
metadata:
  author: codeing-test-workflow
---

# 手动触发 api-test-runner(IT 接口测试编排，OpenSpec 版)

**职责边界**：agent 是唯一执行本体(读写 `tests/{domain}/it.md`、发 HTTP、断言、存证)；本 skill 只负责**前置校验 + 调起 agent + 刷新矩阵 + 汇总**。IT 直接 `curl` 后端，`localhost` 即可达，**无需** run-web-test 那套远程浏览器 IP/端口探测。

## 入参

`/run-api-test [--change <change-id> | --module <domain>] [--base-url <url>] [--cases <TC完整ID,逗号分隔>] [--rerun-failed]`

- `--change <change-id>`：**交付轮**。按 `openspec/changes/<id>/test-cases.md` 受影响清单跑；缺省时 `openspec/changes/` 下(不含 archive)恰好唯一则提示「默认用 <id>，确认?」，否则要求明确指定(不臆测)。
- `--module <domain>`：**回归轮**。跑 `tests/<domain>/it.md` 全部用例。回归编排优先用 `/regression-test`，本参数供单域快速回归。
- `--base-url`：覆盖后端地址；缺省 `http://localhost:8080`(admin 后端，路径 `/api/admin/*`；app 后端为 `http://localhost:8081`，路径 `/api/app/*`，需带 API-key 请求头)。**必须命中 `tests/modules.md` 白名单**。
- `--cases`：只跑指定用例(完整 TC ID，如 `TC-auth-IT-001`)。
- `--rerun-failed`：仅重跑 `❌ 失败` 用例。

## 执行流程(主 agent 按此操作)

### 1. 解析执行范围

- change 模式：确认 `openspec/changes/<id>/test-cases.md` 存在且清单含 IT 用例；从 TC ID 的 domain 段定位涉及的 `tests/<domain>/it.md`。
- module 模式：确认该域已在 `tests/modules.md` 注册且 `tests/<domain>/it.md` 存在。
- 都不满足则报错并提示先走 `/opsx:propose`(test-cases artifact 会生成用例)。

### 2. 前置校验(缺一即停，逐条提示用户补充；不猜测、不擅自拉起服务)

- `contracts/api-spec.json` 存在。
- `baseUrl` 命中 `tests/modules.md` 白名单。
- **后端可达**：健康检查(`curl -s -o /dev/null -w "%{http_code}" <baseUrl>/actuator/health`，非 `000` 即可达，404 也算可达)。不可达则提示用户先启动后端，**不代为启动**。

```bash
BASE="http://localhost:8080"
code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$BASE/actuator/health" 2>/dev/null)
echo "后端 $BASE -> ${code:-000}"
# code 为空或 000 → 停下提示用户启动后端，不调 agent
```

### 3. 调起 api-test-runner(把执行范围/baseUrl/用例范围注入指令)

用 Agent 工具，`subagent_type: api-test-runner`，prompt 模板：

```
请执行 <--change <id> 清单内 / tests/<domain>/it.md 全部> 的 IT 用例<（或 --cases/--rerun-failed 指定子集）>。
后端 baseUrl = <baseUrl>(已核对 tests/modules.md 白名单)。遵循 api-test-runner 既定规则：
前置依赖拓扑排序 + 提取变量串联、逐条断言(状态码→响应头→body 字段→契约 schema，
契约读 contracts/api-spec.json 按用例 关联契约 JSON Pointer 定位)、契约漂移标 ⚠️ 不判失败、
存证到 <test-evidence/<change-id>/ 或 test-evidence/regression/<domain>/><TC完整ID>/
(request/response/assertions，按 tests/modules.md 存证口径脱敏)、
只回写 it.md 状态字段(状态/执行存证/最后更新→<当天日期>/失败原因)不改定义字段、
不碰 web.md/app.md 与 openspec/ 下文件、前置不满足标「未执行」非 ❌、
最后给汇总(总数/✅/❌/⚠️/跳过 + 原因 + 存证路径)。
```

### 4. 刷新追溯矩阵(必做，别忘)

runner 只回写 `it.md`，**不会**更新追溯矩阵。runner 一返回就执行：

```bash
node scripts/generate-traceability-matrix.js --change <change-id>   # 交付轮
node scripts/generate-traceability-matrix.js                        # 回归轮(全局)
```

然后读矩阵的双向覆盖核对结论(Scenario 是否有覆盖、悬空用例、标 ✅ 但存证缺失的存疑项)，纳入汇报。

### 5. 回收结果

向用户汇总：总数 / ✅ / ❌ / ⚠️ / 未执行，失败原因，存证路径，本次使用的 baseUrl，以及**矩阵已刷新**及其核对结论。交付轮同时把执行结果摘要填回 `openspec/changes/<id>/test-cases.md` 的「执行汇总」节。

## 不做什么

- 不修改 `it.md` 的测试步骤/预期(那是 test-cases artifact 的职责)，只让 runner 碰状态字段。
- 不代为启动后端(不可达时提示用户，不擅自拉起)。
- 不在 skill 里重复实现 HTTP 执行/断言逻辑——那是 api-test-runner agent 本体的职责。
- 不碰 `web.md`/`app.md`、`openspec/specs/`。
