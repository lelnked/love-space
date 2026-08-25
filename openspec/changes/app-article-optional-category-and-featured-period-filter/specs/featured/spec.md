## MODIFIED Requirements

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，返回**扁平数组**（每个条目带 `period` 字段标识所属周期），按 `sortOrder` 升序、同序号按创建时间倒序，由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

接口 SHALL 支持两个可选查询参数，可同时使用：
- `period`（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）：传入时仅下发该周期的条目；不传时下发全部周期。
- `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该内容类型的条目；不传时下发全部类型。

传入非法的 `period` 或 `type` 值 SHALL 返回 400。过滤后无条目 SHALL 返回空数组。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 仅需活动 `online=true`（活动不关联城市）；`ROUTE` 仅需其爱女大使 `online=true`（与路线所属城市是否上架无关）；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：所属周期、类型、banner 签名 URL、该类型的文案字段，以及关联实体 id（供 App 端自行决定跳转）。

#### Scenario: 查询四个周期的推荐列表
- **GIVEN** 经期、排卵期下各有一条上线条目，黄体期下有一条下线条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，响应为数组，含经期与排卵期两条条目且各自 `period` 字段正确，不含下线条目

#### Scenario: 按周期过滤
- **GIVEN** 经期下有两条上线条目，卵泡期下有一条上线条目
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组仅含经期的两条条目（`period` 均为 `MENSTRUAL`），不含卵泡期条目

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
