# TC-banner-IT-016 请求/响应存证

用例: GET /api/app/banners 缺少 API-key 返回 401
执行日期: 2026-08-26 ｜ change: app-list-sort-tiebreak ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 `X-API-Key: test-api-key`
图片 objectKey 用 test profile 的 Stub 校验器接受的固定 key。

> 无前置数据依赖，纯鉴权断言。


## Step 1: 不携带 X-API-Key 请求

```bash
curl -s -i "http://localhost:8081/api/app/banners?positionCode=APP_HOME_TOP"
```

实际响应（HTTP/1.1 401）:

```
HTTP/1.1 401
Content-Type: Content-Type: application/problem+json

{
  "detail": "Invalid or missing API key",
  "instance": "/api/app/banners",
  "status": 401,
  "title": "Unauthorized"
}
```

## Step 2: 携带错误的 X-API-Key 请求

```bash
curl -s -i -H "X-API-Key: wrong-key" "http://localhost:8081/api/app/banners?positionCode=APP_HOME_TOP"
```

实际响应（HTTP/1.1 401）:

```
HTTP/1.1 401
Content-Type: Content-Type: application/problem+json

{
  "detail": "Invalid or missing API key",
  "instance": "/api/app/banners",
  "status": 401,
  "title": "Unauthorized"
}
```
