## Why

App 端路线列表当前强制按城市查询（契约里仍是必填 `cityId`，代码已先行改成必填 `cityName`，二者不一致）。移动端需要「不限城市浏览全部路线」以及「查看某位爱女大使的全部路线」两种入口，当前接口都做不到。

## What Changes

- **BREAKING** `GET /api/app/routes` 移除 `cityId` 查询参数（契约层遗留，代码已不再使用）。
- `GET /api/app/routes` 新增可选查询参数 `cityName`（城市中文名）：不传时不按城市过滤，返回全部可见路线；传入且城市不存在时返回空数组（沿用现行语义）。
- `GET /api/app/routes` 新增可选查询参数 `ambassadorId`（UUID）：按关联爱女大使过滤；与 `cityName` 同时传入时取交集（AND）。
- 可见性规则不变：仅关联大使 `online=true` 的路线可见，按 `sortOrder` 升序。
- `contracts/api-spec.json` 同步为两个可选参数，删除 `cityId`。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `route`: App 端路线列表查询条件由「按城市必填查询」改为「城市名可选 + 大使 ID 可选的组合过滤」。

## Impact

- 代码：`love-space-app` route 模块（Controller / QueryService / Repository）。
- 契约：`contracts/api-spec.json` 的 `/api/app/routes` GET 参数。
- 测试：`tests/route/it.md` 中 TC-route-IT-012/013/015 的请求 URL（仍用 `cityId`）需更新，并新增无参与按大使过滤的用例。
- 前端/admin：无影响（admin 端路线管理接口不变，web 端不调用 app API）。
