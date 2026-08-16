# TC-recommend-list-IT-002 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 前置：当前城市 01a00b34-a31b-726c-8c15-95d9ccbae26b 下清单总数 = 1

## Step 1: POST 缺 title

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b"
}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400 
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "清单标题不能为空",
  "path": "/api/admin/recommend-lists"
}
```

## Step 2: POST 缺 cityId

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "无城市清单"
}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400 
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "所属城市不能为空",
  "path": "/api/admin/recommend-lists"
}
```

## Step 3: GET page 确认清单数未变

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a00b34-a31b-726c-8c15-95d9ccbae26b&page=0&size=1" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "content": [
    {
      "id": "01a00b34-ac2e-7458-af62-c82ebd00e403",
      "title": "江畔约会精选",
      "introduction": "沿江十家小店",
      "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
      "sortOrder": 3,
      "merchantCount": 0,
      "createdAt": "2026-08-16T15:33:16.459288Z",
      "updatedAt": "2026-08-16T15:33:16.459288Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```
