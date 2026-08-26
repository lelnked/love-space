## MODIFIED Requirements

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
