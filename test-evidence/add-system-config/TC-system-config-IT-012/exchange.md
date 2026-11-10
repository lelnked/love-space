# TC-system-config-IT-012 请求/响应存证

> 未登录访问后台系统配置接口返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 无 Authorization GET /page

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

## step 2 无 Authorization POST

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H 'Content-Type: application/json' \
  -d '{"key": "anon.k_8aqo74", "name": "x", "value": 1}'
```

HTTP 401（Content-Type: application/json）

```json
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/system-configs"
}
```

## step 3 Bearer invalid GET /page

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?page=1&size=20' \
  -H 'Authorization: Bearer invalid'
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

## step 4 admin 核对未创建

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=anon.k_8aqo74&page=1&size=20' \
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
