# App Banner API Contract

Base path: `/api/app/banners`
Auth: `X-API-Key`（项目记忆 `project_app_auth_api_key`；缺失或不匹配返回 401）。

## 移除

- `GET /api/app/explore` 及 `com.space.app.modules.explore` 整模块在本特性中删除。
- `BannerItem`、`ExploreResponse` 等 explore 专属 DTO 一并删除。

## 端点

### GET `/api/app/banners`

Query: `type`（可选；不传则全部 type）、`cityId`（可选；当指定时仅返回 `linkedEntityId=cityId`
的 CITY banner）。

返回：

```json
{
  "items": [
    {
      "id": "uuid",
      "name": "上海首页 Banner",
      "type": "CITY",
      "image": ["https://cdn.example.com/a.png", "https://cdn.example.com/b.png"],
      "data": { "id": "<cityId>", "name": "上海" }
    }
  ]
}
```

### 过滤规则（FR-013 / FR-015）

- 仅返回 `banner.online = true`。
- 仅返回关联实体仍合法且 online 的：CITY 类型要求 `loves_city` 中 id 存在且 `online=true`。
- 排序：`updatedAt desc`（首期；未来可加 `displayOrder`）。

### 错误

- 401 `INVALID_API_KEY`
- 200 + 空 `items[]` 表示无可见 banner，不返回 404。

## 实现要点

- `BannerQueryService`：使用 `BannerRepository.findAll(Specification, Sort)` 全字段走
  metamodel；CITY 数据装配通过 `CityRepository.findAllByIdInAndOnlineTrue(linkedIds)`
  一次性查回需要的 city 行，避免 N+1。
- `data` 字段类型 `Map<String, Object>`，便于未来 type 扩展（FR-014）。

## 中文 JavaDoc 要点

- Controller 与 `BannerQueryService` 公共方法 MUST 配中文 JavaDoc，描述请求参数、响应结构、
  鉴权方式、过滤规则。
- DTO 字段（`id`/`name`/`type`/`image`/`data`）MUST 配中文字段含义说明。
