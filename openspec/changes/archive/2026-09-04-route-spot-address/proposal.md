## Why

CMS 路线管理的地点子列表只有名称/图片/介绍，运营无处录入地点地址，App 端也拿不到，用户看到路线地点却不知道在哪。

## What Changes

- 路线地点（jsonb 内联元素 `RouteSpot`）新增可选文本字段 `address`（地址），可空、可改、可清空。
- admin：`RouteSpotRequest` 接受 `address`（非必填），`RouteSpotResponse` 返回该字段。
- web：路线表单地点子项在「介绍」上方新增「地址」单行输入框，可留空，编辑时回显。
- app：`GET /api/app/routes/{id}` 详情的地点列表项返回 `address`，未填时为 null。
- 非 BREAKING：新增可选字段，既有请求体不带 `address` 仍合法，既有响应字段一个不减；既有 jsonb 数据无该 key，反序列化为 null。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `route`: 路线管理、App 端路线查询、web 端大使与路线管理页面三条 requirement 的地点字段集合各增 `address`

## Impact

- 数据库：无迁移（地点存于 `loves_route.spots` jsonb，加 key 即可）
- admin：`RouteSpot`、`RouteSpotRequest`、`RouteSpotResponse`、`RouteService`
- app：`RouteSpot`、`RouteSpotItemResponse`、`RouteQueryService`
- web：`src/api/routes.ts`、`src/pages/Routes/Form.tsx`
- 契约：`contracts/api-spec.json`、`love-space-app/docs/openapi.json`
- 测试：`tests/route/{it,web}.md`
