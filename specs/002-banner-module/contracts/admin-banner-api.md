# Admin Banner API Contract

Base path: `/api/admin/banners`
Auth: 落入 `SecurityConfig` 的 `/api/admin/**` `.authenticated()` 规则；ADMIN 与 MEMBER 两种 Manager 角色均可访问；MUST NOT 在 controller 上加 `@PreAuthorize("hasRole('ADMIN')")`。Manager 自身管理类接口才是 ADMIN-only。
所有响应包装与项目其它 admin 接口一致（`ApiResponse<T>` / `PageResult<T>`）。

## 数据契约

### BannerCreateRequest

```json
{
  "name": "上海首页 Banner",
  "type": "CITY",
  "imageUrls": ["https://cdn.example.com/a.png", "https://cdn.example.com/b.png"],
  "link": "9f1c2a7e-..."     // 当 type=CITY 时为 loves_city.id
}
```

- 服务端 JSON 字段名 `link`，Java 字段 `linkedEntityId`。
- `name` 非空 ≤ 128；`imageUrls` ≥ 1；`type` ∈ {CITY}；`link` 非空 UUID。

### BannerUpdateRequest

与 Create 同；MUST NOT 含 `online` 字段；任何 `online` 字段会被 400 拒绝。

### BannerOnlineRequest

```json
{ "online": true }
```

### BannerDetailResponse / BannerListItemResponse

```json
{
  "id": "...",
  "name": "...",
  "online": false,
  "type": "CITY",
  "imageUrls": [...],
  "link": "<city-id>",
  "linkedCityName": "上海",   // 仅 CITY 类型在列表/详情中冗余返回，前端列表展示用
  "createdAt": "...",
  "updatedAt": "..."
}
```

## 端点

### POST `/api/admin/banners`

- 入参：`BannerCreateRequest`
- 返回：201 + `BannerDetailResponse`（`online=false`）
- 错误：
  - 400 `BANNER_NAME_REQUIRED` / `BANNER_IMAGE_REQUIRED` / `BANNER_LINK_REQUIRED`
  - 404 `CITY_NOT_FOUND`（type=CITY 且 link 对应城市不存在）

### PUT `/api/admin/banners/{id}`

- 入参：`BannerUpdateRequest`
- 返回：200 + `BannerDetailResponse`
- 注意：服务端 MUST 忽略请求中可能误传的 `online` 字段（或直接 400 拒绝以防意外）；FR-009。

### POST `/api/admin/banners/{id}/online`

- 入参：`BannerOnlineRequest`
- 返回：200 + `BannerDetailResponse`
- 错误：
  - 400 `BANNER_LINKED_CITY_OFFLINE`（启用且关联城市离线）
  - 404 `BANNER_NOT_FOUND`

### GET `/api/admin/banners`

Query: `keyword`、`type`、`online`、`page`、`size`、`sort`（默认 `updatedAt,desc`）。
返回：`PageResult<BannerListItemResponse>`。
内部实现：`BannerSpecifications` + `BannerRepository.findAll(spec, pageable)`，
**全部字段引用 metamodel（宪法 VI）**。

### GET `/api/admin/banners/{id}`

返回：`BannerDetailResponse`。

### DELETE `/api/admin/banners/{id}`

返回：204。

### GET `/api/admin/cities?online=true&keyword=...`（既有接口，本特性确认其满足下拉框需求）

- 必要时新增 `keyword` 过滤参数。
- 前端 CitySelect 通过该接口拉取 online 城市并本地搜索；当城市量超阈值时可改为远程搜索，
  接口已具备能力。

## 操作日志

复用 `@OperationLog` 注解：`banner:create` / `banner:update` / `banner:online` / `banner:delete`。

## 中文 JavaDoc 要点（宪法 I）

- Controller 类与每个端点方法 MUST 在 JavaDoc 中说明：请求体语义、响应结构、HTTP 状态、
  鉴权要求、业务约束（如关联城市必须 online）。
- Request/Response record 的每个字段 JavaDoc 描述业务含义、约束。
