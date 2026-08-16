# TC-city-IT-004 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：POST /api/admin/cities 创建上架城市（editorNote=山与湖之间的浪漫）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "chineseName": "测湖城153313",
  "englishName": "Hucheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "editorNote": "山与湖之间的浪漫",
  "online": true
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-a4cb-7575-bafe-8b3fc7e32473",
  "chineseName": "测湖城153313",
  "englishName": "Hucheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": "山与湖之间的浪漫",
  "online": true,
  "createdAt": "2026-08-16T15:33:14.571270688Z",
  "updatedAt": "2026-08-16T15:33:14.571270688Z"
}
```

## Step 2: GET /api/app/cities（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/cities" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "01a00b34-a4cb-7575-bafe-8b3fc7e32473",
    "chineseName": "测湖城153313",
    "englishName": "Hucheng153313",
    "chineseProvince": "测试省",
    "englishProvince": "Test Province",
    "backgroundImage": null,
    "editorNote": "山与湖之间的浪漫"
  },
  {
    "id": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
    "chineseName": "测江城153313",
    "englishName": "Jiangcheng153313",
    "chineseProvince": "测试省",
    "englishProvince": "Test Province",
    "backgroundImage": null,
    "editorNote": "编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编编"
  }
]
```
