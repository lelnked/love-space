# TC-city-IT-013 断言明细

| # | 断言层 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | POST /api/admin/auth/login | 200 | 200 | ✅ |
| 2 | body | login 响应 token 为三段式 JWT | a.b.c | `eyJhbGciOiJIUzI1NiJ9.<payload>.<sig>` 三段 | ✅ |
| 3 | 状态码 | POST /api/admin/cities（前置） | 200 | 200 | ✅ |
| 4 | body | 城市 online | true | true | ✅ |
| 5 | 状态码 | POST /api/admin/merchants（前置） | 200 | 200 | ✅ |
| 6 | body | 商户 online / cityId | true / 01a038bd-5ec2-75f6-afe5-f1f90aa794ae | true / 01a038bd-5ec2-75f6-afe5-f1f90aa794ae | ✅ |
| 7 | 状态码 | POST /api/admin/banners + /{id}/online | 200 / 200 | 200 / 200 | ✅ |
| 8 | body | Banner type / link / online | CITY / 01a038bd-5ec2-75f6-afe5-f1f90aa794ae / true | CITY / 01a038bd-5ec2-75f6-afe5-f1f90aa794ae / true | ✅ |
| 9 | 状态码 | **DELETE /api/admin/cities/{id}** | 200 | 200 | ✅ |
| 10 | 状态码 | GET /api/admin/cities/{id}（删除后） | 400 | 400 | ✅ |
| 11 | body | 上条 message 为中文业务错误 | 中文「不存在」口径 | `城市不存在：01a038bd-5ec2-75f6-afe5-f1f90aa794ae` | ✅ |
| 12 | 响应头 | 上条 Content-Type | application/json | application/json | ✅ |
| 13 | 状态码 | GET /api/admin/merchants/{id}（删除后） | 200（记录仍在） | 200 | ✅ |
| 14 | body | 商户 online | false | false | ✅ |
| 15 | body | 商户 cityId 未清空 | 01a038bd-5ec2-75f6-afe5-f1f90aa794ae | 01a038bd-5ec2-75f6-afe5-f1f90aa794ae | ✅ |
| 16 | 状态码 | GET /api/admin/banners/{id}（删除后） | 200（记录仍在） | 200 | ✅ |
| 17 | body | Banner online | false | false | ✅ |
| 18 | body | Banner link 保留 | 01a038bd-5ec2-75f6-afe5-f1f90aa794ae | 01a038bd-5ec2-75f6-afe5-f1f90aa794ae（linkedCityName 变 null，城市行已删，符合预期） | ✅ |
| 19 | 契约 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete 存在 | 存在 | 存在（仅 summary，无 responses schema，无法做 schema 级校验） | ⚠️ |

**结论**: ✅ 通过（19 条断言全部满足；第 19 条为契约描述漂移，见下）

## ⚠️ 契约漂移（不判失败）

`contracts/api-spec.json` 中 `/api/admin/cities/{id}` `delete` 的 summary 仍为
「删除城市/地图（该城市下仍有路线时返回 400 中文业务错误，拒绝删除）」，`x-requirement` 仍指向
已 REMOVED 的 `city/城市下存在路线时禁止删除`。实际行为（本轮实测）为无条件删除 + 级联下架。
契约滞后于本 change，需人工确认后更新 summary / x-requirement 至 `city/地图删除`。
另：该 operation 未声明 responses schema，也未登记 `GET /api/admin/cities/{id}`、
`GET /api/admin/merchants/{id}` 两个本用例实际调用的读接口（实现存在，契约缺失）。
