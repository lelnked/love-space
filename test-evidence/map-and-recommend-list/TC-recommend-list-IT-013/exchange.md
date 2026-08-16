# TC-recommend-list-IT-013 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 前置：城市 C=01a00b34-b86b-76e2-abdc-a2e2dbe5ee79 配有清单 LC=01a00b34-b8a1-757a-83c3-0153285513df 后已被下架（TC-city-IT-005 执行）

## Step 1: GET /api/app/recommend-lists?cityId=（下架城市）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b34-b86b-76e2-abdc-a2e2dbe5ee79" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

[]
```

## Step 2: GET /api/app/recommend-lists/{listId}（下架城市清单详情）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a00b34-b8a1-757a-83c3-0153285513df" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404 
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "recommend list not found: 01a00b34-b8a1-757a-83c3-0153285513df",
  "path": "/api/app/recommend-lists/01a00b34-b8a1-757a-83c3-0153285513df"
}
```
