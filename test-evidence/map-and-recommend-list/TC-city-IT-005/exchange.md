# TC-city-IT-005 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：POST /api/admin/cities 创建上架城市 C

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "chineseName": "测撤城153313",
  "englishName": "Checheng153313",
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
  "id": "01a00b34-b86b-76e2-abdc-a2e2dbe5ee79",
  "chineseName": "测撤城153313",
  "englishName": "Checheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T15:33:19.595358193Z",
  "updatedAt": "2026-08-16T15:33:19.595358193Z"
}
```

## Step 2: 前置：POST /api/admin/recommend-lists 城市 C 下创建清单 LC

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "下架级联清单",
  "cityId": "01a00b34-b86b-76e2-abdc-a2e2dbe5ee79",
  "sortOrder": 1
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-b8a1-757a-83c3-0153285513df",
  "title": "下架级联清单",
  "introduction": null,
  "cityId": "01a00b34-b86b-76e2-abdc-a2e2dbe5ee79",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-16T15:33:19.64925289Z",
  "updatedAt": "2026-08-16T15:33:19.64925289Z"
}
```

## Step 3: GET /api/app/recommend-lists?cityId= 下架前可见

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b34-b86b-76e2-abdc-a2e2dbe5ee79" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "01a00b34-b8a1-757a-83c3-0153285513df",
    "title": "下架级联清单",
    "introduction": null,
    "cityId": "01a00b34-b86b-76e2-abdc-a2e2dbe5ee79",
    "sortOrder": 1
  }
]
```

## Step 4: PUT /api/admin/cities/{id}/online 下架城市 C

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/cities/01a00b34-b86b-76e2-abdc-a2e2dbe5ee79/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "online": false
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b34-b86b-76e2-abdc-a2e2dbe5ee79",
  "chineseName": "测撤城153313",
  "englishName": "Checheng153313",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": false,
  "createdAt": "2026-08-16T15:33:19.595358Z",
  "updatedAt": "2026-08-16T15:33:19.595358Z"
}
```

## Step 5: GET /api/app/recommend-lists?cityId= 下架后

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists?cityId=01a00b34-b86b-76e2-abdc-a2e2dbe5ee79" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

[]
```

## Step 6: GET /api/app/recommend-lists/{LC} 详情

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a00b34-b8a1-757a-83c3-0153285513df" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 404）:

```
HTTP/1.1 404 
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "recommend list not found: 01a00b34-b8a1-757a-83c3-0153285513df",
  "path": "/api/app/recommend-lists/01a00b34-b8a1-757a-83c3-0153285513df"
}
```
