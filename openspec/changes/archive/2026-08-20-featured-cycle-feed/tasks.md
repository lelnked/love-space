## 1. 数据层（love-space-admin）

- [x] 1.1 新增 Liquibase changeset `db/changelog/changes/014-create-featured-cycle-item.sql`：建表 `loves_featured_cycle_item`（`id` uuid PK、`phase` text NOT NULL、`type` text NOT NULL、`sort_order` int NOT NULL DEFAULT 0、`online` boolean NOT NULL DEFAULT false、`activity_id`/`route_id`/`article_id` uuid 可空、`title`/`subtitle`/`description`/`note` text 可空、`banner` text NOT NULL、`created_at` TIMESTAMPTZ NOT NULL、`updated_at` TIMESTAMPTZ），加索引 `ix_loves_featured_cycle_item_phase (phase, sort_order)`，写 `--rollback DROP TABLE`；在 `db.changelog-master.yaml` include
- [x] 1.2 周期枚举**复用既有 `common/enums/Period`**（取值 `MENSTRUAL`/`FOLLICULAR`/`OVULATION`/`LUTEAL` 完全一致，admin 与 app 两端均已存在），不新建 `CyclePhase`；只新增 `modules/featuredcycle/entity/FeaturedCycleItemType.java`（`ACTIVITY`/`ROUTE`/`ARTICLE`）
- [x] 1.3 新增实体 `modules/featuredcycle/entity/FeaturedCycleItem.java`（字段同 1.1，枚举列用 `@Enumerated(EnumType.STRING)`，继承既有审计基类，无外键）与 `repository/FeaturedCycleItemRepository.java`（`JpaRepository` + `JpaSpecificationExecutor`，加 `findAllByOnlineTrueOrderBySortOrderAscCreatedAtDesc()`）

## 2. admin 后端接口（love-space-admin）

- [x] 2.1 新增 DTO：`FeaturedCycleItemUpsertRequest`（宽 record，字段与可空性对齐 `contracts/api-spec.json` 的同名 schema；`phase`/`type`/`banner` 加 `@NotNull`/`@NotBlank`，文案列加 `@Size` 长度约束）与 `FeaturedCycleItemResponse`（含 `phase`/`type`/`sortOrder`/`online`/banner 签名 URL/文案列/关联实体 id 与其标题、实体查不到时标题为 null 供前端标「已删除」）
- [x] 2.2 新增 `service/FeaturedCycleItemService.java`：`page(phase, type, pageable)` 按 `sortOrder` 升序；`detail`/`create`/`update`/`delete`/`setOnline`；`create` 校验 `phase`/`type` 非空，`update` 忽略请求中的 `phase`/`type`；banner 走 `ObjectKeyValidator.validateAndBind` + `ImageUrlSigner`（同 `FeaturedItemService`）
- [x] 2.3 在 `FeaturedCycleItemService` 内实现按 type 的分派校验与落库（`switch (type)`）：`ACTIVITY` 校验 activityId 存在 + description 必填、`ROUTE` 校验 routeId 存在 + title/subtitle/description 必填、`ARTICLE` 校验 articleId 存在 + title 必填；不属于该 type 的关联 id 与文案列一律置 null 再落库；错误抛 `IllegalArgumentException` 带中文消息
- [x] 2.4 新增 `controller/FeaturedCycleItemController.java`：`/api/admin/featured-cycle-items` 的 page/detail/create/update/delete/online 六个端点，路径、方法、参数严格对齐 `contracts/api-spec.json`；写操作加 `@OperationLog("featured-cycle-item:{create,update,delete,online}")`；上下线复用 `common.dto.OnlineStatusRequest`
- [x] 2.5 UT `FeaturedCycleItemServiceTest`：覆盖 `@scenario featured/周期推荐条目管理#创建活动类周期推荐`、`#创建路线类周期推荐`、`#创建文章类周期推荐`、`#缺少类型必填项被拒绝`、`#关联实体不存在被拒绝`、`#周期与类型创建后不可变`、`#按周期过滤列表`、`#周期推荐上下线切换`；另加一条断言「切类型后无关列被置空」

## 3. app 后端接口（love-space-app）

- [x] 3.1 在 `love-space-app` 镜像新增 `modules/featuredcycle/` 的 `entity`（含两个枚举）与 `repository`（只读，liquibase 在 app 端关闭，不建迁移）
- [x] 3.2 新增 `dto/FeaturedCycleFeedResponse`：四个周期键恒在的分组结构（`Map<Period, List<Item>>`，空周期为空数组），Item 含 `type`、banner 签名 URL、按 type 的文案字段与关联实体 id
- [x] 3.3 新增 `service/FeaturedCycleItemQueryService.list()`：捞上线条目后在内存过滤可见性——`ACTIVITY` 需活动 `online=true` 且其城市上架、`ROUTE` 需路线所属城市上架且其大使 `online=true`、`ARTICLE` 需文章 `online=true`，关联实体不存在即过滤；组内 `sortOrder` 升序、同序号 `createdAt` 倒序（模式同 `FeaturedItemQueryService`，加 `// ponytail:` 注释说明内存过滤的适用量级）
- [x] 3.4 新增 `controller/FeaturedCycleItemController.java`：`GET /api/app/featured-cycle-items`
- [x] 3.5 UT `FeaturedCycleItemQueryServiceTest`：覆盖 `@scenario featured/App 端周期推荐查询#查询四个周期的推荐列表`、`#关联实体不可见时条目不下发`、`#大使下线连带隐藏路线类条目`、`#组内按排序号升序`

## 4. web 前端（love-space-web）

- [x] 4.1 新增 `src/api/featuredCycleItems.ts`：六个 admin 端点的 client 与 TS 类型（`Period`/`FeaturedCycleItemType`/请求响应体），风格对齐 `api/featuredItems.ts`
- [x] 4.2 新增 `src/pages/FeaturedCycleItems/List.tsx` —— 线框区域「顶部四周期 Tab」：四个受控 Tab（经期/卵泡期/排卵期/黄体期），选中值绑定列表请求的 `phase` 参数，切换时重新拉取；带 loading 骨架
- [x] 4.3 `List.tsx` —— 线框区域「列表表格」：DataTable 列 = banner 缩略图 / 内容类型徽标 / 标题 / 关联实体名（响应中标题为 null 时显示「已删除」标记）/ 排序号 / 状态 Switch / 操作（编辑·删除）；空态文案「该周期暂无推荐」，请求失败展示错误提示与重试
- [x] 4.4 `List.tsx` —— 线框区域「上下线开关」与「删除确认弹窗」：Switch 调 `/online` 端点、成功提示后就地更新行；删除走确认弹窗，确认后调 DELETE 并刷新列表，两处失败均 toast 中文错误
- [x] 4.5 新增 `src/pages/FeaturedCycleItems/Form.tsx` —— 线框区域「新增/编辑弹窗」：顶部固定「内容类型」选择器（编辑态禁用）+ banner 上传（复用既有上传组件）+ 排序号输入；类型切换时清空下方字段块
- [x] 4.6 `Form.tsx` 类型字段块 —— `ACTIVITY`：活动下拉（调 `/api/admin/activities/page`）绑定 `activityId`、推荐说明 textarea 绑定 `description`（必填）、活动说明 textarea 绑定 `note`（选填）
- [x] 4.7 `Form.tsx` 类型字段块 —— `ROUTE`：路线下拉（调 `/api/admin/routes/page`）绑定 `routeId`、主标题绑 `title`、副标题绑 `subtitle`、推荐说明绑 `description`（四者均必填，前端做必填提示）
- [x] 4.8 `Form.tsx` 类型字段块 —— `ARTICLE`：文章下拉（调 `/api/admin/articles/page`）绑定 `articleId`，选中后把文章标题写入 `title` 输入框且保持可编辑
- [x] 4.9 在 `src/App.tsx` 注册路由 `/featured-cycle-items`，在 `src/layout/AppSidebar.tsx` 的「精选推荐」下方加「周期推荐」入口

## 5. 契约与登记

- [x] 5.1 核对 `contracts/api-spec.json` 与实现一致（design 阶段已写入七个 operation + 三个 schema，实现后逐条比对路径/参数/必填项，有偏差以实现为准回改契约）
- [x] 5.2 更新 `tests/modules.md` 的 `featured` 行：接口路径前缀补 `/api/admin/featured-cycle-items/*`、`/api/app/featured-cycle-items`，页面域补 `/love-space/featured-cycle-items`

## 6. 交付验证

- [x] 6.1 `./mvnw test` 在 admin 与 app 两端分别跑绿（UT）
- [x] 6.2 `/run-api-test --change featured-cycle-feed`（admin 与 app 的 IT 不并行跑）— 2026-08-20 实跑 19/19 ✅，存证 `test-evidence/featured-cycle-feed/`
- [x] 6.3 `/run-web-test --change featured-cycle-feed` — ⚠️ **环境不可用，待补**：远程 Playwright MCP `100.103.199.95:9233` 连接被拒，7 条 WEB 用例（WEB-001~007）未执行；被测前端与 admin 后端已验证可达。用户 2026-08-20 决定接受 WEB 未验证先归档，Playwright 恢复后跑 `/regression-test --module featured` 补齐
- [x] 6.4 `node scripts/generate-traceability-matrix.js --change featured-cycle-feed`，核对 `.quality-gate.yml` 逐项
