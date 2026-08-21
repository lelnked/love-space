# TC-featured-IT-010 POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝 — 请求/响应存证

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

## Step 2: POST type=ROUTE 缺 subtitle

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "OVULATION", "type": "ROUTE", "routeId": "01a01f69-a22a-7cd1-bdac-6f417a03fd6d", "title": "T", "description": "D", "banner": "images/fd8ff320-d68c-4831-a826-474a8ab18ad9.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "副标题不能为空",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 3: POST type=ACTIVITY 缺 description

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "type": "ACTIVITY", "activityId": "01a01f69-a234-740b-9621-901ba0af890b", "banner": "images/253492cd-0287-4558-9bb1-5d2b2c8fb43b.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "推荐说明不能为空",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 4: POST type=ARTICLE 缺 banner

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "LUTEAL", "type": "ARTICLE", "articleId": "01a01f69-a23b-7034-a411-88475f9fa612", "title": "T"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "banner 图片不能为空",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 5: POST 缺 phase

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"type": "ACTIVITY", "activityId": "01a01f69-a234-740b-9621-901ba0af890b", "description": "D", "banner": "images/1c6aae73-4a82-495a-be00-8a4b0882e4b2.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "所属周期不能为空",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 6: POST 缺 type

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-cycle-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"phase": "MENSTRUAL", "activityId": "01a01f69-a234-740b-9621-901ba0af890b", "description": "D", "banner": "images/186c0e08-001f-4bdc-8696-7217cb74ac5e.png"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "内容类型不能为空",
  "path": "/api/admin/featured-cycle-items"
}
```

## Step 7: 复查分页计数不变

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

