# TC-system-config-IT-004 请求/响应存证

> 未登录访问后台系统配置接口返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 GET page 不带 Authorization

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?page=1&size=20'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/system-configs/page"
}
```

## step 2 GET page Bearer invalid

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?page=1&size=20' \
  -H "Authorization: Bearer invalid"
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/system-configs/page"
}
```
