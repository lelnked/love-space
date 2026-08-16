# TC-recommend-list-IT-004 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 前置：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b 下清单 L1=01a00b34-ac2e-7458-af62-c82ebd00e403（TC-recommend-list-IT-001 创建）；城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473

## Step 1: PUT /api/admin/recommend-lists/{id}（body 带城市 B 的 cityId）

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/recommend-lists/01a00b34-ac2e-7458-af62-c82ebd00e403" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "改名后的清单",
  "introduction": "新介绍",
  "cityId": "01a00b34-a4cb-7575-bafe-8b3fc7e32473",
  "sortOrder": 9
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ac2e-7458-af62-c82ebd00e403",
  "title": "改名后的清单",
  "introduction": "新介绍",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 9,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.459288Z",
  "updatedAt": "2026-08-16T15:33:16.839859954Z"
}
```

## Step 2: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/01a00b34-ac2e-7458-af62-c82ebd00e403" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ac2e-7458-af62-c82ebd00e403",
  "title": "改名后的清单",
  "introduction": "新介绍",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 9,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.459288Z",
  "updatedAt": "2026-08-16T15:33:16.841445Z"
}
```
