# TC-recommend-list-IT-013 请求/响应存证

用例: GET /api/app/recommend-lists 下架城市清单不可见、详情 404
执行日期: 2026-08-25 ｜ change: app-recommend-list-owns-merchant-order ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 X-API-Key: test-api-key
说明: admin 侧 `PUT /api/admin/recommend-lists/{id}/merchants` 已删除，清单内商户改由 POST/PUT body 的 `merchantIds`（有序 UUID 数组）整体替换；图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）。

## Step 1: 前置：POST /api/admin/cities 创建上架城市 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"测城B140542","englishName":"CityB140542","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393d-bfaf-76eb-872a-1ff3a3791e6c",
  "chineseName": "测城B140542",
  "englishName": "CityB140542",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:05:43.215366826Z",
  "updatedAt": "2026-08-25T14:05:43.215366826Z"
}
```

## Step 2: 前置：POST /api/admin/recommend-lists 在城市 B 下创建清单

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"下架城市清单140600","introduction":"x","cityId":"01a0393d-bfaf-76eb-872a-1ff3a3791e6c","sortOrder":1}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-04ac-7772-8e08-a5e1020e349b",
  "title": "下架城市清单140600",
  "introduction": "x",
  "cityId": "01a0393d-bfaf-76eb-872a-1ff3a3791e6c",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-25T14:06:00.876375621Z",
  "updatedAt": "2026-08-25T14:06:00.876375621Z",
  "status": "ONLINE"
}
```

## Step 3: 前置：PUT /api/admin/cities/{B}/online {"online":false} 下架城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a0393d-bfaf-76eb-872a-1ff3a3791e6c/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393d-bfaf-76eb-872a-1ff3a3791e6c",
  "chineseName": "测城B140542",
  "englishName": "CityB140542",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-25T14:05:43.215367Z",
  "updatedAt": "2026-08-25T14:05:43.215367Z"
}
```

## Step 4: GET /api/app/recommend-lists?cityId=B（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a0393d-bfaf-76eb-872a-1ff3a3791e6c" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 5: GET /api/app/recommend-lists/{listId}（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a0393e-04ac-7772-8e08-a5e1020e349b" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "recommend list not found: 01a0393e-04ac-7772-8e08-a5e1020e349b",
  "path": "/api/app/recommend-lists/01a0393e-04ac-7772-8e08-a5e1020e349b"
}
```
