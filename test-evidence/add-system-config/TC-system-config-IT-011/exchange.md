# TC-system-config-IT-011 请求/响应存证

> GET /{id} 详情不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 GET 不存在 id

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/00000000-0000-0000-0000-000000000000 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/admin/system-configs/00000000-0000-0000-0000-000000000000"
}
```
