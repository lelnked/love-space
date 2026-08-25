# TC-city-IT-006 断言明细

用例：城市下架后 app 端活动仍可见（不再级联）
锚定需求：city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见
契约：api-spec.json#/paths/~1api~1app~1activities/get（及 /api/app/activities/{id}）
执行日期：2026-08-25　结果：**✅ 通过（9/9）**

| # | 步骤 | 断言 | 期望 | 实测 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 存在上架城市 | ≥1 | 40 城中 online=true 38 个 | ✅ |
| 2 | 1 | 存在上线活动 | ≥1 | /api/app/activities 返回 10 条，选定 `01a038bb-d304-7295-a941-ee48da8726b5` | ✅ |
| 3 | 2 | 下架前列表含该活动 | 含 | 含（10 条中命中 id） | ✅ |
| 4 | 3 | 全部城市下架成功 | 全部 200 | 38/38 返回 200，0 失败 | ✅ |
| 5 | 3 | 下架生效核验 | app 城市列表为空 | GET /api/app/cities → `[]`；admin 侧 online 计数 0 | ✅ |
| 6 | 4 | 列表状态码 | 200 | 200 | ✅ |
| 7 | 4 | **列表仍含该活动** | 含 | 含，且总数不变 10 → 10 | ✅ |
| 8 | 5 | **详情状态码** | 200 | 200 | ✅ |
| 9 | 5 | 详情 Content-Type / body.id | application/json，id 一致 | `Content-Type: application/json`，id=`01a038bb-d304-7295-a941-ee48da8726b5` | ✅ |

## 契约校验

api-spec.json 中 `/api/app/activities` 与 `/api/app/activities/{id}` 的 get 只有 `summary` + `x-requirement`，
未声明 responses / schema，故无 schema 级断言可做，仅做字段合理性核对：

- 列表项字段：`id, images, introduction, level, periods, tags, title`
- 详情字段：`id, images, title, tags, periods, level, introduction, editorNote, gatheringPlace, dismissalPlace, transportation, visa, landscape, itinerary, detailHtml`
- 两者均**无 city/cityId 字段**，与 activity-drop-city-link 后「活动与地图解耦」一致。

⚠️ 契约漂移（不判失败，供人工确认）：
1. `/api/app/activities` 与 `/api/app/activities/{id}` 在 api-spec.json 中缺 `responses` 与响应 schema，无法做契约级字段校验。
2. 本轮用到的 admin 侧 `GET /api/admin/cities` 与 `PUT /api/admin/cities/{id}/online`（实现见 CityController）未在 api-spec.json 的 paths 中声明（spec 只有 `/api/admin/cities` 的 post 与 `/api/admin/cities/{id}` 的 put/delete）。

## 结论

下架系统内全部城市后，app 端活动列表与详情均不受影响——活动可见性只取决于活动自身上线状态，
与城市上下架无关。用例预期（反转后的新方向）成立。
