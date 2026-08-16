# TC-recommend-list-IT-007 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 补跑说明：首轮因 test 实例 OSS 不可用标记未执行；现 admin test 实例已启用 test-profile StubObjectKeyValidator（正则校验后直接返回 bound key），本轮补跑。城市夹具复用首轮：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b（上架）、城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473（上架）。

## Step 1: POST /api/admin/auth/login 获取 JWT

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"8@y2eoRLyStM*UVU"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3ZlLXNwYWNlLWFkbWluIiwic3ViIjoiMDE5Nzk0YjYtYjQwMC03MDAwLTgwMDAtMDAwMDAwMDAwMDAxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4Njg5NDk2MCwiZXhwIjoxNzg2OTM4MTYwfQ._MyN_8-Ode7EuzTh9_B3Ojv6SFmAptkygws132BGN3I",
  "manager": {
    "id": "019794b6-b400-7000-8000-000000000001",
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

## Step 2: 前置：POST /api/admin/merchants 创建 M1（城市 A，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "name": "商户M一154239",
  "logo": "images/logo-test.png",
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "weight": 0,
  "online": true,
  "images": [
    "images/img-test-1.png"
  ],
  "recommendReason": "M1 江景推荐理由"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
  "name": "商户M一154239",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": "M1 江景推荐理由",
  "weight": 0,
  "online": true,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VhG4xjep69%2FnLiwRS%2FjtpTcxeK8%3D"
    }
  ],
  "createdAt": "2026-08-16T15:42:40.609762196Z",
  "updatedAt": "2026-08-16T15:42:40.609762196Z"
}
```

## Step 3: 前置：POST /api/admin/merchants 创建 M2（城市 A，上架）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "name": "商户M二154239",
  "logo": "images/logo-test.png",
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "weight": 0,
  "online": true,
  "images": [
    "images/img-test-1.png"
  ],
  "recommendReason": "M2 湖景推荐理由"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
  "name": "商户M二154239",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.30,
  "latitude": 30.59,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "categoryId": null,
  "safetyEnvironmentScore": 25,
  "businessRightsScore": 20,
  "experienceFriendlyScore": 20,
  "socialContributionScore": 15,
  "story": null,
  "recommendReason": "M2 湖景推荐理由",
  "weight": 0,
  "online": true,
  "periods": [],
  "tagIds": [],
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VhG4xjep69%2FnLiwRS%2FjtpTcxeK8%3D"
    }
  ],
  "createdAt": "2026-08-16T15:42:40.648382599Z",
  "updatedAt": "2026-08-16T15:42:40.648382599Z"
}
```

## Step 4: 前置：POST /api/admin/recommend-lists 创建清单 L7

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "清单维护用例154239",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 1
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4861-7691-9ffb-595f36c7d477",
  "title": "清单维护用例154239",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 1,
  "merchants": [],
  "createdAt": "2026-08-16T15:42:40.735161772Z",
  "updatedAt": "2026-08-16T15:42:40.735161772Z"
}
```

## Step 5: PUT /{id}/merchants 全量替换 [M2:1, M1:2]

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/recommend-lists/01a00b3d-4861-7691-9ffb-595f36c7d477/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '[
  {
    "merchantId": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
    "sortOrder": 1
  },
  {
    "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
    "sortOrder": 2
  }
]'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4861-7691-9ffb-595f36c7d477",
  "title": "清单维护用例154239",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 1,
  "merchants": [
    {
      "merchantId": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
      "name": "商户M二154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
      "name": "商户M一154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-16T15:42:40.735162Z",
  "updatedAt": "2026-08-16T15:42:40.735162Z"
}
```

## Step 6: GET /api/admin/recommend-lists/{id}

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/01a00b3d-4861-7691-9ffb-595f36c7d477" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4861-7691-9ffb-595f36c7d477",
  "title": "清单维护用例154239",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 1,
  "merchants": [
    {
      "merchantId": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
      "name": "商户M二154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    },
    {
      "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
      "name": "商户M一154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-16T15:42:40.735162Z",
  "updatedAt": "2026-08-16T15:42:40.735162Z"
}
```
