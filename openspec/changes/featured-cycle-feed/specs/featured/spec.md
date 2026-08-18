## ADDED Requirements

### Requirement: 周期推荐条目管理
admin 端 SHALL 提供周期推荐条目 CRUD。每个条目 SHALL 归属恰好一个周期（`MENSTRUAL` 经期 / `FOLLICULAR` 卵泡期 / `OVULATION` 排卵期 / `LUTEAL` 黄体期，创建时必选、创建后不可变）与恰好一种内容类型（`ACTIVITY` / `ROUTE` / `ARTICLE`，创建时必选、创建后不可变），并带排序号 `sortOrder`（默认 0）与上线/下线状态（默认下线）。周期推荐 SHALL NOT 关联地图（城市），为全局配置。banner 图片必填 1 张，比例不做校验。删除为物理删除。

按内容类型的字段约束：

| 类型 | 必填 | 选填 |
|---|---|---|
| `ACTIVITY`（tripperclub活动） | 关联活动 id、推荐说明、banner 图 | 活动说明 |
| `ROUTE`（路线体验） | 关联路线 id、主标题、副标题、推荐说明、banner 图 | — |
| `ARTICLE`（周期生活法） | 关联文章 id、主标题、banner 图 | — |

关联实体 id SHALL 在保存时校验存在性；不属于该类型的字段一律忽略、不落库。

#### Scenario: 创建活动类周期推荐
- **GIVEN** 存在一个活动
- **WHEN** 提交 type=ACTIVITY、周期=经期、关联该活动、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含周期、类型、关联活动 id 与标题、推荐说明、banner 签名 URL、sortOrder 与 online=false

#### Scenario: 创建路线类周期推荐
- **GIVEN** 存在一条路线
- **WHEN** 提交 type=ROUTE、周期=排卵期、关联该路线、手填主标题与副标题、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含手填的主标题与副标题、关联路线 id，且主副标题不取自路线实体

#### Scenario: 创建文章类周期推荐
- **GIVEN** 存在一篇文章
- **WHEN** 提交 type=ARTICLE、周期=黄体期、关联该文章、主标题与 banner 图
- **THEN** 返回 200，详情含主标题、关联文章 id 与标题、banner 签名 URL

#### Scenario: 缺少类型必填项被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交 type=ROUTE 但缺副标题，或 type=ACTIVITY 但缺推荐说明，或任一类型缺 banner 图
- **THEN** 返回 400 及中文业务错误

#### Scenario: 关联实体不存在被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交的关联活动/路线/文章 id 在库中不存在
- **THEN** 返回 400 及中文业务错误

#### Scenario: 周期与类型创建后不可变
- **GIVEN** 一个 type=ACTIVITY、周期=经期的条目
- **WHEN** 提交更新请求（请求体不含周期与类型字段，或传入不同值）
- **THEN** 返回 200，条目的周期与类型保持不变

#### Scenario: 按周期过滤列表
- **GIVEN** 四个周期下各有若干条目
- **WHEN** 以周期=卵泡期查询分页列表
- **THEN** 返回 200，仅含该周期条目，按 sortOrder 升序

#### Scenario: 周期推荐上下线切换
- **GIVEN** 一个上线的周期推荐条目
- **WHEN** 将其下线
- **THEN** 返回 200，详情 online=false

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，一次性返回四个周期的完整列表（按周期分组，组内按 `sortOrder` 升序、同序号按创建时间倒序），由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 需活动 `online=true` 且所属城市上架；`ROUTE` 需路线所属城市上架且其爱女大使 `online=true`；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：类型、banner 签名 URL、该类型的文案字段，以及关联实体 id（供 App 端自行决定跳转）。

#### Scenario: 查询四个周期的推荐列表
- **GIVEN** 四个周期下各有上线条目，另有一条下线条目
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，四个周期键齐全（无条目的周期为空数组），仅含上线条目，不含下线条目

#### Scenario: 关联实体不可见时条目不下发
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动被下线（或所属城市被下架）
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目不出现在结果中

#### Scenario: 大使下线连带隐藏路线类条目
- **GIVEN** 一个上线的 ROUTE 类条目，其关联路线的爱女大使被下线
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目不出现在结果中

#### Scenario: 组内按排序号升序
- **GIVEN** 经期下有 sortOrder 为 2、1、3 的三个上线条目
- **WHEN** app 端查周期推荐接口
- **THEN** 经期分组内条目按 1、2、3 顺序返回

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
