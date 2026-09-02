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
admin 端 SHALL 提供周期推荐条目 CRUD。每个条目 SHALL 归属**一个或多个**周期（`MENSTRUAL` 经期 / `FOLLICULAR` 卵泡期 / `OVULATION` 排卵期 / `LUTEAL` 黄体期），由多值字段 `phases` 表示，创建时至少选一个、创建后**可修改**；并归属恰好一种内容类型（`ACTIVITY` / `ROUTE` / `ARTICLE`，创建时必选、创建后不可变），并带排序号 `sortOrder`（默认 0）与上线/下线状态（默认下线）。`phases` 为空数组或缺省 SHALL 返回 400。`phases` 内重复的周期值 SHALL 去重后落库，落库顺序按 `MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL` 的枚举声明顺序。周期推荐 SHALL NOT 关联地图（城市），为全局配置。banner 图片必填 1 张，比例不做校验。删除为物理删除。

关联实体 SHALL 由单字段 `targetId` 表示，三种类型共用该字段，指向哪类实体由 `type` 判别（`ACTIVITY` 指向活动、`ROUTE` 指向路线、`ARTICLE` 指向文章）。`targetId` 为必填。

同一关联实体 SHALL 全局至多存在一条周期推荐条目：`(type, targetId)` 组合 SHALL 唯一。创建或更新时若该组合已被**其他**条目占用，SHALL 返回 400 及中文业务错误「该活动/路线/文章已存在周期推荐」（文案按类型区分）。该约束 SHALL 由数据库唯一约束与服务端校验共同保证，与条目的上下线状态无关（下线条目同样占位）。

按内容类型的字段约束：

| 类型 | 必填 | 选填 |
|---|---|---|
| `ACTIVITY`（tripperclub活动） | `targetId`（活动）、推荐说明、banner 图 | 活动说明 |
| `ROUTE`（路线体验） | `targetId`（路线）、主标题、副标题、推荐说明、banner 图 | — |
| `ARTICLE`（周期生活法） | `targetId`（文章）、主标题、banner 图 | — |

`targetId` SHALL 在保存时按 `type` 分派到对应实体校验存在性；不属于该类型的文案字段一律忽略、不落库。`targetId` 创建后**可修改**——改为同 `type` 下的另一个实体即可，新值仍受存在性校验与 `(type, targetId)` 唯一约束约束；改动 `targetId` 后原实体的唯一位随之释放。

列表分页接口 SHALL 支持可选的 `phase` 查询参数，语义为「条目的 `phases` **包含**该周期」；不传时返回全部条目。列表按 `sortOrder` 升序、同序号按创建时间倒序返回，列表项含 `phases` 数组。

#### Scenario: 创建活动类周期推荐
- **GIVEN** 存在一个尚未被任何周期推荐引用的活动
- **WHEN** 提交 type=ACTIVITY、`phases=["MENSTRUAL"]`、`targetId` 为该活动 id、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含 `phases=["MENSTRUAL"]`、类型、`targetId` 与关联实体标题、推荐说明、banner 签名 URL、sortOrder 与 online=false

#### Scenario: 创建多周期条目
- **GIVEN** 存在一条尚未被引用的路线
- **WHEN** 提交 type=ROUTE、`phases=["LUTEAL","MENSTRUAL"]`、`targetId` 为该路线 id 及该类型的全部必填字段
- **THEN** 返回 200，详情 `phases` 为 `["MENSTRUAL","LUTEAL"]`（按枚举声明顺序），该条目在按经期与按黄体期过滤的列表中均出现

#### Scenario: phases 为空被拒绝
- **GIVEN** 已登录 admin
- **WHEN** 提交 `phases` 为空数组或不带 `phases` 的条目
- **THEN** 返回 400 及中文业务错误

#### Scenario: 创建路线类周期推荐
- **GIVEN** 存在一条尚未被引用的路线
- **WHEN** 提交 type=ROUTE、`phases=["OVULATION"]`、`targetId` 为该路线 id、手填主标题与副标题、含推荐说明与 banner 图的条目
- **THEN** 返回 200，详情含手填的主标题与副标题、`targetId` 为该路线 id，且主副标题不取自路线实体

#### Scenario: 创建文章类周期推荐
- **GIVEN** 存在一篇尚未被引用的文章
- **WHEN** 提交 type=ARTICLE、`phases=["LUTEAL"]`、`targetId` 为该文章 id、主标题与 banner 图
- **THEN** 返回 200，详情含主标题、`targetId` 与关联实体标题、banner 签名 URL

#### Scenario: 同一关联实体重复创建被拒绝
- **GIVEN** 某个活动已存在一条周期推荐条目
- **WHEN** 再次提交 type=ACTIVITY 且 `targetId` 为同一活动的条目
- **THEN** 返回 400 及中文业务错误，不新增条目

#### Scenario: 下线条目同样占用唯一位
- **GIVEN** 某篇文章已存在一条**下线**的周期推荐条目
- **WHEN** 提交 type=ARTICLE 且 `targetId` 为同一文章的新条目
- **THEN** 返回 400 及中文业务错误

#### Scenario: 更新条目自身不触发唯一冲突
- **GIVEN** 一个 type=ACTIVITY 的条目
- **WHEN** 更新该条目、`targetId` 仍为原活动，仅改动文案与 `phases`
- **THEN** 返回 200，更新成功

#### Scenario: 更新关联实体
- **GIVEN** 一个 type=ACTIVITY 的条目关联活动 A，活动 B 尚未被任何周期推荐引用
- **WHEN** 把该条目的 `targetId` 改为活动 B
- **THEN** 返回 200，详情 `targetId` 为活动 B 且关联实体标题随之变为 B 的标题；此后再新建一条 `targetId` 为活动 A 的条目 SHALL 成功（A 的唯一位已释放）

#### Scenario: 更新指向已被占用的实体被拒绝
- **GIVEN** 活动 A 与活动 B 各有一条周期推荐条目
- **WHEN** 把 A 的条目的 `targetId` 改为活动 B
- **THEN** 返回 400 及中文业务错误

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
- **GIVEN** 一个 type=ACTIVITY、`phases=["MENSTRUAL"]` 的条目
- **WHEN** 提交更新请求，`phases` 改为 `["FOLLICULAR","OVULATION"]`，同时传入不同的 `type` 值，其余字段仍按该条目**持久化类型**的必填约束提供
- **THEN** 返回 200，`phases` 更新为 `["FOLLICULAR","OVULATION"]`，`type` 保持 ACTIVITY 不变；必填校验与 `targetId` 的存在性校验均按持久化类型执行，与请求体传入的类型无关

#### Scenario: 按周期过滤列表
- **GIVEN** 条目 X 的 `phases` 为 `["FOLLICULAR","LUTEAL"]`，条目 Y 的 `phases` 为 `["MENSTRUAL"]`
- **WHEN** 以 `phase=FOLLICULAR` 查询分页列表
- **THEN** 返回 200，仅含条目 X，按 sortOrder 升序

#### Scenario: 不传周期返回全部条目
- **GIVEN** 各周期下共有若干条目
- **WHEN** 不带 `phase` 查询分页列表
- **THEN** 返回 200，含全部条目，每项带 `phases` 数组，按 sortOrder 升序

#### Scenario: 周期推荐上下线切换
- **GIVEN** 一个上线的周期推荐条目
- **WHEN** 将其下线
- **THEN** 返回 200，详情 online=false

### Requirement: App 端周期推荐查询
app 端 SHALL 提供只读的周期推荐接口，返回**扁平数组**，按 `sortOrder` 升序、同序号按创建时间倒序，由客户端按本地判定的周期自行选取展示。服务端 SHALL NOT 依据用户身份做筛选——app 后端不持有用户周期数据。

每个条目的 `period` 字段 SHALL 为周期枚举**数组**，其值直接取自该条目自身持久化的周期集合 `phases`，去重后按 `MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL` 的枚举声明顺序排列。该值 SHALL NOT 跨条目聚合计算，也 SHALL NOT 受本次请求的 `period` / `type` 查询参数影响。因同一关联实体全局至多一条条目，一个 target 在响应中至多出现一次。

接口 SHALL 支持两个可选查询参数，可同时使用：
- `period`（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）：传入时仅下发 `period` 数组**包含**该值的条目；不传时下发全部条目。
- `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该内容类型的条目；不传时下发全部类型。

传入非法的 `period` 或 `type` 值 SHALL 返回 400。过滤后无条目 SHALL 返回空数组。

条目仅在自身 `online=true` **且**其关联实体当前可见时下发：`ACTIVITY` 仅需活动 `online=true`（活动不关联城市）；`ROUTE` 仅需其爱女大使 `online=true`（与路线所属城市是否上架无关）；`ARTICLE` 需文章 `online=true`。关联实体已被删除的条目 SHALL 不下发。

每个条目下发内容：条目覆盖的周期集合、类型、banner 签名 URL、该类型的文案字段、关联实体 id `targetId`（单字段，指向哪张表由 `type` 判别，供 App 端自行决定跳转），以及关联实体的基础信息对象 `target`。

`target` SHALL 随条目一起下发，其字段形状按 `type` 判别，由 App 端自行解析；每种形状只含渲染推荐卡片所需的基础信息，不含详情内容：

| `type` | `target` 字段 |
|---|---|
| `ACTIVITY` | `id`、`title`、`subtitle`（活动副标题，活动未填写时为 null）、`cover`（活动首图签名 URL，活动无图时为 null）、`level` |
| `ROUTE` | `id`、`title`、`thumbnail`（签名 URL）、`cityName`、`ambassadorName` |
| `ARTICLE` | `id`、`title`、`coverTitle`、`image`（签名 URL） |

因条目仅在关联实体可见时才下发，被下发条目的 `target` SHALL NOT 为 null。`target` 的字段值 SHALL 取自关联实体本身，与条目上手填的文案字段（`title` / `subtitle` / `description` / `note`）相互独立、互不覆盖。

#### Scenario: 查询四个周期的推荐列表
- **GIVEN** 一条 `phases=["MENSTRUAL"]` 的上线条目、一条 `phases=["OVULATION"]` 的上线条目、一条 `phases=["LUTEAL"]` 的下线条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，响应为数组，含两条上线条目，`period` 分别为 `["MENSTRUAL"]` 与 `["OVULATION"]`，不含下线条目

#### Scenario: 同一 target 跨周期时下发全部周期
- **GIVEN** 一条上线条目的 `phases` 为 `["MENSTRUAL","LUTEAL"]`，其关联活动上线
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，数组含该条目一次，其 `period` 为 `["MENSTRUAL","LUTEAL"]`（按枚举声明顺序）

#### Scenario: 按周期过滤时 period 数组仍含其他周期
- **GIVEN** 一条上线条目的 `phases` 为 `["MENSTRUAL","LUTEAL"]`
- **WHEN** app 端带 `period=LUTEAL` 查周期推荐接口
- **THEN** 返回 200，数组含该条目，其 `period` 为 `["MENSTRUAL","LUTEAL"]`

#### Scenario: 类型过滤不影响 period 数组
- **GIVEN** 一条 type=ACTIVITY、`phases=["MENSTRUAL","LUTEAL"]` 的上线条目
- **WHEN** app 端带 `type=ACTIVITY&period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组含该条目，其 `period` 为 `["MENSTRUAL","LUTEAL"]`

#### Scenario: 不可下发条目不贡献周期
- **GIVEN** 一条 `phases=["MENSTRUAL","LUTEAL"]` 的**下线**条目
- **WHEN** app 端不带参数查周期推荐接口
- **THEN** 返回 200，响应中不含该条目

#### Scenario: 不同 target 的周期集合互不影响
- **GIVEN** 条目 A（关联活动 A）`phases=["MENSTRUAL","LUTEAL"]`，条目 B（关联活动 B）`phases=["MENSTRUAL"]`，均上线且活动可见
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组含两条条目，A 的 `period` 为 `["MENSTRUAL","LUTEAL"]`，B 的 `period` 为 `["MENSTRUAL"]`

#### Scenario: 按周期过滤
- **GIVEN** 两条 `phases=["MENSTRUAL"]` 的上线条目与一条 `phases=["FOLLICULAR"]` 的上线条目
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 返回 200，数组仅含前两条（`period` 均为 `["MENSTRUAL"]`），不含卵泡期条目

#### Scenario: 周期与类型同时过滤
- **GIVEN** `phases` 含经期的 ACTIVITY、ARTICLE 各一条上线条目，另有一条 `phases=["FOLLICULAR"]` 的上线 ARTICLE 条目
- **WHEN** app 端带 `period=MENSTRUAL&type=ARTICLE` 查周期推荐接口
- **THEN** 返回 200，数组仅含 `phases` 含经期的那条 ARTICLE 条目

#### Scenario: 按内容类型过滤
- **GIVEN** `phases` 相同的 ACTIVITY、ROUTE、ARTICLE 各一条上线条目
- **WHEN** app 端带 `type=ARTICLE` 查周期推荐接口
- **THEN** 返回 200，数组仅含该 ARTICLE 条目，不含 ACTIVITY 与 ROUTE 条目

#### Scenario: 类型过滤后周期为空仍返回空数组
- **GIVEN** 仅有一条 ACTIVITY 上线条目
- **WHEN** app 端带 `type=ROUTE` 查周期推荐接口
- **THEN** 返回 200，响应为空数组（不返回 404）

#### Scenario: 周期过滤后无条目返回空数组
- **GIVEN** 仅有一条 `phases=["MENSTRUAL"]` 的 ACTIVITY 上线条目
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
- **GIVEN** `phases` 均含经期、sortOrder 为 2、1、3 的三个上线条目
- **WHEN** app 端带 `period=MENSTRUAL` 查周期推荐接口
- **THEN** 数组内条目按 1、2、3 顺序返回

#### Scenario: 活动类条目下发活动基础信息
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动上线且有图片、标题、副标题与难度等级
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目的 `target` 含活动 id、活动标题、活动副标题、首图签名 URL 与难度等级，且条目自身的推荐说明不受影响

#### Scenario: 活动未填副标题时 target.subtitle 为 null
- **GIVEN** 一个上线的 ACTIVITY 类条目，其关联活动未填写副标题，条目自身手填了推荐说明
- **WHEN** app 端查周期推荐接口
- **THEN** 返回 200，该条目的 `target.subtitle` 为 null（不回落为活动标题），条目自身的 `description` 仍为手填值——ACTIVITY 类条目本身不持有 `subtitle` 文案（该文案字段只适用于 `ROUTE`），故两者不存在覆盖关系

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

### Requirement: web 端周期推荐页面
web 端 SHALL 提供「周期推荐」后台页面：**单一 DataTable 列表**（banner 图/内容类型/标题/关联实体/**投放周期**/排序号/状态/操作），SHALL NOT 提供周期 Tab 切换。列表顶部 SHALL 提供一个可选的周期筛选下拉（含「全部周期」项，默认全部），选中某周期时列表仅展示 `phases` 包含该周期的条目。「投放周期」列 SHALL 以标签形式展示该条目的全部周期，按 `MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL` 顺序排列。

新增/编辑表单为**独立路由页**（`/featured-cycle-items/create`、`/featured-cycle-items/:id/edit`，字段数超过 5 个，不做成弹窗）。表单 SHALL 提供**周期多选勾选框组**（四个周期各一个勾选框，至少勾选一个，未勾选时提交被阻止并提示），新增与编辑时均可勾选与改动；列表页的周期筛选 SHALL NOT 带进新增页做预填。表单 SHALL 先选内容类型，再按类型动态展示对应字段与对应实体的下拉选择器；`ARTICLE` 类型选中文章后主标题 SHALL 自动带出文章标题且可编辑。关联实体已存在推荐时提交 SHALL 展示后端返回的中文业务错误并停留在表单页。列表页 SHALL 提供上下线开关与删除确认弹窗。

页面线框：

```
┌─────────────────────────────────────────────────────────────────────┐
│ 周期推荐                                          [ + 新增周期推荐 ] │ ← ①
├─────────────────────────────────────────────────────────────────────┤
│ 周期筛选: [ 全部周期 ▾ ]                                            │ ← ②
├──────┬────────┬────────┬──────────┬──────────────┬────┬──────┬──────┤
│banner│ 类型   │ 标题   │ 关联实体 │ 投放周期     │排序│ 状态 │ 操作 │ ← ③
├──────┼────────┼────────┼──────────┼──────────────┼────┼──────┼──────┤
│ [img]│ 活动   │ 春日…  │ 春日徒步 │ [经期][黄体期]│ 0 │ [ON] │编辑删│
│ [img]│ 路线   │ 山野…  │ 山野路线 │ [卵泡期]      │ 1 │ [OFF]│编辑删│
└──────┴────────┴────────┴──────────┴──────────────┴────┴──────┴──────┘

新增/编辑表单页（独立路由，非弹窗）：
┌───────────────────────────────────────────┐
│ 新增周期推荐                              │
├─ 基础信息 ───────────────────────────────┤
│ 投放周期*  ☑经期 ☐卵泡期 ☐排卵期 ☑黄体期 │ ← ④
│            可多选，至少选一个；创建后仍可改│
│ 内容类型*  [ tripperclub活动 ▾ ]          │ ← ⑤
│ 排序号     [ 0 ]                          │
├─ 内容配置 ───────────────────────────────┤
│ 关联活动*  [ 请选择活动 ▾ ]               │
│ 推荐说明*  [____________________________] │
│ banner 图* [ 上传 ]                       │
├───────────────────────────────────────────┤
│                        [ 保存 ] [ 取消 ]  │
└───────────────────────────────────────────┘
```

#### Scenario: 周期 Tab 切换与列表展示
- **GIVEN** 已登录后台，存在一条 `phases` 为经期+黄体期的条目与一条 `phases` 为卵泡期的条目
- **WHEN** 进入周期推荐页
- **THEN** 区域③ 一张列表同时展示两条条目，页面上没有周期 Tab；第一条的「投放周期」列展示「经期」「黄体期」两个标签，第二条展示「卵泡期」一个标签

#### Scenario: 周期筛选下拉
- **GIVEN** 已登录后台，存在一条 `phases` 含卵泡期的条目与一条仅含经期的条目
- **WHEN** 在区域② 的周期筛选下拉中选择「卵泡期」
- **THEN** 区域③ 仅展示 `phases` 含卵泡期的那条条目；切回「全部周期」后两条都展示

#### Scenario: 新增周期推荐
- **GIVEN** 已登录后台并进入新增周期推荐页
- **WHEN** 在区域④ 同时勾选「经期」与「黄体期」，选类型、选关联实体、填文案、上传 banner 并提交
- **THEN** 保存成功并有成功提示，列表新条目的「投放周期」列展示「经期」「黄体期」两个标签

#### Scenario: 未勾选周期无法提交
- **GIVEN** 已登录后台并进入新增周期推荐页，其余必填项均已填写
- **WHEN** 区域④ 一个周期都不勾选并尝试提交
- **THEN** 提交被阻止并提示需至少选择一个周期，列表不新增条目

#### Scenario: 编辑时修改周期
- **GIVEN** 已登录后台，存在一条 `phases` 仅为经期的条目
- **WHEN** 进入该条目的编辑页，取消勾选「经期」、勾选「排卵期」与「黄体期」并保存
- **THEN** 保存成功，列表该条目的「投放周期」列变为「排卵期」「黄体期」

#### Scenario: 关联实体重复时展示错误
- **GIVEN** 已登录后台，某个活动已存在一条周期推荐条目
- **WHEN** 在新增页再次选择同一活动并提交
- **THEN** 展示后端返回的中文业务错误提示，停留在表单页，列表不新增条目

#### Scenario: 表单按类型切换字段
- **GIVEN** 已登录后台并进入新增周期推荐页
- **WHEN** 将区域⑤ 的内容类型从「tripperclub活动」切到「路线体验」
- **THEN** 表单字段切换为路线下拉、主标题、副标题、推荐说明、banner 上传，活动专属字段消失，区域④ 已勾选的周期保持不变

#### Scenario: 文章类型自动带出主标题
- **GIVEN** 已登录后台并在新增页选择内容类型「周期生活法」
- **WHEN** 在文章下拉中选中一篇文章
- **THEN** 主标题输入框自动填入该文章标题，且仍可手动改写

#### Scenario: 周期推荐上下线与删除
- **GIVEN** 已登录后台且列表存在条目
- **WHEN** 切换某条目状态开关，再对另一条目点删除并确认
- **THEN** 状态即时更新，被删条目从列表消失，两步均有成功提示

