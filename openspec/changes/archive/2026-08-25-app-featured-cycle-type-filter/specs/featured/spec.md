## MODIFIED Requirements

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，一次性返回四个周期的完整列表（按周期分组，组内按 `sortOrder` 升序、同序号按创建时间倒序），由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

接口 SHALL 支持可选查询参数 `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该内容类型的条目，四周期分组键仍恒在（无条目的周期为空数组）；不传时下发全部类型。传入非法类型值 SHALL 返回 400。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 需活动 `online=true` 且所属城市上架；`ROUTE` 仅需其爱女大使 `online=true`（与路线所属城市是否上架无关）；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：类型、banner 签名 URL、该类型的文案字段，以及关联实体 id（供 App 端自行决定跳转）。

#### Scenario: 查询四个周期的推荐列表
- **GIVEN** 四个周期下各有上线条目，另有一条下线条目
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，四个周期键齐全（无条目的周期为空数组），仅含上线条目，不含下线条目

#### Scenario: 按内容类型过滤
- **GIVEN** 同一周期下有 ACTIVITY、ROUTE、ARTICLE 各一条上线条目
- **WHEN** app 端带 `type=ARTICLE` 查周期推荐接口
- **THEN** 返回 200，四个周期键仍齐全，结果中仅含该 ARTICLE 条目，不含 ACTIVITY 与 ROUTE 条目

#### Scenario: 类型过滤后周期为空仍返回空数组
- **GIVEN** 仅经期下有一条 ACTIVITY 条目，其余周期无条目
- **WHEN** app 端带 `type=ROUTE` 查周期推荐接口
- **THEN** 返回 200，四个周期键齐全且每个均为空数组

#### Scenario: 非法类型值被拒绝
- **GIVEN** 周期推荐接口
- **WHEN** app 端带 `type=UNKNOWN` 查询
- **THEN** 返回 400

#### Scenario: 关联实体不可见时条目不下发
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动被下线（或所属城市被下架）
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
- **WHEN** app 端查周期推荐接口
- **THEN** 经期分组内条目按 1、2、3 顺序返回
