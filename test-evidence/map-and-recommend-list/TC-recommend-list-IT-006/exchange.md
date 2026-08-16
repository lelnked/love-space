# TC-recommend-list-IT-006 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：POST /api/admin/cities 创建城市 D

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "chineseName": "测榜城153313",
  "englishName": "Bangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "editorNote": null,
  "online": true
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "chineseName": "测榜城153313",
  "englishName": "Bangcheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T15:33:16.932666205Z",
  "updatedAt": "2026-08-16T15:33:16.932666205Z"
}
```

## Step 2: 前置：创建清单 sortOrder=5

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "清单五",
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 5
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ae2c-75b4-8b6b-fd272f9b5cc3",
  "title": "清单五",
  "introduction": null,
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 5,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:16.972275947Z",
  "updatedAt": "2026-08-16T15:33:16.972275947Z"
}
```

## Step 3: 前置：创建清单 sortOrder=1（标题含精选）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "湖畔精选清单",
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 1
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ae5d-7dee-9d98-753f586ef0d1",
  "title": "湖畔精选清单",
  "introduction": null,
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:17.021791648Z",
  "updatedAt": "2026-08-16T15:33:17.021791648Z"
}
```

## Step 4: 前置：创建清单 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "清单三",
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 3
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-ae96-7d1a-b4ad-0304dbabd417",
  "title": "清单三",
  "introduction": null,
  "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:17.077705069Z",
  "updatedAt": "2026-08-16T15:33:17.077705069Z"
}
```

## Step 5: GET page?cityId=&page=0&size=10

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a00b34-ae04-7c00-ae5f-1188c71b301a&page=0&size=10" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "content": [
    {
      "id": "01a00b34-ae5d-7dee-9d98-753f586ef0d1",
      "title": "湖畔精选清单",
      "introduction": null,
      "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
      "sortOrder": 1,
      "merchantCount": 0,
      "createdAt": "2026-08-16T15:33:17.021792Z",
      "updatedAt": "2026-08-16T15:33:17.021792Z"
    },
    {
      "id": "01a00b34-ae96-7d1a-b4ad-0304dbabd417",
      "title": "清单三",
      "introduction": null,
      "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
      "sortOrder": 3,
      "merchantCount": 0,
      "createdAt": "2026-08-16T15:33:17.077705Z",
      "updatedAt": "2026-08-16T15:33:17.077705Z"
    },
    {
      "id": "01a00b34-ae2c-75b4-8b6b-fd272f9b5cc3",
      "title": "清单五",
      "introduction": null,
      "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
      "sortOrder": 5,
      "merchantCount": 0,
      "createdAt": "2026-08-16T15:33:16.972276Z",
      "updatedAt": "2026-08-16T15:33:16.972276Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1
}
```

## Step 6: GET page?cityId=&keyword=精选

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/page?cityId=01a00b34-ae04-7c00-ae5f-1188c71b301a&keyword=%E7%B2%BE%E9%80%89" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "content": [
    {
      "id": "01a00b34-ae5d-7dee-9d98-753f586ef0d1",
      "title": "湖畔精选清单",
      "introduction": null,
      "cityId": "01a00b34-ae04-7c00-ae5f-1188c71b301a",
      "sortOrder": 1,
      "merchantCount": 0,
      "createdAt": "2026-08-16T15:33:17.021792Z",
      "updatedAt": "2026-08-16T15:33:17.021792Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```
