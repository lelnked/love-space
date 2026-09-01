## MODIFIED Requirements

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，返回**扁平数组**，按 `sortOrder` 升序、同序号按创建时间倒序，由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

每个条目的 `period` 字段 SHALL 为周期枚举**数组**，表示该条目的 **target**（`type` 与 `targetId` 都相同者视为同一 target）在全部可下发条目中被投放到的周期集合，去重后按 `MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL` 的枚举声明顺序排列。该集合 SHALL 在全部可下发条目（`online=true` 且关联实体可见）上计算，**不受本次请求的 `period` / `type` 查询参数影响**；不可下发的条目 SHALL NOT 贡献周期。同一 target 的多条条目 SHALL 各自独立下发（不去重、不合并），且其 `period` 数组内容相同。

接口 SHALL 支持两个可选查询参数，可同时使用：
- `period`（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）：传入时仅下发**自身所属周期**为该值的条目；不传时下发全部周期。过滤依据是条目自身持久化的所属周期，而非其 `period` 数组。
- `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该内容类型的条目；不传时下发全部类型。

传入非法的 `period` 或 `type` 值 SHALL 返回 400。过滤后无条目 SHALL 返回空数组。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 仅需活动 `online=true`（活动不关联城市）；`ROUTE` 仅需其爱女大使 `online=true`（与路线所属城市是否上架无关）；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：target 覆盖的周期集合、类型、banner 签名 URL、该类型的文案字段、关联实体 id `targetId`（单字段，指向哪张表由 `type` 判别，供 App 端自行决定跳转），以及关联实体的基础信息对象 `target`。

`target` SHALL 随条目一起下发，其字段形状按 `type` 判别，由 App 端自行解析；每种形状只含渲染推荐卡片所需的基础信息，不含详情内容：

| `type` | `target` 字段 |
|---|---|
| `ACTIVITY` | `id`、`title`、`cover`（活动首图签名 URL，活动无图时为 null）、`level` |
| `ROUTE` | `id`、`title`、`thumbnail`（签名 URL）、`cityName`、`ambassadorName` |
| `ARTICLE` | `id`、`title`、`coverTitle`、`image`（签名 URL） |

因条目仅在关联实体可见时才下发，被下发条目的 `target` SHALL NOT 为 null。`target` 的字段值 SHALL 取自关联实体本身，与条目上手填的文案字段（`title` / `subtitle` / `description` / `note`）相互独立、互不覆盖。

#### Scenario: 查询四个周期的推荐列表
- **GIVEN** 经期、排卵期下各有一条上线条目（关联不同 target），黄体期下有一条下线条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，响应为数组，含经期与排卵期两条条目，各自 `period` 数组分别为 `["MENSTRUAL"]` 与 `["OVULATION"]`，不含下线条目

#### Scenario: 同一 target 跨周期时下发全部周期
- **GIVEN** 同一个上线活动被配置在经期与黄体期下各一条上线条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，数组含这两条条目，两条的 `period` 均为 `["MENSTRUAL","LUTEAL"]`（按枚举声明顺序），两条的 banner 与文案仍为各自条目的配置

#### Scenario: 按周期过滤时 period 数组仍含其他周期
- **GIVEN** 同一个上线活动被配置在经期与黄体期下各一条上线条目
- **WHEN** app 端带 `period=LUTEAL` 查周期推荐接口
- **THEN** 返回 200，数组仅含黄体期那一条条目，其 `period` 为 `["MENSTRUAL","LUTEAL"]`

#### Scenario: 类型过滤不影响 period 数组
- **GIVEN** 同一个上线活动被配置在经期与黄体期下各一条上线条目
- **WHEN** app 端带 `type=ACTIVITY&period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组仅含经期那一条条目，其 `period` 为 `["MENSTRUAL","LUTEAL"]`

#### Scenario: 不可下发条目不贡献周期
- **GIVEN** 同一个上线活动被配置在经期（上线）与黄体期（下线）下各一条条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，数组仅含经期那一条条目，其 `period` 为 `["MENSTRUAL"]`（下线条目所属的黄体期不计入）

#### Scenario: 不同 target 的周期集合互不影响
- **GIVEN** 活动 A 被配置在经期与黄体期，活动 B 仅被配置在经期，均为上线且活动可见
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组含两条条目，A 的条目 `period` 为 `["MENSTRUAL","LUTEAL"]`，B 的条目 `period` 为 `["MENSTRUAL"]`

#### Scenario: 按周期过滤
- **GIVEN** 经期下有两条上线条目（关联不同 target，均只配在经期），卵泡期下有一条上线条目
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组仅含经期的两条条目（`period` 均为 `["MENSTRUAL"]`），不含卵泡期条目

#### Scenario: 周期与类型同时过滤
- **GIVEN** 经期下有 ACTIVITY、ARTICLE 各一条上线条目，卵泡期下有一条上线 ARTICLE 条目
- **WHEN** app 端带 `period=MENSTRUAL&type=ARTICLE` 查周期推荐接口
- **THEN** 返回 200，数组仅含经期的那条 ARTICLE 条目

#### Scenario: 按内容类型过滤
- **GIVEN** 同一周期下有 ACTIVITY、ROUTE、ARTICLE 各一条上线条目
- **WHEN** app 端带 `type=ARTICLE` 查周期推荐接口
- **THEN** 返回 200，数组仅含该 ARTICLE 条目，不含 ACTIVITY 与 ROUTE 条目

#### Scenario: 类型过滤后周期为空仍返回空数组
- **GIVEN** 仅经期下有一条 ACTIVITY 上线条目，其余周期无条目
- **WHEN** app 端带 `type=ROUTE` 查周期推荐接口
- **THEN** 返回 200，响应为空数组（不返回 404）

#### Scenario: 周期过滤后无条目返回空数组
- **GIVEN** 仅经期下有一条 ACTIVITY 上线条目，其余周期无条目
- **WHEN** app 端带 `period=LUTEAL` 查周期推荐接口
- **THEN** 返回 200，响应为空数组（不返回 404）

#### Scenario: 非法类型值被拒绝
- **GIVEN** 周期推荐接口
- **WHEN** app 端带 `type=UNKNOWN` 查询
- **THEN** 返回 400

#### Scenario: 非法周期值被拒绝
- **GIVEN** 周期推荐接口
- **WHEN** app 端带 `period=UNKNOWN` 查询
- **THEN** 返回 400

#### Scenario: 关联实体不可见时条目不下发
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动被下线（或被删除）
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目不出现在结果中

#### Scenario: 城市未上架不影响路线类条目
- **GIVEN** 一个上线的 ROUTE 类条目，其关联路线所属城市为下架状态，路线的爱女大使已上线
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目正常出现在结果中

#### Scenario: 大使下线连带隐藏路线类条目
- **GIVEN** 一个上线的 ROUTE 类条目，其关联路线的爱女大使被下线
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目不出现在结果中

#### Scenario: 组内按排序号升序
- **GIVEN** 经期下有 sortOrder 为 2、1、3 的三个上线条目
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 数组内条目按 1、2、3 顺序返回


#### Scenario: 活动类条目下发活动基础信息
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动上线且有图片、标题与难度等级
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目的 `target` 含活动 id、活动标题、首图签名 URL 与难度等级，且条目自身的推荐说明不受影响

#### Scenario: 路线类条目下发路线基础信息且不覆盖手填文案
- **GIVEN** 一个上线的 ROUTE 类条目，条目手填主标题与路线自身标题不同，路线有缩略图、城市名，其爱女大使已上线
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目的 `target` 含路线 id、路线自身标题、缩略图签名 URL、路线城市名与大使名称，条目的 `title` 仍为手填主标题

#### Scenario: 文章类条目下发文章基础信息
- **GIVEN** 一个上线的 ARTICLE 类条目，其关联文章上线且设置了封面标题与封面图
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目的 `target` 含文章 id、文章标题、封面标题与封面图签名 URL

#### Scenario: 活动无图片时 cover 为 null
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动上线但未上传任何图片
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目仍被下发，其 `target.cover` 为 null，其余字段正常
