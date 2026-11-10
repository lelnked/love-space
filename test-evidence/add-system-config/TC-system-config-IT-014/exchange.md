# TC-system-config-IT-014 请求/响应存证

> PUT /{id} 编辑成功且忽略请求体 key
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_voux3d", "name": "首页公告", "value": {"text": "旧"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d84e-73a9-8916-b2eb677303a4",
 "key": "home.notice_voux3d",
 "name": "首页公告",
 "value": {
  "text": "旧"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:30.702162856Z",
 "updatedAt": "2026-11-10T05:05:30.702162856Z"
}
```

## step 2 PUT 编辑（带 other.key）

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d84e-73a9-8916-b2eb677303a4 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "other.key_voux3d", "name": "首页公告-改", "value": {"text": "新"}, "description": "顶部"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d84e-73a9-8916-b2eb677303a4",
 "key": "home.notice_voux3d",
 "name": "首页公告-改",
 "value": {
  "text": "新"
 },
 "description": "顶部",
 "createdAt": "2026-11-10T05:05:30.702163Z",
 "updatedAt": "2026-11-10T05:05:30.766543963Z"
}
```

## step 3 GET 详情

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d84e-73a9-8916-b2eb677303a4 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d84e-73a9-8916-b2eb677303a4",
 "key": "home.notice_voux3d",
 "name": "首页公告-改",
 "value": {
  "text": "新"
 },
 "description": "顶部",
 "createdAt": "2026-11-10T05:05:30.702163Z",
 "updatedAt": "2026-11-10T05:05:30.766544Z"
}
```

## step 4 GET /page keyword=other.key_voux3d

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=other.key_voux3d&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 5 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d84e-73a9-8916-b2eb677303a4 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
