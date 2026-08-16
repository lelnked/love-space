# TC-recommend-list-IT-009 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 补跑说明：首轮因 test 实例 OSS 不可用标记未执行；现 admin test 实例已启用 test-profile StubObjectKeyValidator（正则校验后直接返回 bound key），本轮补跑。城市夹具复用首轮：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b（上架）、城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473（上架）。

> 前置：清单 L7=01a00b3d-4861-7691-9ffb-595f36c7d477 当前商户 [M2,M1]

## Step 1: PUT /{id}/merchants 同一商户 M1 出现两次

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/recommend-lists/01a00b3d-4861-7691-9ffb-595f36c7d477/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '[
  {
    "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
    "sortOrder": 1
  },
  {
    "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
    "sortOrder": 2
  }
]'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400 
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "同一商户不能重复添加到清单",
  "path": "/api/admin/recommend-lists/01a00b3d-4861-7691-9ffb-595f36c7d477/merchants"
}
```

## Step 2: GET /api/admin/recommend-lists/{id} 确认关联未变化

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
  "createdAt": "2026-08-16T15:42:40.735162Z",
  "updatedAt": "2026-08-16T15:42:40.735162Z"
}
```
