## 1. admin：数据库与实体

- [x] 1.1 Liquibase `027-add-ai-generation-rating.sql`：`loves_ai_generation` 加 `rating`、`rating_tags jsonb NOT NULL DEFAULT '[]'`、`rating_remark`、`rated_by`、`rated_at` + `ix_loves_ai_generation_rating`，带 rollback；master include
- [x] 1.2 `modules/ai/entity/AiGeneration`、`AiGenerationAttempt`：全列只读映射（实现用 `@Immutable`，等效逐列 `insertable=false, updatable=false`），jsonb 列 `columnDefinition = "jsonb"`
- [x] 1.3 枚举：`AiRating`、`AiRatingFilter`（含 NONE）、`AiRatingTag`（8 个）、`AiGenerationStatusFilter`（PROCESSING + 7 个终态）
- [x] 1.4 Repository：`AiGenerationRepository extends JpaRepository, JpaSpecificationExecutor`（+ 原生 SQL `rate(...) WHERE id=? AND status <> 'PROCESSING'`，`@Transactional @Modifying`）；`AiGenerationAttemptRepository.findAllByGenerationIdOrderBySeqAsc`；`AiSceneRepository.findAllBySceneIdIn`
- [x] 1.5 `aiClock` Bean（Asia/Shanghai）供日期默认值计算

## 2. admin：查询、详情、评价

- [x] 2.1 DTO：`AiGenerationItemResponse`（11 字段）、`AiGenerationDetailResponse`、`AiGenerationAttemptResponse`、`AiGenerationRatingRequest{rating @NotNull, tags, remark @Size(max=500)}`，中文校验 message、javadoc
- [x] 2.2 `AiGenerationService.page`：北京时间日界（默认今天-6~今天、单边过滤、start>end 抛 400「开始日期不能晚于结束日期」）、requestId/anonymousUserId/sceneId/templateId/model/status 精确、rating（NONE→IS NULL）、tag（`jsonb_exists`）、`createdAt DESC`、`PageQuery.normalize`；本页 sceneName 批量查
- [x] 2.3 `AiGenerationService.detail`：404；templateName；attempts 按 seq 升序；jsonb → JsonNode，ratingTags → `List<AiRatingTag>`
- [x] 2.4 `AiGenerationService.rate`：tags 去重校验 400、null tags → `[]`、空白 remark → null；原生 SQL 更新；0 行 → 不存在 404 / PROCESSING 409「生成中的记录不能评价」；ratedBy 取 `OperatingContext.currentUsername()`
- [x] 2.5 `AiGenerationController`（`/api/admin/ai/generations`：`page`、`/{id}`、`PUT /{id}/rating` 204 + `@OperationLog("ai-generation:rate")`），`@DateTimeFormat(iso = DATE)` 的 LocalDate 参数，中文 javadoc
- [x] 2.6 UT `AiGenerationQueryTest`（固定 Clock，JdbcTemplate 插数据控制 created_at）：`// @scenario: ai/生成记录列表查询#默认最近 7 天`、`#日期区间含两端整日`、`#开始晚于结束被拒绝`、`#按未评价筛选`、`#按问题标签筛选`、`#组合筛选与倒序`、`#非法枚举被拒绝`、`#列表不含正文`
- [x] 2.7 UT 详情：`// @scenario: ai/生成记录详情#详情含调用明细`、`#未评价记录的评价字段为空`、`#记录不存在`
- [x] 2.8 UT 评价：`// @scenario: ai/生成记录人工评价#首次评价`、`#再次评价覆盖原评价`、`#生成中的记录不能评价`、`#失败记录可以评价`、`#评价参数非法`；remark 恰 500 字符通过
- [x] 2.9 UT 留痕与权限：`// @scenario: ai/生成记录人工评价#评价留痕`、`ai/AI 管理写操作留痕#发布留痕`（回归保持）、`operation-log/运营写操作留痕#AI 生成记录评价留痕`、`ai/AI 管理权限与菜单#MEMBER 可评价生成记录`

## 3. web：FilterBar 扩展（D6）

- [x] 3.1 `components/filter/FilterBar.tsx`：`FilterField.minField / maxField`（date 字段按另一字段当前值设 flatpickr `minDate` / `maxDate`，随值变化 `fp.set`）、`FilterField.disabled`、`FilterField.dependsOn`（依赖字段变化时清空本字段，用于模板随场景清空）、`onChange?(values)`、`resetValues?`（Reset 回到该值并以其调用 onReset）；现有调用方不传则行为不变
- [x] 3.2 回归检查：`/logs`、`/managers` 等现有 FilterBar 页面筛选 / 重置行为不变（tsc 已过；手工点验 2026-10-26 移交用户随页面测试一并执行）

## 4. web：API 与外壳

- [x] 4.1 `src/api/aiGenerations.ts`：类型逐字对齐后端 DTO；`listAiGenerations / getAiGeneration / rateAiGeneration`；`STATUS_LABEL`、`RATING_LABEL`、`RATING_TAG_LABEL`、`TRIGGER_REASON_LABEL` 中文映射
- [x] 4.2 `AppSidebar.tsx` AI 管理 subItems 加「生成记录」`/ai/generations`（第 4 项）（`ai/AI 管理权限与菜单#侧栏菜单`）
- [x] 4.3 `App.tsx` 注册 `/ai/generations`、`/ai/generations/:id`

## 5. web：生成记录列表页（页面 1）

- [x] 5.1 `pages/AiGenerations/List.tsx` ①时间范围：两个 date 字段 `startDate`（`maxField: "endDate"`）、`endDate`（`minField: "startDate"`），初始值 = 最近 7 天（今天-6 ~ 今天，本地日期），`resetValues` 同此（`#默认展示最近 7 天`、`#时间范围互相限制`）
- [x] 5.2 ②请求 ID、③匿名用户 ID 文本框（精确，placeholder 注明）
- [x] 5.3 ④场景 select（`listAiSceneOptions`，加载失败 toast）；⑤模板版本 select：`onChange` 监听场景变化 → `listAiPromptTemplates(sceneId, 1, 200)` 生成 `V{seq} {name}` 选项，未选场景 `disabled`，场景变化清空模板值（`#模板版本依赖场景`）；⑥模型 select（`listAiModels`）
- [x] 5.4 ⑦状态、⑧评价（含「未评价」=NONE）、⑨问题标签 select；⑩查询 / 重置回第 1 页（`#按未评价筛选`）
- [x] 5.5 列表 `DataTable`：时间、请求 ID（前 8 位 + …，title 全文）、匿名用户（缩略 + title）、场景名称、版本 V{seq}、实际模型（空「—」）、状态（中文；成功 ✓ 绿、其余 ✗ 红、生成中灰）、耗时（秒，1 位小数，空「—」）、评价（中文，空「—」）；`onRowClick` → `/ai/generations/{id}`（`#点击行进入详情`）；loading / 空态「暂无生成记录」/ 错误 toast
- [x] 5.6 `Pagination` 表格右下，默认 20、可切 200

## 6. web：生成记录详情页（页面 2）

- [x] 6.1 `pages/AiGenerations/Detail.tsx` 外壳：`getAiGeneration(id)`，加载中 / 失败 toast 回列表；标题「生成记录 › {id 前 8 位}…」；返回按钮
- [x] 6.2 业务请求区：requestId、clientRequestId、匿名用户、场景（名称 + sceneId）、模板 `V{seq} {templateName}` `<Link>` 到 `/ai/prompt-templates/{templateId}`、App 版本（空「—」）、状态中文、实际模型、尝试次数、创建 / 完成时间、耗时、tokens（`prompt/completion`，空「—」）
- [x] 6.3 输入快照区（inputs JSON）、实际请求区（requestMessages + requestParams JSON），`<pre>` 格式化、可滚动
- [x] 6.4 `AttemptTable.tsx` 调用明细：#、触发原因中文、模型、状态中文、错误码（空「—」）、供应商请求 ID（缩略）、开始时间、耗时、tokens；⑯行尾「原始返回」展开 / 收起，展示 rawResponse 与 validationError（`#展开调用原始返回`）
- [x] 6.5 生成结果区：最终 content（格式化 JSON，空「—」）、最终模型原始输出 rawOutput（空「—」）
- [x] 6.6 `RatingPanel.tsx` ⑫评价单选（可用 / 需优化 / 不可用）、⑬8 个问题标签多选、⑭备注 textarea（maxLength 500，字数提示）；回填当前评价；⑮未评价「保存评价」/ 已评价「修改评价」，未选评价时禁用、提交中禁用；成功 toast + 重新拉详情；错误 toast 后端 message（`#未评价记录保存评价`、`#修改已有评价`）
- [x] 6.7 ⑰已评价显示「最后评价：{ratedBy} · {ratedAt}」，未评价不显示；页面不出现评价历史
- [x] 6.8 状态为 PROCESSING 时⑫~⑮全部 disabled，并显示「生成中的记录不能评价」（`#生成中记录评价区置灰`）

## 7. 收尾自检

- [x] 7.1 admin `./mvnw test` 全绿（带 25432 库地址）；app 无改动但用 admin 迁移后的库以 `ddl-auto=validate` 起一次 app 确认 027 不影响
- [x] 7.2 `love-space-web` `tsc -b` + `npm run lint` 无错误，`vite build --mode test` 通过
- [x] 7.3 `contracts/api-spec.json` 与实现路径 / 参数 / 字段核对一致
