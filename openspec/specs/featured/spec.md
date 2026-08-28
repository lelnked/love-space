# featured Specification

## Purpose
精选信息流：①精选·地图上新推荐——admin 端维护城市推荐条目，app 端只读信息流展示（需求文档 §7.1）；②周期推荐——按经期/卵泡期/排卵期/黄体期四周期配置的全局推荐，内容取自活动/路线/文章，app 端一次性下发四组由客户端自行选取。
## Requirements
### Requirement: 精选推荐管理
admin 端 SHALL 提供精选·地图上新推荐 CRUD：关联地图（城市）单选（创建时必选，创建后不可变）、banner 图片 1 张（必填，比例不做校验）、推荐说明（文本）、上线/下线状态。删除为物理删除。与既有 Banner 模块相互独立。

#### Scenario: 创建精选推荐
- **GIVEN** 存在上架城市
- **WHEN** 提交关联该城市、含 banner 图与推荐说明的推荐条目
- **THEN** 返回 200，详情含关联城市、banner 签名 URL、说明与状态

#### Scenario: 缺少必填项被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交缺 banner 图或城市不存在的推荐条目
- **THEN** 返回 400 及中文业务错误

#### Scenario: 精选推荐上下线切换
- **GIVEN** 一个上线推荐条目
- **WHEN** 将其下线
- **THEN** 返回 200，详情 online=false

### Requirement: App 端精选推荐查询
app 端 SHALL 提供只读的精选推荐信息流列表：对所有用户生效，按创建时间倒序，条目含 banner 签名 URL、推荐说明与关联城市数据（id 与名称，供 App 端自行决定跳转）。仅当条目上线**且**关联城市上架时可见。

#### Scenario: 查询精选推荐信息流
- **GIVEN** 上架城市下有一条上线推荐、一条下线推荐
- **WHEN** app 端查精选推荐列表
- **THEN** 返回 200，仅含上线条目，含 banner、说明与关联城市 id/名称

### Requirement: web 端精选推荐页面
web 端 SHALL 提供「精选推荐」后台页面：DataTable 列表（banner 图/关联城市/推荐说明/状态/操作）+ 弹窗表单（城市单选、banner 上传、说明文本）+ 上下线开关 + 删除确认弹窗。

#### Scenario: 精选推荐列表与上下线
- **GIVEN** 已登录后台且存在推荐条目
- **WHEN** 进入精选推荐页并切换某条目状态开关
- **THEN** 列表展示 banner、关联城市、说明，切换后状态即时更新并有成功提示

#### Scenario: 新增精选推荐
- **GIVEN** 已登录后台
- **WHEN** 在弹窗表单选择城市、上传 banner、填写说明并提交
- **THEN** 保存成功，列表出现新条目

### Requirement: 周期推荐条目管理
admin 端 SHALL 提供周期推荐条目 CRUD。每个条目 SHALL 归属恰好一个周期（`MENSTRUAL` 经期 / `FOLLICULAR` 卵泡期 / `OVULATION` 排卵期 / `LUTEAL` 黄体期，创建时必选、创建后不可变）与恰好一种内容类型（`ACTIVITY` / `ROUTE` / `ARTICLE`，创建时必选、创建后不可变），并带排序号 `sortOrder`（默认 0）与上线/下线状态（默认下线）。周期推荐 SHALL NOT 关联地图（城市），为全局配置。banner 图片必填 1 张，比例不做校验。删除为物理删除。

关联实体 SHALL 由单字段 `targetId` 表示，三种类型共用该字段，指向哪类实体由 `type` 判别（`ACTIVITY` 指向活动、`ROUTE` 指向路线、`ARTICLE` 指向文章）。`targetId` 为必填。

按内容类型的字段约束：

| 类型 | 必填 | 选填 |
|---|---|---|
| `ACTIVITY`（tripperclub活动） | `targetId`（活动）、推荐说明、banner 图 | 活动说明 |
| `ROUTE`（路线体验） | `targetId`（路线）、主标题、副标题、推荐说明、banner 图 | — |
| `ARTICLE`（周期生活法） | `targetId`（文章）、主标题、banner 图 | — |

`targetId` SHALL 在保存时按 `type` 分派到对应实体校验存在性；不属于该类型的文案字段一律忽略、不落库。

#### Scenario: 创建活动类周期推荐
- **GIVEN** 存在一个活动
- **WHEN** 提交 type=ACTIVITY、周期=经期、`targetId` 为该活动 id、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含周期、类型、`targetId` 与关联实体标题、推荐说明、banner 签名 URL、sortOrder 与 online=false

#### Scenario: 创建路线类周期推荐
- **GIVEN** 存在一条路线
- **WHEN** 提交 type=ROUTE、周期=排卵期、`targetId` 为该路线 id、手填主标题与副标题、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含手填的主标题与副标题、`targetId` 为该路线 id，且主副标题不取自路线实体

#### Scenario: 创建文章类周期推荐
- **GIVEN** 存在一篇文章
- **WHEN** 提交 type=ARTICLE、周期=黄体期、`targetId` 为该文章 id、主标题与 banner 图
- **THEN** 返回 200，详情含主标题、`targetId` 与关联实体标题、banner 签名 URL

#### Scenario: 缺少类型必填项被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交 type=ROUTE 但缺副标题，或 type=ACTIVITY 但缺推荐说明，或任一类型缺 banner 图
- **THEN** 返回 400 及中文业务错误

#### Scenario: 缺少 targetId 被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交任一类型的条目但不带 `targetId`
- **THEN** 返回 400 及中文业务错误

#### Scenario: 关联实体不存在被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交的 `targetId` 在该 `type` 对应的实体表中不存在
- **THEN** 返回 400 及中文业务错误（文案按类型区分「关联活动/路线/文章不存在」）

#### Scenario: 周期与类型创建后不可变
- **GIVEN** 一个 type=ACTIVITY、周期=经期的条目
- **WHEN** 提交更新请求（请求体不含周期与类型字段，或传入不同值），其余字段仍按该条目**持久化类型**的必填约束提供
- **THEN** 返回 200，条目的周期与类型保持不变；必填校验与 `targetId` 的存在性校验均按持久化类型执行，与请求体传入的类型无关

#### Scenario: 按周期过滤列表
- **GIVEN** 四个周期下各有若干条目
- **WHEN** 以周期=卵泡期查询分页列表
- **THEN** 返回 200，仅含该周期条目，按 sortOrder 升序

#### Scenario: 周期推荐上下线切换
- **GIVEN** 一个上线的周期推荐条目
- **WHEN** 将其下线
- **THEN** 返回 200，详情 online=false

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，返回**扁平数组**，按 `sortOrder` 升序、同序号按创建时间倒序，由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

每个条目的 `period` 字段 SHALL 为周期枚举**数组**，表示该条目的 **target**（`type` 与 `targetId` 都相同者视为同一 target）在全部可下发条目中被投放到的周期集合，去重后按 `MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL` 的枚举声明顺序排列。该集合 SHALL 在全部可下发条目（`online=true` 且关联实体可见）上计算，**不受本次请求的 `period` / `type` 查询参数影响**；不可下发的条目 SHALL NOT 贡献周期。同一 target 的多条条目 SHALL 各自独立下发（不去重、不合并），且其 `period` 数组内容相同。

接口 SHALL 支持两个可选查询参数，可同时使用：
- `period`（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）：传入时仅下发**自身所属周期**为该值的条目；不传时下发全部周期。过滤依据是条目自身持久化的所属周期，而非其 `period` 数组。
- `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该内容类型的条目；不传时下发全部类型。

传入非法的 `period` 或 `type` 值 SHALL 返回 400。过滤后无条目 SHALL 返回空数组。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 仅需活动 `online=true`（活动不关联城市）；`ROUTE` 仅需其爱女大使 `online=true`（与路线所属城市是否上架无关）；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：target 覆盖的周期集合、类型、banner 签名 URL、该类型的文案字段，以及关联实体 id `targetId`（单字段，指向哪张表由 `type` 判别，供 App 端自行决定跳转）。

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

### Requirement: web 端周期推荐页面
web 端 SHALL 提供「周期推荐」后台页面：顶部四个周期 Tab 切换，每个 Tab 下为 DataTable 列表（banner 图/内容类型/标题/关联实体/排序号/状态/操作）+ 新增弹窗表单 + 上下线开关 + 删除确认弹窗。表单 SHALL 先选内容类型，再按类型动态展示对应字段与对应实体的下拉选择器；`ARTICLE` 类型选中文章后主标题 SHALL 自动带出文章标题且可编辑。

#### Scenario: 周期 Tab 切换与列表展示
- **GIVEN** 已登录后台且各周期下有条目
- **WHEN** 进入周期推荐页并切换到卵泡期 Tab
- **THEN** 列表仅展示卵泡期条目，按排序号升序，含 banner 缩略图、类型、标题与状态

#### Scenario: 表单按类型切换字段
- **GIVEN** 已登录后台并打开新增弹窗
- **WHEN** 将内容类型从「tripperclub活动」切到「路线体验」
- **THEN** 表单字段切换为路线下拉、主标题、副标题、推荐说明、banner 上传，活动专属字段消失

#### Scenario: 文章类型自动带出主标题
- **GIVEN** 已登录后台并在新增弹窗选择内容类型「周期生活法」
- **WHEN** 在文章下拉中选中一篇文章
- **THEN** 主标题输入框自动填入该文章标题，且仍可手动改写

#### Scenario: 新增周期推荐
- **GIVEN** 已登录后台且处于经期 Tab
- **WHEN** 在弹窗表单选类型、选关联实体、填文案、上传 banner 并提交
- **THEN** 保存成功，经期列表出现新条目并有成功提示

#### Scenario: 周期推荐上下线与删除
- **GIVEN** 已登录后台且列表存在条目
- **WHEN** 切换某条目状态开关，再对另一条目点删除并确认
- **THEN** 状态即时更新，被删条目从列表消失，两步均有成功提示
