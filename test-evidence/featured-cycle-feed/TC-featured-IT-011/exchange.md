# TC-featured-IT-011 POST /api/admin/featured-cycle-items 关联实体不存在被拒绝 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：记录当前条目总数

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

## Step 2: POST type=ACTIVITY activityId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "6986f09c-3a75-49ef-b31c-0108c07b4f3a", "description": "D", "banner": "images/7552a6d6-1a84-41f0-ac40-000b147501ad.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联活动不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 3: POST type=ROUTE routeId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "OVULATION", "type": "ROUTE", "routeId": "6986f09c-3a75-49ef-b31c-0108c07b4f3a", "title": "T", "subtitle": "S", "description": "D", "banner": "images/cdc0b997-5323-45f6-b944-5ae03b8940e3.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联路线不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 4: POST type=ARTICLE articleId 不存在

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "LUTEAL", "type": "ARTICLE", "articleId": "6986f09c-3a75-49ef-b31c-0108c07b4f3a", "title": "T", "banner": "images/dc27b083-d273-4303-8997-28bf6c289418.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联文章不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 5: 复查分页计数不变

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-cycle-items/page?size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

