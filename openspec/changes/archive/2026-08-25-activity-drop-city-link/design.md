## Context

活动实体自 `011-create-activity.sql` 起就带 `city_id not null` + 索引 `ix_loves_activity_city`，二期文档把城市更名为「地图」后，活动被定义为「关联地图（城市）」。该关联贯穿三端：admin 校验城市存在性并锁定不可变、app 端唯一的活动列表入口是 `?cityId=`、web 端表单有必填下拉、列表有筛选与列。

产品侧现确认活动是全局内容，不属于任何单一地图。schema 由 admin 端 Liquibase 统一管理（app 端 liquibase 关闭），因此迁移只在 admin 侧新增 changeset，app 端实体跟随。

## Goals / Non-Goals

**Goals:**
- 活动与地图（城市）在三端 + 数据库彻底解耦，无残留字段、参数、校验。
- app 端活动列表变为全局列表，可见性仅由活动自身 `online` 决定。
- 契约（`contracts/api-spec.json`）与二期需求文档同步修订，不留失真描述。

**Non-Goals:**
- 不为活动引入任何替代的归属维度（不挂路线、不挂商户）。
- 不改动精选模块——`loves_featured_cycle_item.activity_id` 只引用活动 id，与地图无关。
- 不改动城市（地图）自身的下架级联——它本就不含活动。
- 不做 API 版本化/灰度：admin 与 app 客户端随本次发布一同更新。

## Decisions

### 决策 1：app 端活动列表改为全局列表，而非保留被忽略的 cityId 参数
`GET /api/app/activities` 移除 `cityId` 参数（原为 `@RequestParam UUID cityId` 必填），返回全部 `online=true` 活动，按 `createdAt` 倒序。

*理由*：留一个被忽略的参数会让契约撒谎——调用方以为在筛选，实际拿到全量，是最容易埋 bug 的形态。用户已确认可与 App 端一同发版，无需兼容老客户端。
*备选*：保留参数但忽略（兼容老 App）——被否，理由同上。

### 决策 2：直接 DROP COLUMN，不保留可空列
新增 changeset `0NN-drop-activity-city.sql`：`DROP INDEX ix_loves_activity_city; ALTER TABLE loves_activity DROP COLUMN city_id;`，rollback 写 `ADD COLUMN city_id uuid` + 重建索引（结构可回滚，数据不可回滚，changeset comment 中写明）。

*理由*：用户明确确认地图归属数据可永久丢弃。留可空列会让实体、DTO、迁移三处长期背着一个没人读的字段。
*备选*：改可空保留数据——用户在门禁处已否决。
*前置*：由于不可逆，实施时先确认目标库不是生产库，或已有备份。

### 决策 3：admin 端更新语义由「cityId 不可变（忽略传入值）」变为「字段不存在」
`ActivityUpsertRequest` 直接删掉 `cityId` record 组件。Spring 默认 `FAIL_ON_UNKNOWN_PROPERTIES=false`，客户端误传 `cityId` 会被静默忽略而非 400——这正是 spec 中「请求体携带 cityId 不影响创建」场景所断言的行为。

### 决策 4：admin 列表接口保留 `cityId` 查询参数的兼容性？否
`GET /api/admin/activities/page` 直接删掉 `@RequestParam(required=false) UUID cityId`。Spring MVC 对未声明的查询参数默认忽略，故老前端若残留该参数也不会 400——spec 场景「活动列表不按城市过滤」断言的即此。

### 决策 5：删除两端 service 对 CityRepository 的依赖
- admin `ActivityService`：删构造注入的 `cityRepository`、`create()` 中的存在性校验、`page()` 中的 cityId 谓词、`toDetail/toItem` 中的 cityId 映射。
- app `ActivityQueryService`：删 `cityRepository` 字段与构造参数、`listByCity` 中的城市上架校验（方法更名 `listAll()`）、`detail()` 中的城市上架校验。repository 方法 `findAllByCityIdAndOnlineTrueOrderByCreatedAtDesc` → `findAllByOnlineTrueOrderByCreatedAtDesc`。

### 决策 6：contracts/api-spec.json 同步
5 个 operation 需改：`/api/admin/activities/page`（删 cityId parameter）、`/api/admin/activities` POST 与 `/api/admin/activities/{id}` PUT（`ActivityUpsertRequest` schema 删 cityId 属性与 required 项）、`/api/admin/activities/{id}` GET（响应 schema 删 cityId）、`/api/app/activities` GET（删必填 cityId parameter）、`/api/app/activities/{id}` GET（响应删 cityId）。各 operation 的 `x-requirement` 反链更新为 `activity/活动管理` 与 `activity/App 端活动查询`。

### 界面实现映射

| 变更点 | 文件 | 具体位置 |
|---|---|---|
| 列表筛选区去掉「所属地图（城市）」下拉 | `love-space-web/src/pages/Activities/List.tsx` | `filters.cityId` 组装（L24）、筛选项定义 `name: "cityId"`（L51） |
| 列表去掉「所属城市」列 | 同上 | 列定义 `key: "cityId"`（L134-137）、`cityName` useMemo（L60）及其城市列表拉取 |
| 表单去掉「所属地图（城市）」下拉与必填校验 | `love-space-web/src/pages/Activities/Form.tsx` | `cityId` state（L36）、回显（L68/L84 `getCity`）、校验（L107）、提交体（L120）、下拉渲染（L191-207） |

## Risks / Trade-offs

- **[数据不可逆] `city_id` 一经 drop，已有活动的地图归属无法恢复** → changeset 的 rollback 只恢复列结构不恢复数据，comment 中显式标注；实施前确认目标库有备份或非生产库。
- **[发布顺序] 后端先发、旧版 App 仍带 `?cityId=` 调用** → Spring MVC 忽略未声明参数，旧客户端不会 500，只是拿到全量活动（行为变宽而非报错），可接受。
- **[列表体积] app 端活动列表由「一城之内」变为全量，无分页** → 沿用现状（原接口也无分页），活动量级为运营手工维护的数十条，暂不引入分页。量级增长后再加。
- **[UT 改写] `ActivityQueryServiceTest` 现有用例全部围绕城市可见性构造** → 按新 spec 场景重写，不做机械删改。

## Migration Plan

1. admin 端新增 Liquibase changeset drop 列与索引（编号接现有最大值之后）。
2. 两端 Java 代码去 cityId；admin 与 app 的 `Activity` 实体同步（app 端 liquibase 关闭，仅跟随实体）。
3. web 端去表单项/筛选/列。
4. 更新 `contracts/api-spec.json` 与 `二期需求开发文档.md`。
5. 回滚：changeset rollback 恢复列结构（数据不恢复），代码回滚至本 change 之前的提交。

## Open Questions

无——两项实质决策（app 列表形态、列的处置）已在提案前由用户确认。
