# TC-city-IT-005 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建上架城市 C

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"chineseName":"清单城5-162755","englishName":"City16275529746","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bcaf-7cf0-b0a1-494b424d3b60",
  "chineseName": "清单城5-162755",
  "englishName": "City16275529746",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T16:27:57.487724461Z",
  "updatedAt": "2026-08-16T16:27:57.487724461Z"
}
```

## Step 2: 前置：城市 C 下创建推荐清单

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"title":"级联清单-162755","cityId":"01a00b66-bcaf-7cf0-b0a1-494b424d3b60","sortOrder":1}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bcd5-79b6-ae4f-a5b707331100",
  "title": "级联清单-162755",
  "introduction": null,
  "cityId": "01a00b66-bcaf-7cf0-b0a1-494b424d3b60",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-16T16:27:57.523833125Z",
  "updatedAt": "2026-08-16T16:27:57.523833125Z"
}
```

## Step 3: GET /api/app/recommend-lists 下架前可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b66-bcaf-7cf0-b0a1-494b424d3b60" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[
  {
    "id": "01a00b66-bcd5-79b6-ae4f-a5b707331100",
    "title": "级联清单-162755",
    "introduction": null,
    "cityId": "01a00b66-bcaf-7cf0-b0a1-494b424d3b60",
    "sortOrder": 1
  }
]
```

## Step 4: admin PUT /cities/{id}/online 下架城市

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a00b66-bcaf-7cf0-b0a1-494b424d3b60/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-bcaf-7cf0-b0a1-494b424d3b60",
  "chineseName": "清单城5-162755",
  "englishName": "City16275529746",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-16T16:27:57.487724Z",
  "updatedAt": "2026-08-16T16:27:57.487724Z"
}
```

## Step 5: GET /api/app/recommend-lists 下架后列表

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b66-bcaf-7cf0-b0a1-494b424d3b60" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

[]
```

## Step 6: GET /api/app/recommend-lists/{id} 下架后详情（应 404）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a00b66-bcd5-79b6-ae4f-a5b707331100" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "recommend list not found: 01a00b66-bcd5-79b6-ae4f-a5b707331100",
  "path": "/api/app/recommend-lists/01a00b66-bcd5-79b6-ae4f-a5b707331100"
}
```

