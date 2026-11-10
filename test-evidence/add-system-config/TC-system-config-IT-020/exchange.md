# TC-system-config-IT-020 请求/响应存证

> GET /api/app/system-configs/{key} 不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 App GET 不存在 key

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/no.such_p2d6jh \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/app/system-configs/no.such_p2d6jh"
}
```
