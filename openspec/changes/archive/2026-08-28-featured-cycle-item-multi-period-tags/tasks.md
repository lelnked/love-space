## 1. 数据库迁移（love-space-admin 统一管理 schema）

- [x] 1.1 上线前核查脏数据：`SELECT count(*) FROM loves_featured_cycle_item WHERE COALESCE(activity_id, route_id, article_id) IS NULL` 必须为 0，非 0 先人工处理
- [x] 1.2 新建 `love-space-admin/src/main/resources/db/changelog/changes/015-merge-featured-cycle-item-target-id.sql`：加 `target_id uuid` → 回填 `COALESCE(activity_id, route_id, article_id)` → 置 `NOT NULL` → 删三列
- [x] 1.3 同 changeset 写 `--rollback`：加回三列、按 `type` 用 CASE 把 `target_id` 分派回对应列、删 `target_id`
- [x] 1.4 在测试库跑一遍 forward + rollback，确认数据无损、索引 `ix_loves_featured_cycle_item_phase` 完好

## 2. 实体与 DTO 合并为 targetId

- [x] 2.1 admin `entity/FeaturedCycleItem.java`：三个 UUID 字段合并为 `targetId`（`@Column(name = "target_id", nullable = false)`），更新类 javadoc
- [x] 2.2 app `entity/FeaturedCycleItem.java`：同上，两端实体保持镜像
- [x] 2.3 admin `dto/FeaturedCycleItemUpsertRequest.java`：三字段合并为 `@NotNull(message = "关联实体不能为空") UUID targetId`，更新 param javadoc
- [x] 2.4 admin `dto/FeaturedCycleItemResponse.java`：三字段合并为 `targetId`，`relatedTitle` 保留不变
- [x] 2.5 app `dto/FeaturedCycleItemResponse.java`：三字段合并为 `targetId`，同时把 `period` 由 `Period` 改为 `List<Period>`，更新 javadoc（`period` 语义改为「该 target 覆盖的周期集合，按枚举声明顺序」）

## 3. admin 服务层

- [x] 3.1 `FeaturedCycleItemService`：按 `type` 分派的三段 `requireId` + `existsById` 收敛为一段——取 `request.targetId()`，按 `type` 选 repository 校验存在性，错误文案保持「关联活动/路线/文章不存在：{id}」
- [x] 3.2 确认更新路径仍按**持久化类型**（而非请求体传入的 type）分派 `targetId` 存在性校验，锚定 `@scenario featured/周期推荐条目管理#周期与类型创建后不可变`
- [x] 3.3 确认 `targetId` 缺失时返回 400 中文业务错误，锚定 `@scenario featured/周期推荐条目管理#缺少 targetId 被拒绝`

## 4. app 周期聚合实现

- [x] 4.1 `FeaturedCycleItemQueryService`：新增按 `(type, targetId)` 二元组的 target key 提取（record 或 `Map.entry`，不再三选一）
- [x] 4.2 `feed`：在可见性过滤之后、`period`/`type` 参数过滤之前，用 `groupingBy` 建 `Map<targetKey, EnumSet<Period>>`（`EnumSet` 天然去重且按声明顺序迭代，不额外排序）
- [x] 4.3 `toResponse` 改为接受聚合 map，按 target key 取出周期集合转 `List<Period>`；确认参数过滤只作用于结果集、不影响已算好的聚合
- [x] 4.4 复核排序未受影响：仍是仓储层 `sortOrder` 升序、同序号 `createdAt` 倒序，聚合步骤不得改变 stream 顺序

## 5. web 后台前端

- [x] 5.1 `src/api/featuredCycleItems.ts`：响应类型与 upsert 载荷的三个 id 字段合并为 `targetId`
- [x] 5.2 `src/pages/FeaturedCycleItems/Form.tsx`：三个 `useState` 合并为单个 `targetId`；回填详情、提交载荷、校验 key 一并收敛
- [x] 5.3 三个实体下拉选择器仍按 `type` 切换渲染，但 `value`/`onChange` 统一绑定 `targetId`；切换类型时清空 `targetId`（避免把活动 id 带进路线下拉）
- [x] 5.4 校验错误 key 统一为 `targetId`，三种类型的错误文案仍分别为「请选择关联活动/路线/文章」
- [x] 5.5 确认表单交互与线框无变化（先选类型→按类型展示字段→ARTICLE 选中文章自动带出主标题）

## 6. 接口契约同步

- [x] 6.1 `contracts/api-spec.json`：`FeaturedCycleItemUpsertRequest` schema 的 `activityId`/`routeId`/`articleId` 合并为必填 `targetId`
- [x] 6.2 `/api/app/featured-cycle-items` `get.summary` 改写：`period` 响应字段为该 target 覆盖的周期数组、条目不去重、关联 id 为单字段 `targetId`
- [x] 6.3 同一 operation 的 `period` 查询参数 description 补明「过滤按条目自身所属周期，与响应的 `period` 数组不是同一语义」
- [x] 6.4 确认两个 `x-requirement` 反链仍为 `featured/周期推荐条目管理` 与 `featured/App 端周期推荐查询`（Requirement 名未改）

## 7. 单元测试（UT，`@scenario` 注释锚定）

- [x] 7.1 `FeaturedCycleItemServiceTest`（admin）：三个 id 字段的构造与断言改为 `targetId`，锚定 `#创建活动类周期推荐`、`#创建路线类周期推荐`、`#创建文章类周期推荐`
- [x] 7.2 admin 新增 UT：缺 `targetId` 返回 400，`@scenario featured/周期推荐条目管理#缺少 targetId 被拒绝`
- [x] 7.3 admin UT：`targetId` 在对应类型实体表中不存在时 400，`@scenario featured/周期推荐条目管理#关联实体不存在被拒绝`
- [x] 7.4 `FeaturedCycleItemQueryServiceTest`（app）：既有断言 `period` 单值的用例改数组断言，三个 id 断言改 `targetId`
- [x] 7.5 app 新增 UT：同一活动配在经期与黄体期，两条均返回且 `period` 均为 `[MENSTRUAL, LUTEAL]`，`@scenario featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期`
- [x] 7.6 app 新增 UT：带 `period=LUTEAL` 时仅返回黄体期那条，其 `period` 仍为 `[MENSTRUAL, LUTEAL]`，`@scenario featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期`
- [x] 7.7 app 新增 UT：带 `type=ACTIVITY&period=MENSTRUAL` 时聚合不受类型参数影响，`@scenario featured/App 端周期推荐查询#类型过滤不影响 period 数组`
- [x] 7.8 app 新增 UT：黄体期那条下线时经期条目的 `period` 为 `[MENSTRUAL]`，`@scenario featured/App 端周期推荐查询#不可下发条目不贡献周期`
- [x] 7.9 app 新增 UT：活动 A 跨两周期、活动 B 仅一周期时各自数组互不影响，`@scenario featured/App 端周期推荐查询#不同 target 的周期集合互不影响`
- [x] 7.10 `FeaturedCycleItemControllerWebMvcTest`（app）：`period` 的 JSON 断言由字符串改数组（断言元素与顺序），关联 id 断言改 `targetId`；400 与空数组用例不变

## 8. 交付验证

- [x] 8.1 跑 admin UT：`mvn test`（带 `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:25432/love_space` 等环境变量），全绿
- [x] 8.2 跑 app UT：`mvn test`（带 `APP_SECURITY_API_KEYS` 与独占测试库），全绿；两后端不并行跑
- [x] 8.3 跑 IT：`/run-api-test --change featured-cycle-item-multi-period-tags`
- [ ] 8.4 跑 WEB：`/run-web-test --change featured-cycle-item-multi-period-tags`（表单行为未变，属回归确认）
- [x] 8.5 ~~刷新追溯矩阵~~ 不适用：本仓库无 `scripts/generate-traceability-matrix.js`（session protocol 提及但项目未落地该脚本）
- [x] 8.6 跑 `openspec validate featured-cycle-item-multi-period-tags`（本仓库无 `.quality-gate.yml`，该项不适用）
