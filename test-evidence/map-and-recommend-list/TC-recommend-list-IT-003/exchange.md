# TC-recommend-list-IT-003 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

## Step 1: POST /api/admin/recommend-lists（不含 sortOrder）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "默认排序清单",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ad54-726d-9b8e-86edfdc60a11",
  "title": "默认排序清单",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.756079806Z",
  "updatedAt": "2026-08-16T15:33:16.756079806Z"
}
```

## Step 2: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/01a00b34-ad54-726d-9b8e-86edfdc60a11" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ad54-726d-9b8e-86edfdc60a11",
  "title": "默认排序清单",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.75608Z",
  "updatedAt": "2026-08-16T15:33:16.75608Z"
}
```
