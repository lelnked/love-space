# TC-system-config-IT-023 请求/响应存证

> App 系统配置接口 API-key 缺失或非法返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 无 X-API-Key

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice
```

HTTP 401（Content-Type: application/problem+json）

```json
{
 "detail": "Invalid or missing API key",
 "instance": "/api/app/system-configs/home.notice",
 "status": 401,
 "title": "Unauthorized"
}
```

## step 2 X-API-Key: wrong-key

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice \
  -H 'X-API-Key: wrong-key'
```

HTTP 401（Content-Type: application/problem+json）

```json
{
 "detail": "Invalid or missing API key",
 "instance": "/api/app/system-configs/home.notice",
 "status": 401,
 "title": "Unauthorized"
}
```

## step 3 批量 无 X-API-Key

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=home.notice'
```

HTTP 401（Content-Type: application/problem+json）

```json
{
 "detail": "Invalid or missing API key",
 "instance": "/api/app/system-configs",
 "status": 401,
 "title": "Unauthorized"
}
```
