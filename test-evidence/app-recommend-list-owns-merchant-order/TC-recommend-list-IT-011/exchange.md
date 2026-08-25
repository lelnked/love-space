# TC-recommend-list-IT-011 请求/响应存证

用例: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序
执行日期: 2026-08-25 ｜ change: app-recommend-list-owns-merchant-order ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU）返回 JWT，下文以 $TOKEN 代指（`export TOKEN=<jwt>` 后 curl 原样可用）；app 侧请求头 X-API-Key: test-api-key
说明: admin 侧 `PUT /api/admin/recommend-lists/{id}/merchants` 已删除，清单内商户改由 POST/PUT body 的 `merchantIds`（有序 UUID 数组）整体替换；图片 objectKey 用 `bound/*.png`（test profile StubObjectKeyValidator）。

## Step 1: 前置：POST /api/admin/cities 创建上架城市 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"测城A140542","englishName":"CityA140542","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
  "chineseName": "测城A140542",
  "englishName": "CityA140542",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-25T14:05:42.99871652Z",
  "updatedAt": "2026-08-25T14:05:42.99871652Z"
}
```

## Step 2: 前置：POST /api/admin/recommend-lists 清单 sortOrder=5

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单五140600","introduction":"s5","cityId":"01a0393d-bee3-74d7-ba44-26e7d9e2c00e","sortOrder":5}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0339-7d14-9cab-4a4b994b1652",
  "title": "清单五140600",
  "introduction": "s5",
  "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
  "sortOrder": 5,
  "merchants": [],
  "createdAt": "2026-08-25T14:06:00.504530368Z",
  "updatedAt": "2026-08-25T14:06:00.504530368Z",
  "status": "ONLINE"
}
```

## Step 3: 前置：POST /api/admin/recommend-lists 清单 sortOrder=1

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单一140600","introduction":"s1","cityId":"01a0393d-bee3-74d7-ba44-26e7d9e2c00e","sortOrder":1}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0358-76c9-ad65-d78373189998",
  "title": "清单一140600",
  "introduction": "s1",
  "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-25T14:06:00.536375366Z",
  "updatedAt": "2026-08-25T14:06:00.536375366Z",
  "status": "ONLINE"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 清单 sortOrder=3

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"清单三140600","introduction":"s3","cityId":"01a0393d-bee3-74d7-ba44-26e7d9e2c00e","sortOrder":3}'
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a0393e-0368-72a3-be5b-952a1ea804e7",
  "title": "清单三140600",
  "introduction": "s3",
  "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
  "sortOrder": 3,
  "merchants": [],
  "createdAt": "2026-08-25T14:06:00.552121491Z",
  "updatedAt": "2026-08-25T14:06:00.552121491Z",
  "status": "ONLINE"
}
```

## Step 5: GET /api/app/recommend-lists?cityId=A（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a0393d-bee3-74d7-ba44-26e7d9e2c00e" -H "X-API-Key: test-api-key"
```

实际响应（HTTP/1.1 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a0393e-0358-76c9-ad65-d78373189998",
    "title": "清单一140600",
    "introduction": "s1",
    "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
    "sortOrder": 1
  },
  {
    "id": "01a0393e-0368-72a3-be5b-952a1ea804e7",
    "title": "清单三140600",
    "introduction": "s3",
    "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
    "sortOrder": 3
  },
  {
    "id": "01a0393e-0339-7d14-9cab-4a4b994b1652",
    "title": "清单五140600",
    "introduction": "s5",
    "cityId": "01a0393d-bee3-74d7-ba44-26e7d9e2c00e",
    "sortOrder": 5
  }
]
```
