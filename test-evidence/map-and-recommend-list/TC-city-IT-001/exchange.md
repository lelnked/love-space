# TC-city-IT-001 请求/响应存证

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
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3ZlLXNwYWNlLWFkbWluIiwic3ViIjoiMDE5Nzk0YjYtYjQwMC03MDAwLTgwMDAtMDAwMDAwMDAwMDAxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4Njg5NDM5MywiZXhwIjoxNzg2OTM3NTkzfQ.1hdzZ0K9c-e3baYsQ0vkjjKXNKl4nHRXtXKM8tKjeoU",
  "manager": {
    "id": "019794b6-b400-7000-8000-000000000001",
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

## Step 2: POST /api/admin/cities 创建城市（含 editorNote）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "chineseName": "测江城153313",
  "englishName": "Jiangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "editorNote": "江城夜景是这座城市的灵魂",
  "online": true
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "chineseName": "测江城153313",
  "englishName": "Jiangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": "江城夜景是这座城市的灵魂",
  "online": true,
  "createdAt": "2026-08-16T15:33:14.113935998Z",
  "updatedAt": "2026-08-16T15:33:14.113935998Z"
}
```

## Step 3: GET /api/admin/cities/{id} 查询详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/cities/01a00b34-a31b-726c-8c15-95d9ccbae26b" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "chineseName": "测江城153313",
  "englishName": "Jiangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": "江城夜景是这座城市的灵魂",
  "online": true,
  "createdAt": "2026-08-16T15:33:14.113936Z",
  "updatedAt": "2026-08-16T15:33:14.113936Z"
}
```
