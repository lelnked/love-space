## MODIFIED Requirements

### Requirement: App 端 Banner 查询
app 端 SHALL 提供 `GET /api/app/banners`（API-key 认证），必填查询参数 `positionCode`（**精确匹配**），可选 `linkedEntityId`（精确匹配）。返回**数组**（非分页），按 `sortOrder` 升序、同序号按创建时间**倒序**（新创建的靠前）。

仅 `online=true` 的 Banner 可见。条目字段为 `{id, name, type, image, data}`——图片字段名为 **`image`**（与 admin 端的 `imageUrls` 不同），且不下发 `positionCode` / `online` / `sortOrder` / `link` / 时间戳。`data` 按类型装配关联实体信息，CITY 类型含城市 id 与中英文名称、省份。

关联城市不存在或已下架时，该 Banner SHALL 被整条剔除（与 admin 上架校验、状态级联共同构成三重防线）。

缺少 `positionCode` 返回 400。API-key 缺失或错误 SHALL 返回 401，两种情况不作区分。

#### Scenario: 按展示位查询上架 Banner
- **GIVEN** 某展示位下有两个已上架 Banner，排序号分别为 1 与 0
- **WHEN** app 端以该 `positionCode` 查询
- **THEN** 返回 200 数组，按排序号 0、1 的顺序返回，每项含 `image` 数组与 `data`

#### Scenario: 同排序号 Banner 按创建时间倒序
- **GIVEN** 某展示位下两个已上架 Banner A、B 的排序号均为 0，B 的创建时间晚于 A
- **WHEN** app 端以该 `positionCode` 查询
- **THEN** 返回 200 数组，B 排在 A 之前

#### Scenario: 下架 Banner 不下发
- **GIVEN** 某展示位下有一个已下架 Banner
- **WHEN** app 端以该 `positionCode` 查询
- **THEN** 返回 200，结果中不含该 Banner

#### Scenario: 关联城市下架时条目被剔除
- **GIVEN** 一个 `online=true` 的 Banner，其关联城市为下架状态
- **WHEN** app 端查询该展示位
- **THEN** 返回 200，结果中不含该 Banner

#### Scenario: 缺少 API-key 返回 401
- **GIVEN** 请求未携带 API-key 请求头
- **WHEN** 请求 app 端 Banner 接口
- **THEN** 返回 401
