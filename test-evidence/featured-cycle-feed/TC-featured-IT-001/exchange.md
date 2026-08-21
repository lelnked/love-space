# TC-featured-IT-001 POST /api/admin/featured-items 创建精选推荐 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: POST /api/admin/auth/login 获取 JWT token

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' -d '{"username": "admin", "password": "8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "token": "$TOKEN",
  "manager": {
    "id": "019794b6-b400-7000-8000-000000000001",
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

## Step 2: 前置：创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName": "精选城R001", "englishName": "FeatCityR001", "chineseProvince": "测试省", "englishProvince": "TP", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1",
  "chineseName": "精选城R001",
  "englishName": "FeatCityR001",
  "chineseProvince": "测试省",
  "englishProvince": "TP",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-20T13:47:15.8878378Z",
  "updatedAt": "2026-08-20T13:47:15.8878378Z"
}
```

## Step 3: 创建精选推荐

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1", "banner": "images/c2c2405d-d892-4238-b0bd-31535d2b83e8.png", "description": "地图上新", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e3c-75a3-9cd2-eb2beb0cb050",
  "cityId": "01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1",
  "banner": {
    "id": "bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pEoac5QwI8c3i4R762uP0JoHx88%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.898762364Z",
  "updatedAt": "2026-08-20T13:47:15.898762364Z"
}
```

## Step 4: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/01a01f6d-0e3c-75a3-9cd2-eb2beb0cb050" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e3c-75a3-9cd2-eb2beb0cb050",
  "cityId": "01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1",
  "banner": {
    "id": "bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pEoac5QwI8c3i4R762uP0JoHx88%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.898762Z",
  "updatedAt": "2026-08-20T13:47:15.898762Z"
}
```

