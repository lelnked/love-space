# TC-featured-IT-027 GET /api/app/featured-cycle-items?period= 非法周期值返回 400 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:21423（test profile）｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
objectKey 为 test-profile 直接接受的占位 key（该实例不校验 OSS 对象存在），fixture 名带本轮后缀 `3u50` 防撞名。

## Step 1: GET ?period=UNKNOWN

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=UNKNOWN" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"Invalid value for parameter 'period': UNKNOWN","path":"/api/app/featured-cycle-items"}
```

## Step 2: GET ?period=menstrual（小写）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?period=menstrual" -H "X-API-Key: $APP_API_KEY"
```

实际响应: HTTP 400（Content-Type: application/json）

```json
{"status":400,"error":"Bad Request","message":"Invalid value for parameter 'period': menstrual","path":"/api/app/featured-cycle-items"}
```
