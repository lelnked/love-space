# banner Specification

## Purpose
Banner 管理：admin 端 CRUD 与上下架前置校验、名称唯一、图片 objectKey 绑定、城市状态变更的双向级联；app 端按展示位只读查询，关联城市不可见时整条剔除。
## Requirements
### Requirement: Banner 管理
admin 端 SHALL 提供 Banner CRUD，**ADMIN 与 MEMBER 角色均可访问**（未加角色限制）：

| 接口 | 说明 | 成功状态码 |
|---|---|---|
| `GET /api/admin/banners/page` | 分页列表 | 200 |
| `GET /api/admin/banners/{id}` | 详情 | 200 |
| `POST /api/admin/banners` | 创建 | **200**（非 201） |
| `PUT /api/admin/banners/{id}` | 更新 | 200 |
| `DELETE /api/admin/banners/{id}` | 删除 | **200**（空 body，非 204） |
| `POST /api/admin/banners/{id}/online` | 上下架 | 200 |

请求体字段与中文校验消息：`name` 必填且不超过 128 字符、`positionCode` 必填且不超过 64 字符、`type` 必填（当前仅 `CITY`）、`imageUrls` 至少一张且每项须匹配 OSS objectKey 格式、`link`（JSON 字段名为 `link`，值为关联城市 UUID）必填「请选择关联城市」、`sortOrder` 必填且非负。

创建 SHALL 强制 `online=false`，忽略请求中的上下架意图。更新为全字段覆盖，且请求体**携带 `online` 字段时直接拒绝**，返回 400「更新 banner 时不可修改上下架状态，请使用上下架操作」——上下架只能走专用接口。

Banner 名称 SHALL 全局唯一（更新时排除自身），重复返回 400「Banner 名称已存在：{name}」。关联城市不存在返回 400「关联城市不存在或已删除」。

删除为物理删除。资源不存在统一返回 **400**「banner 不存在：{id}」。

响应中 `imageUrls` SHALL 为 `{id, url}` 对象数组（`id` 为 objectKey、`url` 为签名访问地址），并附 `linkedCityName`（关联城市中文名，城市已删则为 null）。

#### Scenario: 创建后默认下架
- **GIVEN** 存在一个城市
- **WHEN** 提交完整的 Banner 创建请求
- **THEN** 返回 200，详情 `online` 为 false，`imageUrls` 每项含 id 与 url

#### Scenario: 名称重复被拒绝
- **GIVEN** 已存在名称为「首页顶部」的 Banner
- **WHEN** 以同一名称创建另一个 Banner
- **THEN** 返回 400 及消息「Banner 名称已存在：首页顶部」

#### Scenario: 更新时携带上下架字段被拒绝
- **GIVEN** 一个已存在的 Banner
- **WHEN** 提交带 `online` 字段的更新请求
- **THEN** 返回 400 及消息「更新 banner 时不可修改上下架状态，请使用上下架操作」

#### Scenario: 图片 objectKey 格式非法被拒绝
- **GIVEN** 以合法身份登录
- **WHEN** 提交 `imageUrls` 含 `other/abc.exe` 的创建请求
- **THEN** 返回 400 及 objectKey 格式的中文业务错误

### Requirement: Banner 上架前置校验
Banner 上架 SHALL 要求其关联城市存在且处于上架状态：城市不存在或已删除返回 400「关联城市不存在或已删除，无法上架」；城市存在但已下架返回 400「关联城市已下架，无法上架」。

下架 SHALL NOT 有任何前置条件。

#### Scenario: 关联城市下架时无法上架 Banner
- **GIVEN** 一个 Banner，其关联城市为下架状态
- **WHEN** 对该 Banner 执行上架
- **THEN** 返回 400 及消息「关联城市已下架，无法上架」

#### Scenario: 关联城市上架时可正常上架
- **GIVEN** 一个 Banner，其关联城市为上架状态
- **WHEN** 对该 Banner 执行上架
- **THEN** 返回 200，详情 `online` 为 true

#### Scenario: 下架无前置条件
- **GIVEN** 一个已上架的 Banner，其关联城市随后被下架
- **WHEN** 对该 Banner 执行下架
- **THEN** 返回 200，详情 `online` 为 false

### Requirement: 城市状态变更对 Banner 级联生效
城市上下架状态发生变更时，关联该城市的 CITY 类 Banner 的上下架状态 SHALL **跟随同步**——城市下架时关联 Banner 一并下架，城市**重新上架时关联 Banner 一并恢复上架**（包括此前被运营手动下架的 Banner）。

城市被删除时，关联该城市的 Banner SHALL 被置为下架，但 Banner 记录本身**不删除**。

上述级联 SHALL 在城市事务提交后异步执行；级联失败 SHALL NOT 影响已提交的城市操作，仅记录错误日志。

#### Scenario: 城市下架连带 Banner 下架
- **GIVEN** 一个上架城市与其关联的已上架 Banner
- **WHEN** 将该城市下架
- **THEN** 该 Banner 的 `online` 变为 false

#### Scenario: 城市重新上架连带 Banner 上架
- **GIVEN** 一个已下架城市与其关联的已下架 Banner
- **WHEN** 将该城市重新上架
- **THEN** 该 Banner 的 `online` 变为 true

#### Scenario: 删除城市只下架不删除 Banner
- **GIVEN** 一个城市与其关联的已上架 Banner
- **WHEN** 删除该城市
- **THEN** 该 Banner 仍存在，且 `online` 为 false

### Requirement: App 端 Banner 查询
app 端 SHALL 提供 `GET /api/app/banners`（API-key 认证），必填查询参数 `positionCode`（**精确匹配**），可选 `linkedEntityId`（精确匹配）。返回**数组**（非分页），按 `sortOrder` 升序、同序号按创建时间升序。

仅 `online=true` 的 Banner 可见。条目字段为 `{id, name, type, image, data}`——图片字段名为 **`image`**（与 admin 端的 `imageUrls` 不同），且不下发 `positionCode` / `online` / `sortOrder` / `link` / 时间戳。`data` 按类型装配关联实体信息，CITY 类型含城市 id 与中英文名称、省份。

关联城市不存在或已下架时，该 Banner SHALL 被整条剔除（与 admin 上架校验、状态级联共同构成三重防线）。

缺少 `positionCode` 返回 400。API-key 缺失或错误 SHALL 返回 401，两种情况不作区分。

#### Scenario: 按展示位查询上架 Banner
- **GIVEN** 某展示位下有两个已上架 Banner，排序号分别为 1 与 0
- **WHEN** app 端以该 `positionCode` 查询
- **THEN** 返回 200 数组，按排序号 0、1 的顺序返回，每项含 `image` 数组与 `data`

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

### Requirement: web 端 Banner 管理页面
web 端 SHALL 在 `/banners` 提供列表页：筛选栏含「名称」（模糊）、「位置标识」（模糊）、「类型」、「上下架」四项；列表列为名称、位置标识、类型、关联城市（空显示 `-`）、排序、上下架徽标、更新时间、操作。

操作列 SHALL 提供「编辑」（跳转表单页）、「上架」/「下架」（**乐观更新**：先本地切换状态，失败时回滚并提示）、「删除」（弹出确认框，文案含 Banner 名称，确认后删除）。

web 端 SHALL 在 `/banners/new` 与 `/banners/:id/edit` 提供表单页，字段为名称、位置标识、排序（非负整数）、类型、关联城市、图片（多图上传）。关联城市下拉 SHALL 仅列出**已上架**城市。保存成功后返回列表页。

#### Scenario: 列表展示与状态徽标
- **GIVEN** 已登录且存在若干 Banner
- **WHEN** 进入 Banner 列表页
- **THEN** 展示名称、位置标识、关联城市与上下架徽标，未关联城市的行显示 `-`

#### Scenario: 上下架乐观更新失败回滚
- **GIVEN** 一个关联城市已下架的 Banner
- **WHEN** 点击「上架」
- **THEN** 状态先本地翻转，收到失败响应后回滚为原状态并弹出错误提示

#### Scenario: 删除需确认
- **GIVEN** 列表存在一个 Banner
- **WHEN** 点击删除并在确认弹窗中确认
- **THEN** 该 Banner 从列表消失；点击取消则保留

#### Scenario: 表单城市下拉只列上架城市
- **GIVEN** 系统中同时存在上架城市与下架城市
- **WHEN** 打开 Banner 新增表单并展开关联城市下拉
- **THEN** 仅出现上架城市，下架城市不在选项中
