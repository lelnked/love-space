## Context

App 生成接口的每日额度（`PER_ANON_DAILY` / `GLOBAL_DAILY`）目前由 app 的 `AiLimitProperties`（环境变量 `AI_LIMIT_*`）提供，`AiGenerationService.checkQuota` 读取。admin 与 app 共用同一个 PostgreSQL 库，表结构由 admin Liquibase 管理，AI 场景、模板已是"admin 写、app 读"的模式。本 change 把额度也纳入这一模式。

基线：`ai-generation-records`（已实现、未归档）。

## Goals / Non-Goals

**Goals:** 额度存表、一种限制一行、按行记录修改人；后台可改、`-1` 不限制、即时生效；彻底移除 `AI_LIMIT_*`。

**Non-Goals:** 新增/删除限制项、按场景额度、缓存、修改历史（操作日志已追溯）、环境变量兜底。

## Decisions

### D1 表结构：`limit_key` 文本主键，不继承 `BaseAuditEntity`
`loves_ai_limit(limit_key text PK, name text NOT NULL, limit_value int NOT NULL CHECK (limit_value = -1 OR limit_value > 0), updated_by text, updated_at timestamptz)`。
- 行由 changelog 固定写入，代码按编码读取，UUID 主键 + 编码唯一列只会多一层间接；文本主键更直接。手册"一律继承 BaseAuditEntity"的约定此处不适用（无 created_at，`updated_at` 只在运营修改时写，初始为空，与 JPA Auditing 的"每次 save 都写"语义不同）。
- 取值合法性 DB 加 CHECK 兜底，接口层 `@NotNull @Min(-1)` + 服务层拒绝 0 给出中文 400。
- 备选：单行多列（已被用户否决，需要按行记录修改人）。

### D2 admin 接口
- `AiLimitController`（`/api/admin/ai/limits`）：`GET` 返回 `List<AiLimitResponse>`（`findAll(Sort.by("key"))`）；`PUT /{key}` 体 `AiLimitUpdateRequest(Integer value)`，`@OperationLog("ai-limit:update")`。
- `AiLimitService.update`：`findById` 不存在 → `ResourceNotFoundException`（404）；`value == 0` → `IllegalArgumentException("取值须为 -1 或正整数")`；写 `updatedBy = OperatingContext.currentUsername()`、`updatedAt = now(aiClock)`。
- 校验：`value` 声明为 `BigDecimal`，`@NotNull @Digits(integer=10, fraction=0) @Min(-1) @Max(Integer.MAX_VALUE)`，message 均为「取值须为 -1 或正整数」，service 再 `intValueExact()` 并拒绝 0。不用 `Integer`：Jackson 会把 `1.5` 静默截断成 1 并保存（IT TC-ai-IT-090 实测发现）；不改全局 `ACCEPT_FLOAT_AS_INT`，避免影响其他接口。非数字（如 `"abc"`）仍走现有 `HttpMessageNotReadableException` → 400。

### D3 操作日志 target 取字符串参数
切面现在只取第一个 UUID 入参作 target；`{key}` 是字符串。扩展规则为"第一个 UUID，否则第一个 `String` 入参"。已核对：现有 69 个 `@OperationLog` 方法中，无 UUID 入参的全是"只有 `@RequestBody`"或无参（登出），没有 String 入参，行为不变。备选：把 key 放进请求体——破坏 REST 路径语义，否决。

### D4 app 读取：每次请求查表，不缓存
- app 新增只读实体 `AiLimit`（映射同表，`ddl-auto: validate` 校验）与 `AiLimitRepository`。
- `checkQuota`：`findAll()` 转 Map，缺 `PER_ANON_DAILY` 或 `GLOBAL_DAILY` 抛 `IllegalStateException`（→ 全局 500，不放行）；值为 -1 跳过该项且不做计数查询。
- 每次多一次两行小表查询，相对两次 count 与模型调用可忽略；不缓存换来"保存即生效"。
- 删除 `AiLimitProperties` 与 `@EnableConfigurationProperties`，`application.yml` 删 `love-space.ai-limit` 段。

### D5 app 测试
app 测试库为 `create-drop`，表由 Hibernate 按实体建、无 Liquibase 种子数据。测试基类 `@BeforeEach` 用 `AiLimitRepository` 写入两行（各测试类设置自己的值），替代原 `@TestPropertySource`。

### D6 web 页面映射（线框 ← 区域 → 组件）

| 区域 | 实现 |
|---|---|
| 列表（名称 / 上限 / 最后修改人 / 修改时间 / 操作） | `src/pages/AiLimits/List.tsx`：`ComponentCard` + `DataTable`，全量两行不分页、无 FilterBar（数据量固定为 2，列表三件套中筛选与分页无意义） |
| ① 编辑 | 行内 `Button` → 打开 `LimitEditModal` |
| ② 上限显示 | `value === -1 ? "不限制" : \`${value} 次\``；修改人/时间 null → 「—」，时间用 AiGenerations 现有格式化函数 |
| 提示行 | 表格下方 `text-xs text-gray-400`「提示：-1 表示不限制，保存后立即生效」 |
| ③ 上限输入 | `src/pages/AiLimits/LimitEditModal.tsx`：无遮罩 `Modal`（1 个字段，符合弹窗规范），`<input type="number">`，正则 `^(-1|[1-9]\d*)$` 校验，非法时下方 `text-error-500 text-xs`「取值须为 -1 或正整数」 |
| ④ 保存 | 非法或提交中置灰；成功 `toast.success("已保存，立即生效")`、关闭、刷新列表；失败 toast 后端 detail |
| 加载 / 错误 | `DataTable loading`；加载失败 `toast.error(detail ?? "加载失败")` |
| 菜单 | `AppSidebar.tsx`「AI 管理」末尾加 `{ name: "限流配置", path: "/ai/limits" }`；`App.tsx` 注册路由 |
| API | `src/api/ai.ts` 增 `AiLimit` 类型、`listAiLimits()`、`updateAiLimit(key, value)` |

### D7 契约
`contracts/api-spec.json` 新增 `GET /api/admin/ai/limits`（`x-requirement: ai/生成额度配置`）、`PUT /api/admin/ai/limits/{key}`（同上），schema `AiLimit`、`AiLimitUpdateRequest`。app 端接口不变，无需重跑 openapi 生成（`AiLimitProperties` 不在 DTO 中）。

## 已定决策

- 权限：MEMBER 可改（沿用 `/api/admin/ai/**` 全员开放）。
- 小数与超出 int 的值同样返回中文 400「取值须为 -1 或正整数」。
- 配置行缺失 → 500。

## Risks / Trade-offs

- [部署顺序] app 先于 admin 迁移启动 → `ddl-auto: validate` 找不到表，app 启动失败 → 部署文档明确 admin 先部署；现有 deploy 脚本 deploy-all 已是 admin 先。
- [并发] 先计数再插入的已知上限不变。
- [MEMBER 可放开额度为不限] → 有操作日志追溯；如需收紧另开 change。

## Migration Plan

1. 部署 admin（Liquibase 建表 + 种子 50/5000，行为与现状一致）。
2. 部署 app（读表）。
3. 从服务器 `deploy/.env.<环境>` 删除 `AI_LIMIT_*`（留着也无影响）。

回滚：先回滚 app 到旧版本（读环境变量），再 `DROP TABLE loves_ai_limit`（changelog rollback）。
