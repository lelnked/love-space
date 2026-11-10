# TC-system-config-IT-019 请求/响应存证

> GET /api/app/system-configs/{key} 单个查询
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 admin 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_f59eup", "name": "首页公告", "value": {"text": "hi"}, "description": "顶部"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d9a6-7da6-932b-9a0dc85220dd",
 "key": "home.notice_f59eup",
 "name": "首页公告",
 "value": {
  "text": "hi"
 },
 "description": "顶部",
 "createdAt": "2026-11-10T05:05:31.04680127Z",
 "updatedAt": "2026-11-10T05:05:31.04680127Z"
}
```

## step 2 App GET 单个

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice_f59eup \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "home.notice_f59eup",
 "value": {
  "text": "hi"
 }
}
```

## step 3 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d9a6-7da6-932b-9a0dc85220dd \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
