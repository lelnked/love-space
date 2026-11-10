# TC-system-config-IT-004 请求/响应存证

> POST key 重复返回 409
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_45e0ji", "name": "首页公告", "value": {"text": "hi"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d417-7586-887a-1e59f3beee90",
 "key": "home.notice_45e0ji",
 "name": "首页公告",
 "value": {
  "text": "hi"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:29.623295464Z",
 "updatedAt": "2026-11-10T05:05:29.623295464Z"
}
```

## step 2 再次 POST 重复 key

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_45e0ji", "name": "重复", "value": 1}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "配置 key 已存在",
 "path": "/api/admin/system-configs"
}
```

## step 3 GET /page keyword=home.notice_45e0ji

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=home.notice_45e0ji&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d417-7586-887a-1e59f3beee90",
   "key": "home.notice_45e0ji",
   "name": "首页公告",
   "value": {
    "text": "hi"
   },
   "description": null,
   "createdAt": "2026-11-10T05:05:29.623295Z",
   "updatedAt": "2026-11-10T05:05:29.623295Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 4 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d417-7586-887a-1e59f3beee90 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
