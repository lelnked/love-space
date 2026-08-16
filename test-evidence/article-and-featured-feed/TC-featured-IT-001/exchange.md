# TC-featured-IT-001 POST /api/admin/featured-items 创建精选推荐 — 请求/响应存证

执行日期: 2026-08-16（失败修复后复测）｜ admin=http://localhost:21423
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）

## Step 1: POST /api/admin/auth/login 获取 JWT token

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H 'Content-Type: application/json' -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
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

## Step 2: 前置：POST /api/admin/cities 创建上架城市

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/cities" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"chineseName":"精选城R172742","englishName":"FeatCityR172742","chineseProvince":"测试省","englishProvince":"Test Province","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-71e4-7135-aa5d-4a7b6c907065",
  "chineseName": "精选城R172742",
  "englishName": "FeatCityR172742",
  "chineseProvince": "测试省",
  "englishProvince": "Test Province",
  "backgroundImage": null,
  "editorNote": null,
  "online": true,
  "createdAt": "2026-08-16T17:27:42.818424838Z",
  "updatedAt": "2026-08-16T17:27:42.818424838Z"
}
```

## Step 3: POST /api/admin/featured-items

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b9d-71e4-7135-aa5d-4a7b6c907065","banner":"images/feat001r-172742.png","description":"地图上新","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-7209-7e5d-be6b-33f64275ff19",
  "cityId": "01a00b9d-71e4-7135-aa5d-4a7b6c907065",
  "banner": {
    "id": "bound/feat001r-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001r-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Gzc5BxthCtnWtGvi%2FCSxvtJk2jo%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-16T17:27:42.856083018Z",
  "updatedAt": "2026-08-16T17:27:42.856083018Z"
}
```

## Step 4: GET /api/admin/featured-items/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/01a00b9d-7209-7e5d-be6b-33f64275ff19" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b9d-7209-7e5d-be6b-33f64275ff19",
  "cityId": "01a00b9d-71e4-7135-aa5d-4a7b6c907065",
  "banner": {
    "id": "bound/feat001r-172742.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001r-172742.png?Expires=1786903062&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Gzc5BxthCtnWtGvi%2FCSxvtJk2jo%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-16T17:27:42.856083Z",
  "updatedAt": "2026-08-16T17:27:42.856083Z"
}
```
