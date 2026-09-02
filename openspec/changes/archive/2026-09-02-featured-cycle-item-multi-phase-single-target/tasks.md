## 1. 数据库迁移

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/024-featured-cycle-item-multi-phase.sql`：加 `phases jsonb NOT NULL DEFAULT '[]'::jsonb` 列
- [x] 1.2 同一 changeset 内回填 `phases`：按 `(type, target_id)` 分组，把组内全部 `phase` 去重聚合成 jsonb 数组（用 `CASE` 显式按 `MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL` 定序），写到组内 `created_at` 最早那条
- [x] 1.3 删除组内非最早的重复条目；`DROP COLUMN phase`；建 `CREATE UNIQUE INDEX ux_loves_featured_cycle_item_target ON loves_featured_cycle_item (type, target_id)`
- [x] 1.4 写 rollback 语句（加回 `phase text` 取 `phases` 首元素回填、删唯一索引与 `phases`），changeset comment 注明合并掉的条目不可恢复
- [x] 1.5 在 `db/changelog/db.changelog-master.yaml` 末尾追加 `- include: {file: db/changelog/changes/024-featured-cycle-item-multi-phase.sql}`（沿用现有缩进写法），本地起 admin 验证迁移可跑通

## 2. 实体与仓储（admin + app 同步改）

- [x] 2.1 两端 `FeaturedCycleItem` 实体：`phase` 字段替换为 `@JdbcTypeCode(SqlTypes.JSON) @Column(name="phases", nullable=false, columnDefinition="jsonb") private List<Period> phases`，照抄 `Merchant.periods` 写法；javadoc 说明创建后可改
- [x] 2.2 admin `FeaturedCycleItemRepository` 新增 `boolean existsByTypeAndTargetIdAndIdNot(FeaturedCycleItemType type, UUID targetId, UUID id)` 与 `boolean existsByTypeAndTargetId(...)`

## 3. admin 后端

- [x] 3.1 `FeaturedCycleItemUpsertRequest`：`phase` → `@NotEmpty(message="投放周期不能为空") List<Period> phases`
- [x] 3.2 `FeaturedCycleItemResponse`：`phase` → `List<Period> phases`
- [x] 3.3 `FeaturedCycleItemService` 创建/更新：`phases` 去重并按 `Period` 枚举声明顺序落库（`EnumSet` 转 `List`）；更新时允许改 `phases` 与 `targetId`（新 `targetId` 仍走存在性校验与唯一性校验）、继续忽略 `type` 变更
- [x] 3.4 `FeaturedCycleItemService` 唯一性校验：创建前 `existsByTypeAndTargetId`、更新前 `existsByTypeAndTargetIdAndIdNot`，命中抛中文业务异常（按 type 分「该活动/路线/文章已存在周期推荐」）
- [x] 3.5 分页 Specification：`phase` 参数过滤由等值改为 `jsonb_exists(phases, :phase)`，照抄 `MerchantService` 既有写法
- [x] 3.6 UT：`admin` 端覆盖 `featured/周期推荐条目管理#创建多周期条目`、`#phases 为空被拒绝`、`#同一关联实体重复创建被拒绝`、`#下线条目同样占用唯一位`、`#更新条目自身不触发唯一冲突`、`#更新关联实体`、`#更新指向已被占用的实体被拒绝`、`#周期与类型创建后不可变`、`#按周期过滤列表`、`#不传周期返回全部条目`，测试方法加 `@scenario` 注释

## 4. app 后端

- [x] 4.1 `FeaturedCycleItemQueryService.feed`：删除 `TargetKey` 分组与 `periodsByTarget` 聚合；`period` 过滤改为 `item.getPhases().contains(period)`
- [x] 4.2 `toResponse`：`periods` 由 `EnumSet.copyOf(item.getPhases())` 构造（保留 EnumSet 以去重并按枚举声明顺序输出）；响应字段名 `period` 与形状保持不变
- [x] 4.3 `FeaturedCycleItemResponse` 的 `period` javadoc 改为「条目自身 phases，不跨条目聚合」
- [x] 4.4 UT：覆盖 `featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期`、`#按周期过滤时 period 数组仍含其他周期`、`#类型过滤不影响 period 数组`、`#不可下发条目不贡献周期`、`#不同 target 的周期集合互不影响`、`#按周期过滤`，测试方法加 `@scenario` 注释

## 5. web 前端

- [x] 5.1 `src/api/featuredCycleItems.ts`：`FeaturedCycleItem` 与 upsert 载荷的 `phase: Period` 改为 `phases: Period[]`；分页参数 `phase` 改为可选（`phase?: Period`）
- [x] 5.2 【线框①】`List.tsx` 页头与「新增周期推荐」按钮：跳转链接去掉 `?phase=` 查询串
- [x] 5.3 【线框②】`List.tsx` 用周期筛选下拉替换四个周期 tab：state 由 `useState<Period>("MENSTRUAL")` 改为 `useState<Period | "">("")`（默认「全部周期」），选项为「全部周期」+ 四周期；值为空时请求不带 `phase` 参数；切换时重置 `page` 到首页
- [x] 5.4 【线框③】`List.tsx` DataTable 新增「投放周期」列，用 `PERIOD_LABEL` 渲染 `phases` 的多个标签（按枚举顺序）；删除确认弹窗文案中的 `PERIOD_LABEL[it.phase]` 改为该条目的周期标签串；沿用现有空态/loading/错误提示组件，筛选后无数据走既有空态
- [x] 5.5 【线框④】`Form.tsx` 新增周期多选勾选框组（四个周期各一 checkbox），绑定 `phases`；一个都没勾时提交被阻止并提示「请至少选择一个投放周期」；编辑时回填已有 `phases`
- [x] 5.6 【线框⑤】`Form.tsx` 内容类型下拉与按类型动态字段逻辑不变，确认切换 type 时不重置 ④ 的勾选状态
- [x] 5.7 `Form.tsx` 提交失败时展示后端返回的中文业务错误（关联实体重复场景），弹窗不关闭

## 6. 契约与文档

- [x] 6.1 `contracts/api-spec.json` 已在 design 阶段更新，实现后复核字段与实际一致（`phases`、`phase` 参数「包含」语义、app `period` 描述）
- [x] 6.2 同步 `love-space-app/docs/openapi.json` 中 `/api/app/featured-cycle-items` 的 `period` 参数与响应描述

## 7. 交付验证

- [x] 7.1 `/run-api-test --change featured-cycle-item-multi-phase-single-target`
- [ ] 7.2 `/run-web-test --change featured-cycle-item-multi-phase-single-target` —— ⛔ 环境阻塞：Playwright MCP（playwright-company）ConnectionRefused，无浏览器可驱动
- [x] 7.3 `node scripts/generate-traceability-matrix.js --change featured-cycle-item-multi-phase-single-target`
- [x] 7.4 逐项过 `.quality-gate.yml`，`openspec validate featured-cycle-item-multi-phase-single-target`
