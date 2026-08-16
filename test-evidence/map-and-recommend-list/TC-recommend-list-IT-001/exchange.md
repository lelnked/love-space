# TC-recommend-list-IT-001 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

## Step 1: POST /api/admin/auth/login 获取 JWT

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3ZlLXNwYWNlLWFkbWluIiwic3ViIjoiMDE5Nzk0YjYtYjQwMC03MDAwLTgwMDAtMDAwMDAwMDAwMDAxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4Njg5NDM5NiwiZXhwIjoxNzg2OTM3NTk2fQ.-rbzQfaGAt0aHEFT9Zl51xMmU5lnOYrTeRTJoXiF9c8",
  "manager": {
    "id": "019794b6-b400-7000-8000-000000000001",
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

## Step 2: POST /api/admin/recommend-lists 创建清单

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "江畔约会精选",
  "introduction": "沿江十家小店",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 3
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ac2e-7458-af62-c82ebd00e403",
  "title": "江畔约会精选",
  "introduction": "沿江十家小店",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.459287642Z",
  "updatedAt": "2026-08-16T15:33:16.459287642Z"
}
```

## Step 3: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/01a00b34-ac2e-7458-af62-c82ebd00e403" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ac2e-7458-af62-c82ebd00e403",
  "title": "江畔约会精选",
  "introduction": "沿江十家小店",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.459288Z",
  "updatedAt": "2026-08-16T15:33:16.459288Z"
}
```
