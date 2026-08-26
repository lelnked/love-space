# recommend-list Specification

## Purpose
TBD - created by archiving change map-and-recommend-list. Update Purpose after archive.
## Requirements
### Requirement: 推荐清单管理
admin 端 SHALL 提供推荐清单 CRUD：清单含标题（必填）、介绍、所属城市（必填，创建后可改）、排序号 sortOrder、上架状态 `status`（`ONLINE` / `OFFLINE`，创建默认 `ONLINE`，可在创建或更新时设置）；删除为物理删除（同事务删除商户关联）。修改所属城市时，清单内已有商户 SHALL 全部属于新城市，否则返回 400 及中文业务错误、城市不变。`POST /api/admin/recommend-lists/{id}/online` SHALL 将 OFFLINE 清单人工恢复为 ONLINE；清单内存在已下架商户时 SHALL 拒绝（400 及中文业务错误）；对已是 ONLINE 的清单幂等返回详情。App 端清单列表仅返回 `status = ONLINE` 的清单。

#### Scenario: 创建清单
- **GIVEN** 已登录的 Manager 与一个已存在的城市
- **WHEN** 提交标题、介绍、所属城市、排序号创建清单（不传 status）
- **THEN** 创建成功，清单详情返回全部字段，`status` 为 `ONLINE`

#### Scenario: 缺少必填项被拒绝
- **GIVEN** 已登录的 Manager
- **WHEN** 创建清单时缺少标题或所属城市
- **THEN** 返回 400 及中文校验错误信息

#### Scenario: 修改所属城市需清单内商户同属新城市
- **GIVEN** 城市 A 下的清单含城市 A 的商户 M1；另有城市 B
- **WHEN** Manager 更新该清单把 cityId 改为 B（不改 merchantIds）
- **THEN** 返回 400 及中文业务错误，清单 cityId 仍为 A；若清单内无商户或商户已换成城市 B 的，则更新成功且 cityId 变为 B

#### Scenario: 人工恢复清单
- **GIVEN** 一个 `status = OFFLINE` 的清单
- **WHEN** Manager 调用 `POST /api/admin/recommend-lists/{id}/online`
- **THEN** 清单内无已下架商户时返回 200 且 `status` 变为 `ONLINE`；清单内存在已下架商户时返回 400 及中文业务错误，`status` 仍为 `OFFLINE`

#### Scenario: 删除清单
- **GIVEN** 一个已存在的清单（含商户关联）
- **WHEN** Manager 删除该清单
- **THEN** 清单与其商户关联关系被物理删除，商户本身不受影响

#### Scenario: 清单列表按排序号升序
- **GIVEN** 同一城市下多个清单，sortOrder 各异
- **WHEN** 查询该城市的清单列表（admin 或 app）
- **THEN** 结果按 sortOrder 从小到大排列

### Requirement: 清单内商户维护
清单内商户 SHALL 通过创建/更新请求的 `merchantIds`（有序 UUID 数组）整体替换：数组顺序即清单保存顺序，无独立排序号字段；更新时 `merchantIds` 为 null 视为不修改。保存时 SHALL 校验：每个商户存在、属于清单所属城市、未下架、数组内不重复；任一不满足返回 400 及中文业务错误且关联不变。不在数组中的既有关联 SHALL 被移除，商户本身不受影响。admin 详情 `merchants[]` 按清单保存顺序返回。

#### Scenario: 添加本城市商户
- **GIVEN** 一个清单与其所属城市下的商户 M1、M2
- **WHEN** Manager 更新清单，`merchantIds` 为 `[M2, M1]`
- **THEN** 保存成功，清单详情 `merchants` 顺序为 M2、M1

#### Scenario: 拒绝跨城市商户
- **GIVEN** 一个清单与一个属于其他城市的商户
- **WHEN** Manager 更新清单，`merchantIds` 含该商户
- **THEN** 返回 400 及中文业务错误信息，关联不建立

#### Scenario: 重复添加同一商户被拒绝
- **GIVEN** 一个清单与本城市商户 M1
- **WHEN** Manager 更新清单，`merchantIds` 为 `[M1, M1]`
- **THEN** 返回 400 及中文业务错误信息，关联不变

#### Scenario: 拒绝已下架商户
- **GIVEN** 一个清单与本城市一个已下架的商户
- **WHEN** Manager 更新清单，`merchantIds` 含该商户
- **THEN** 返回 400 及中文业务错误信息，关联不建立

#### Scenario: 从清单移除商户
- **GIVEN** 清单内已含 M1、M2
- **WHEN** Manager 更新清单，`merchantIds` 为 `[M2]`
- **THEN** 详情 `merchants` 仅含 M2；商户 M1 本身仍存在且字段不受影响

### Requirement: App 端清单与清单内商户查询
app 端 SHALL 提供按城市查询清单列表与清单详情的只读接口；仅上架城市的清单可见；清单列表 SHALL 按 `sortOrder` 升序、同序号按 `createdAt` 倒序返回；清单详情内商户 SHALL 按清单保存顺序（运营提交 `merchantIds` 的数组顺序）返回，同保存序号时按 `createdAt` 倒序，仅含上架商户，每项仅含 `id`、`name`、`address`、`logo` 四个字段，不回传排序号。
清单内商户只能通过清单详情接口获取；商户列表接口 `GET /api/app/merchants/page` SHALL NOT 接受清单相关参数，其排序固定为 `weight DESC, createdAt DESC`，不受推荐清单影响。

#### Scenario: 查询上架城市的清单
- **GIVEN** 某上架城市配有多个清单
- **WHEN** App 按该城市查询清单列表
- **THEN** 返回该城市全部清单，按 sortOrder 升序

#### Scenario: 同排序号清单按创建时间倒序
- **GIVEN** 某上架城市下两个 ONLINE 清单 A、B 的 `sortOrder` 均为 0，B 的创建时间晚于 A
- **WHEN** App 按该城市查询清单列表
- **THEN** 返回 200，B 排在 A 之前

#### Scenario: 清单详情返回商户明细
- **GIVEN** 某清单按顺序保存了商户 甲、乙（甲的 weight 低于乙），另有一个已下架商户 丙 也在清单中
- **WHEN** App 查询该清单详情
- **THEN** 返回清单字段与 `merchants` 数组，顺序为 甲→乙（清单保存顺序，与 weight 无关），丙不出现；每项仅含 `id`、`name`、`address`、`logo`，不含 `recommendReason`、`sortOrder`、`recommendSortOrder`

#### Scenario: 商户列表不受清单影响
- **GIVEN** 某城市下有 3 个上架商户，其中 2 个属于清单 L（清单内顺序与 weight 排序相反）
- **WHEN** App 请求商户列表分页（无论是否附带 `recommendListId` 参数）
- **THEN** 返回 200，3 个商户均返回，按 `weight DESC, createdAt DESC` 排列，响应项不含 `recommendSortOrder` 字段

#### Scenario: 下架城市清单不可见
- **GIVEN** 某城市已下架且配有清单
- **WHEN** App 按该城市查询清单列表或查询其清单详情
- **THEN** 列表不返回数据，详情返回 404

### Requirement: web 端推荐清单管理页面
web 后台 SHALL 提供推荐清单管理页面：按城市筛选的清单列表（含排序号、状态、商户数）、新建/编辑表单（所属城市可改、状态可选）、清单内商户维护（仅本城市未下架商户可选，顺序即添加顺序）、删除确认。

#### Scenario: 清单列表与筛选
- **GIVEN** Manager 已登录 web 后台
- **WHEN** 打开推荐清单页面并按城市筛选
- **THEN** 列表按既有 DataTable 口径展示该城市清单（标题、所属城市、排序号、商户数），按 sortOrder 升序

#### Scenario: 维护清单商户
- **GIVEN** Manager 在清单编辑界面
- **WHEN** 依次添加商户（下拉仅出现该清单所属城市的商户）并保存
- **THEN** 保存成功，清单商户列表按添加顺序回显

#### Scenario: 删除清单需确认
- **GIVEN** Manager 在清单列表点击删除
- **WHEN** 弹出确认弹窗并确认
- **THEN** 清单被删除并从列表消失；取消则不删除

