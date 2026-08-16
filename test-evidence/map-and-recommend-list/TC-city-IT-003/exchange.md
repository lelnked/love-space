# TC-city-IT-003 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 前置：城市 id=01a00b34-a31b-726c-8c15-95d9ccbae26b，当前 editorNote 为 200 字（TC-city-IT-002 写入）

## Step 1: PUT /api/admin/cities/{id} editorNote=201 字

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a00b34-a31b-726c-8c15-95d9ccbae26b" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "chineseName": "测江城153313",
  "englishName": "Jiangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "editorNote": "编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编",
  "online": true
}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400 
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "编辑说长度不能超过 200 个字符",
  "path": "/api/admin/cities/01a00b34-a31b-726c-8c15-95d9ccbae26b"
}
```

## Step 2: GET /api/admin/cities/{id} 确认未变更

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
  "editorNote": "编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编",
  "online": true,
  "createdAt": "2026-08-16T15:33:14.113936Z",
  "updatedAt": "2026-08-16T15:33:14.333398Z"
}
```
