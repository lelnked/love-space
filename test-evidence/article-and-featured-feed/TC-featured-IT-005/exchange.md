# TC-featured-IT-005 DELETE /api/admin/featured-items/{id} 物理删除 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置：复用条目（id=01a00b98-5274-719c-8619-76dc30c748df）；token 复用本轮统一登录

## Step 1: DELETE /api/admin/featured-items/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type）:

```json

```

## Step 2: GET /api/admin/featured-items/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "精选推荐不存在：01a00b98-5274-719c-8619-76dc30c748df",
  "path": "/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df"
}
```

## Step 3: GET /api/admin/featured-items/page?size=100

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/page?page=0&size=100" -H "Authorization: Bearer $TOKEN"
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
