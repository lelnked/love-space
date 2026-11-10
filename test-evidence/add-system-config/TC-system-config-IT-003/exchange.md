# TC-system-config-IT-003 请求/响应存证

> GET /{id} 详情不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 POST 登录取 token

```bash
curl -s -i -X POST http://localhost:21423/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "8@y2eoRLyStM*UVU"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "token": "$TOKEN",
 "manager": {
  "id": "019794b6-b400-7000-8000-000000000001",
  "username": "admin",
  "nickname": "管理员",
  "role": "ADMIN"
 }
}
```

## step 2 GET 不存在 id

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
