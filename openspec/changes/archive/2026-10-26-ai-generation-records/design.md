## Context

`ai-management`（已归档）让 app 生成接口写 `loves_ai_generation` / `loves_ai_generation_attempt`，admin 端目前**没有**这两张表的实体。本 change 在 admin 加只读查询 + 单一评价，web 加两页。

现状约束（读代码确认）：

- 两张表的 jsonb 列在 app 端以 `String + @ColumnTransformer(write="?::jsonb")` 映射，admin 的模板实体同样写法——沿用。
- app 在生成结束时以实体 `save` 回写整行；admin 若也用实体 merge 写评价，会和 app 的回写互相覆盖（谁后写谁赢，可能把 PROCESSING 写回去或抹掉评价）。
- admin `GlobalExceptionHandler` 已有 404（`ResourceNotFoundException`）/ 409（`ConflictException`）映射（ai-management 引入）。
- `FilterBar` 只支持静态选项、Reset 清空为 `{}`、日期无可选范围限制；web 手册要求列表页用 `FilterBar`。
- 北京时间口径：app 端额度已用 `Asia/Shanghai` 自然日。

## Goals / Non-Goals

**Goals:** 运营能按多维条件找到生成记录、看清每次模型调用、给每条记录打唯一一个质量评价。

**Non-Goals:** 评价历史、清除评价、正文全文搜索、导出、记录清理、app 端改动。

## Decisions

### D1 数据：给 `loves_ai_generation` 加评价列，不建新表
Liquibase `027-add-ai-generation-rating.sql`：`rating text`、`rating_tags jsonb NOT NULL DEFAULT '[]'`、`rating_remark text`、`rated_by text`、`rated_at timestamptz` + `ix_loves_ai_generation_rating (rating)`，带 rollback。
- app 实体不映射新列：`ddl-auto: validate` 只校验已映射列，app 的 `insert` 不含 `rating_tags`，靠 DEFAULT `'[]'` 满足 NOT NULL。app 测试库 create-drop 按实体建表，没有这些列也不影响 app 测试。
- 替代：评价独立表（一对一）。多一次 join、多一套实体，且产品已确认单一评价 → 不采用。

### D2 admin 实体：只读映射 + 原生 SQL 写评价
- `modules/ai/entity/AiGeneration`、`AiGenerationAttempt`：映射全部列（含评价列），所有列 `updatable = false, insertable = false`；admin 不插入、不经实体更新。
- 评价用 `@Modifying` 原生 SQL：
  `UPDATE loves_ai_generation SET rating=?, rating_tags=cast(? as jsonb), rating_remark=?, rated_by=?, rated_at=? WHERE id=? AND status <> 'PROCESSING'`
  只碰评价列，不会覆盖 app 回写的状态/结果列。影响 0 行 → 再查：不存在 404，否则（PROCESSING）409「生成中的记录不能评价」。
- 反向风险：app 回写走实体 `save`（merge）会覆盖整行吗？app 实体没映射评价列，merge 生成的 UPDATE 只含已映射列 → 不会抹掉评价。且 PROCESSING 期间不允许评价，回写前不存在评价。
- 替代：`@DynamicUpdate` + 实体更新。仍有并发读旧值覆盖风险 → 不采用。

### D3 查询：Specification + 北京时间日界
- 日期：`ZoneId Asia/Shanghai`；`startDate` → `startDate.atStartOfDay(zone)`（≥），`endDate` → `endDate.plusDays(1).atStartOfDay(zone)`（<）。都不传 → `today-6 ~ today`。`startDate > endDate` → 抛 `IllegalArgumentException("开始日期不能晚于结束日期")`，走现有 400 映射（中文 message 直出）。注入 `Clock`（admin 新建 `aiClock` Bean）以便 UT 固定"今天"。
- 枚举参数用 Spring 绑定到 Java enum（`AiGenerationStatusFilter`、`AiRatingFilter`、`AiRatingTag`），非法值 → `MethodArgumentTypeMismatchException` → 现有 400。错误文案沿用现有分支（"Invalid value for parameter …"），不改全局异常口径。日期用 `@DateTimeFormat(iso = DATE)` 的 `LocalDate`，格式错同样 400。
- `rating=NONE` → `rating IS NULL`；其余 → `rating = ?`。
- `tag` → `cb.isTrue(cb.function("jsonb_exists", Boolean.class, root.get(ratingTags), cb.literal(tag)))`（PostgreSQL `jsonb_exists(jsonb, text)`，即 `?` 运算符的函数形式，避免 JDBC 占位符冲突）。`ponytail:` 顺序扫描，数据量上来再加 GIN 索引。
- 排序固定 `createdAt DESC`；分页 `PageQuery.normalize`。
- `sceneName`：本页记录的 sceneId 批量查 `AiSceneRepository.findAllBySceneIdIn`；详情的 `templateName` 按 templateId 查模板。不建关联。

### D4 DTO
- `AiGenerationItemResponse`（11 字段）、`AiGenerationDetailResponse`（全部字段 + sceneName + templateName + attempts[]，jsonb 列转 `JsonNode`；`ratingTags` 转 `List<AiRatingTag>`）、`AiGenerationAttemptResponse`、`AiGenerationRatingRequest{rating @NotNull, tags, remark @Size(max=500)}`。
- tags 去重校验在 service（`new HashSet<>(tags).size() != tags.size()` → 400「问题标签不能重复」）。`tags` 为 null 视为 `[]`，`remark` 为空白视为 null。
- 评价枚举 `AiRating { USABLE, NEEDS_IMPROVEMENT, UNUSABLE }`；列表筛选另用 `AiRatingFilter`（多一个 `NONE`），避免把 NONE 写进库。

### D5 操作日志
`PUT /{id}/rating` 加 `@OperationLog("ai-generation:rate")`：target 取路径 UUID，payload 取请求体（含 rating/tags/remark），满足"谁何时改成什么"的追溯。操作日志页模块 / 动作中文映射不改（现行规则回落英文）。

### D6 web：扩展 FilterBar 三个可选能力，不另造筛选组件
`FilterBar` 新增（全部可选，现有调用方零改动）：
1. `FilterField.minField` / `maxField`（仅 date）：从另一个字段的当前值设置 flatpickr `minDate` / `maxDate`，实现"开始 ≤ 结束"的输入限制；
2. `onChange?(values)`：内部值变化时回调父组件，用于"模板版本依赖场景"——父组件据此重算⑤的 options 与 disabled，并在场景变化时清空模板；
3. `resetValues?: FilterValues`：Reset 时回到该值（默认 `{}`），用于"重置回最近 7 天"。
另加 `FilterField.disabled?: boolean`（⑤ 未选场景时不可选）。实现时「清空模板」改由 `FilterField.dependsOn`（依赖字段变化时 FilterBar 自行清空本字段）承担，`onChange` 只用于父组件重算⑤的 options / disabled——FilterBar 内部值不受父组件控制，父组件无法直接清空。
- 替代：生成记录页自写筛选表单。违反手册 §5 且重复 FilterBar 逻辑 → 不采用。

### D7 web 页面与组件映射

| 线框区域 | 组件 / 文件 |
|---|---|
| 侧栏「生成记录」 | `src/layout/AppSidebar.tsx`（AI 管理 subItems 第 4 项） |
| 路由 | `src/App.tsx`：`/ai/generations`、`/ai/generations/:id` |
| API | `src/api/aiGenerations.ts`：`listAiGenerations / getAiGeneration / rateAiGeneration` + 标签/评价/状态中文映射常量 |
| 页面1 ①~⑩ 筛选 | `src/pages/AiGenerations/List.tsx` + 扩展后的 `components/filter/FilterBar.tsx` |
| 页面1 列表 + ⑪ 行点击 | 同上：`DataTable onRowClick` + `Pagination` |
| 页面2 业务请求 / 输入快照 / 实际请求 | `src/pages/AiGenerations/Detail.tsx`（`JsonBlock` 内联小组件格式化 JSON） |
| 页面2 调用明细 + ⑯ 展开 | `src/pages/AiGenerations/AttemptTable.tsx` |
| 页面2 生成结果 | `Detail.tsx` |
| 页面2 人工评价 ⑫~⑮⑰ | `src/pages/AiGenerations/RatingPanel.tsx` |

- 状态中文：PROCESSING 生成中、SUCCESS 成功、REFUSED 拒答、EMPTY 空输出、FORMAT_ERROR 格式失败、TIMEOUT 超时、RATE_LIMITED 限流、UPSTREAM_ERROR 上游异常。评价中文：可用 / 需优化 / 不可用。触发原因：首次 / 重试 / 切备用。
- 时间显示沿用 `formatDateTime`（`pages/AiPrompts/promptVariables.ts`）；requestId 缩略取前 8 位 + `…`，`title` 显示全文。
- 详情页"模板 V3↗"链接 `/ai/prompt-templates/{templateId}`。

## 已定决策

| # | 决策 | 理由 |
|---|---|---|
| 1 | 每条记录一个评价，覆盖保存，不留历史（产品 2026-10-25） | 产品确认；追溯靠操作日志 |
| 2 | 评价不能清除 | 产品确认 |
| 3 | PROCESSING 不能评价：后端 409，前端置灰 | 产品确认 |
| 4 | startDate > endDate 后端返回 400「开始日期不能晚于结束日期」，前端日期互相限制 | 产品确认（2026-10-25 改为沿用现有参数校验口径） |
| 5 | 评价写入用原生 SQL 只更新评价列，带 `status <> 'PROCESSING'` 条件 | 避免与 app 回写互相覆盖 |
| 6 | 评价列加在原表，不建评价表 | 单一评价，免 join |
| 7 | 扩展 FilterBar 而非自写筛选 | 守手册 §5，现有调用方零改动 |
| 8 | 标签筛选用 `jsonb_exists`，暂不建 GIN 索引 | 数据量小；量大再加 |
| 9 | 非法枚举沿用现有 400 文案（"Invalid value for parameter …"） | 不为本 change 改全局异常口径 |
| 10 | remark 不传 / 空白 = 清空备注；tags 不传 = 空数组 | "整体覆盖"语义 |
| 11 | admin 只读实体用 `@Immutable`，不逐列写 `insertable/updatable=false` | 效果相同，一个注解 |
| 12 | 模板随场景清空用 `FilterField.dependsOn` | FilterBar 值非受控，父组件清不了 |

## Risks / Trade-offs

- [记录量增长后列表筛选变慢] → 已有 `(created_at DESC)`、`(scene_id, created_at DESC)` 索引 + 默认 7 天窗口；标签筛选再慢时加 GIN。
- [详情 raw_response / raw_output 可能很大] → 详情一次返回；调用明细⑯默认折叠，只在前端展开，不另分接口。
- [inputs 可能含用户敏感输入] → 仅登录运营可见，与 proposal 的权限口径一致；不导出。
- [评价并发：两人同时保存] → 后写覆盖先写（单一评价语义下可接受），操作日志可追溯两次操作。

## Migration Plan

1. admin 上线（Liquibase 027 纯加列 + 索引，可回滚）→ web 发布。app 无需重新部署。
2. 回滚：web / admin 回退上一版本；027 rollback 删除 5 列与索引（评价数据随之丢失，回滚前确认）。

## Open Questions

无。
