# Proposal: ai-limit-config（AI 生成每日额度后台配置）

## Why

App 生成接口的两项每日额度（单个匿名 ID 每日上限、全站每日上限）目前写在 app 进程的环境变量 `AI_LIMIT_PER_ANON_DAILY` / `AI_LIMIT_GLOBAL_DAILY` 里，要调整只能改服务器 `.env` 并重启 app。运营需要在后台随时调整，必要时临时放开不限。本 change 把这两项额度改为存在数据库里，由后台页面维护；app 每次生成时实时读取，并彻底移除这两个环境变量。

## What Changes

按功能先后顺序（也是实现顺序）：

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F1 | 限流配置：查看、修改两项每日额度（`-1` 表示不限制） | admin + web | AI 管理 › 限流配置 |
| F2 | 生成接口额度改读配置：app 额度检查从表读取，`-1` 跳过该项；移除环境变量 | app + deploy | — |

明确**不做**：按场景单独设额度、按小时/分钟的应用层限流（IP 限流仍由 nginx 负责，不变）、配置修改历史（操作日志已能追溯）、环境变量兜底、额度缓存。

### 全局约定（各功能共用）

- **取值规则**：每项额度都只允许 `-1` 或正整数（1 ~ 2147483647）；`-1` = 不限制，`0` 及其他负数都不合法。
- **初始值**：建表的 changelog 同时写入两行初始数据：单个匿名 ID 每日上限 50、全站每日上限 5000（和现在环境变量的默认值一致，上线前后行为不变）。
- **不做兜底**：配置只来自数据库，不再读环境变量；对应行不存在视为部署错误，生成接口返回 500，不悄悄放行。
- **权限**：沿用 `/api/admin/ai/**`，所有登录运营（ADMIN、MEMBER）均可查看和修改，不新增角色或权限点。
- **操作日志**：保存写现有操作日志 `ai-limit:update`（每改一行记一条）；查看不留痕。
- **错误格式**：沿用 `{status,error,message,path}`。
- **侧栏**：「AI 管理」新增子菜单「限流配置」，排在「生成记录」之后（基线为 `ai-generation-records` 归档后的菜单）。

---

### F1 限流配置

**表 `loves_ai_limit`**（一种限制一行，各行独立修改、独立记录修改人）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| limit_key | text | PK | 限制编码，代码按编码读取 |
| name | text | NOT NULL | 显示名称 |
| limit_value | int | NOT NULL, CHECK (limit_value = -1 OR limit_value > 0) | 上限，`-1` 不限制 |
| updated_by | text | | 该行最后修改人 username；初始数据为空 |
| updated_at | timestamptz | | 该行最后修改时间；初始数据为空 |

初始数据（本期固定两行，不提供新增 / 删除）：

| limit_key | name | limit_value |
|---|---|---|
| `PER_ANON_DAILY` | 单个匿名 ID 每日上限 | 50 |
| `GLOBAL_DAILY` | 全站每日上限 | 5000 |

changelog `028-create-ai-limit.sql`：建表 + 插入上面两行，带 rollback（`DROP TABLE`）。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/limits` | 无 | `AiLimit[]`，按 limit_key 升序，全量（不分页） |
| PUT | `/api/admin/ai/limits/{key}` | path：key；body：`{value*}` | `AiLimit`；key 不存在 → 404；value 缺失、为 0、小于 -1、非整数 → 400「取值须为 -1 或正整数」 |

`AiLimit` = `{key, name, value, updatedBy, updatedAt}`（该行从未修改过时 `updatedBy` / `updatedAt` 为 null）。

每次只改一行，只更新该行的 `updated_by` / `updated_at`，其他行不受影响；同一行后保存的为准，不做乐观锁。

操作日志：`ai-limit:update`，target 为 limit_key（切面 target 规则扩展：无 UUID 入参时取第一个字符串入参）。

**页面 `/ai/limits`**

```
┌ AI 管理 › 限流配置 ──────────────────────────────────────────────────────┐
│ 名称                    上限            最后修改人   修改时间      操作   │
│ 全站每日上限            5000 次         admin        10-26 14:30   编辑① │
│ 单个匿名 ID 每日上限    不限制 ②        —            —             编辑   │
│ 提示：-1 表示不限制，保存后立即生效                                      │
└──────────────────────────────────────────────────────────────────────────┘

┌ 编辑：单个匿名 ID 每日上限（弹窗）──────┐
│ 上限 *  [    50 ] 次 ③                  │
│         -1 表示不限制                   │
│                    [取消] [保存]④       │
└─────────────────────────────────────────┘
```
进入 → `GET /ai/limits` 渲染列表；② 值为 -1 时显示「不限制」，从未修改过的行修改人 / 时间显示「—」；① → 打开弹窗并回填当前值；③ 前端校验只能填 `-1` 或正整数，不合法时在输入框下提示且 ④ 置灰；④ → `PUT /ai/limits/{key}`，成功后关闭弹窗、提示「已保存，立即生效」并刷新列表。

---

### F2 生成接口额度改读配置

**规则**（只改额度来源和 `-1`，其余沿用现有「生成接口每日额度」要求）
- app 在额度检查这一步读 `loves_ai_limit` 中 `PER_ANON_DAILY`、`GLOBAL_DAILY` 两行（每次请求读一次，不缓存），后台保存后下一次请求即生效，无需重启。
- 某项为 `-1` → 跳过该项检查（也不再为它做计数查询）；两项都是 `-1` → 不做任何额度检查。
- 检查顺序、"每天"按北京时间自然日、计数口径、429 文案、去重不占额度、超额不写记录——全部不变。
- 已知上限：先计数再插入，并发下可能超出上限几次——不变。

**移除**
- app：删除 `AiLimitProperties` 及其注册，`application.yml` 删除 `love-space.ai-limit` 配置段。
- 部署：`deploy/deploy-apps.sh`、`deploy/.env.example` 删除 `AI_LIMIT_PER_ANON_DAILY` / `AI_LIMIT_GLOBAL_DAILY`；服务器上 `deploy/.env.prod`、`deploy/.env.test` 中这两项变为无效，部署时顺手删除。

**表 / API**：无新增；app 端新增 `loves_ai_limit` 的只读实体（`ddl-auto: validate` 校验结构）。

---

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `ai`：
  - 修改「生成接口每日额度」：额度来源由环境变量改为 `loves_ai_limit`，新增 `-1` 不限制，相关场景的 GIVEN 改为通过配置设置额度；
  - 新增「生成额度配置」（F1 接口与取值校验）、「限流配置页面」（F1 页面）；
  - 修改「AI 管理写操作留痕」：新增 `PUT /api/admin/ai/limits/{key}` → `ai-limit:update`；
  - 修改「AI 管理权限与菜单」：子菜单新增「限流配置」(`/ai/limits`)。
- `operation-log`：「留痕字段取值与敏感信息脱敏」target 规则扩展为无 UUID 入参时取第一个字符串入参；「运营写操作留痕」覆盖面新增模块 `ai-limit`，计数随之更新（以 `ai-generation-records` 归档后的 19 个模块 68 个写接口为基线 → 20 个模块 69 个写接口）。

## Impact

- **前置依赖**：`ai-generation-records` 已实现未归档，本 change 的 delta 以它归档后的 living specs 为基线，须在它之后归档。
- **数据库**：admin 端 Liquibase 新增 `028-create-ai-limit.sql`（1 张表 + 2 行初始数据），纯新增、可回滚。
- **love-space-admin**：`modules/ai` 新增限流配置的实体、仓库、服务、controller、DTO。
- **love-space-app**：`AiGenerationService.checkQuota` 改为读表；新增只读实体与仓库；删除 `AiLimitProperties`；`AiGenerationTest`、`AiGenerationGlobalLimitTest` 改为通过写表设置额度。
- **love-space-web**：侧栏「AI 管理」新增「限流配置」+ 1 个路由页面（`/ai/limits`）。
- **契约**：`contracts/api-spec.json` 新增 admin 端 2 个接口；app 端接口不变。
- **部署**：**BREAKING**——`AI_LIMIT_*` 环境变量不再生效。上线顺序必须是 admin 先启动跑完迁移，app 再启动，否则 app 生成接口因读不到配置返回 500。
- **测试**：`tests/ai/it.md` 中依赖 `AI_LIMIT_*` 的额度用例改为先调 `PUT /api/admin/ai/limits/{key}` 设置；新增 F1 接口、`-1` 不限制、F1 页面用例。
