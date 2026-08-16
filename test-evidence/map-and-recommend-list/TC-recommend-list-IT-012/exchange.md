# TC-recommend-list-IT-012 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 补跑说明：首轮因 test 实例 OSS 不可用标记未执行；现 admin test 实例已启用 test-profile StubObjectKeyValidator（正则校验后直接返回 bound key），本轮补跑。城市夹具复用首轮：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b（上架）、城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473（上架）。

## Step 1: 前置：POST /api/admin/recommend-lists 创建清单 L12

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "App 详情清单154239",
  "introduction": "含商户明细",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 2
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4b82-7e9e-9297-f6b20d81c609",
  "title": "App 详情清单154239",
  "introduction": "含商户明细",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 2,
  "merchants": [],
  "createdAt": "2026-08-16T15:42:41.538827349Z",
  "updatedAt": "2026-08-16T15:42:41.538827349Z"
}
```

## Step 2: 前置：PUT /{id}/merchants [M2:1, M1:2]

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/recommend-lists/01a00b3d-4b82-7e9e-9297-f6b20d81c609/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '[
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
  "id": "01a00b3d-4b82-7e9e-9297-f6b20d81c609",
  "title": "App 详情清单154239",
  "introduction": "含商户明细",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 2,
  "merchants": [
    {
      "merchantId": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
      "name": "商户M二154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896761&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aKfcHzsHT9SIdzXhBIVFd1ugMSY%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896761&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aKfcHzsHT9SIdzXhBIVFd1ugMSY%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 2
    }
  ],
  "createdAt": "2026-08-16T15:42:41.538827Z",
  "updatedAt": "2026-08-16T15:42:41.538827Z"
}
```

## Step 3: GET /api/app/recommend-lists/{id}（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/recommend-lists/01a00b3d-4b82-7e9e-9297-f6b20d81c609" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4b82-7e9e-9297-f6b20d81c609",
  "title": "App 详情清单154239",
  "introduction": "含商户明细",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 2,
  "merchants": [
    {
      "merchantId": "01a00b3d-4808-7742-91d0-b662f1b8bfc3",
      "name": "商户M二154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896761&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aKfcHzsHT9SIdzXhBIVFd1ugMSY%3D"
      },
      "address": "测试路 1 号",
      "recommendReason": "M2 湖景推荐理由",
      "sortOrder": 1
    },
    {
      "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
      "name": "商户M一154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896761&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=aKfcHzsHT9SIdzXhBIVFd1ugMSY%3D"
      },
      "address": "测试路 1 号",
      "recommendReason": "M1 江景推荐理由",
      "sortOrder": 2
    }
  ]
}
```
