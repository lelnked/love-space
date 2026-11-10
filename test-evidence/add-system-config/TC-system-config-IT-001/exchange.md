# TC-system-config-IT-001 请求/响应存证

> POST /api/admin/system-configs 新增成功
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 POST 新增 home.notice_p54wrg

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_p54wrg", "name": "首页公告", "value": {"text": "hi"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d355-7105-9960-46c65ad56e2a",
 "key": "home.notice_p54wrg",
 "name": "首页公告",
 "value": {
  "text": "hi"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:29.429009641Z",
 "updatedAt": "2026-11-10T05:05:29.429009641Z"
}
```

## step 2 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d355-7105-9960-46c65ad56e2a \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
