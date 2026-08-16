# TC-recommend-list-IT-005 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423
认证: POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）

> 重跑说明：预期结果已裁决更新——删除后再查详情按 admin 全域「资源不存在→400」口径断言 400 及中文业务错误（决策见 design.md）。前置复用：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b、商户 M1=01a00b3d-47e1-7d97-8d01-dbfb69749d2b（补跑轮 TC-recommend-list-IT-007 创建）。

## Step 1: 前置：POST /api/admin/recommend-lists 创建清单 L5

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/recommend-lists" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "title": "待删除清单154506",
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3f-8347-7d61-bc7d-225923c4ef4c",
  "title": "待删除清单154506",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 0,
  "merchants": [],
  "createdAt": "2026-08-16T15:45:06.887272554Z",
  "updatedAt": "2026-08-16T15:45:06.887272554Z"
}
```

## Step 2: 前置：PUT /{id}/merchants 关联 M1

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/recommend-lists/01a00b3f-8347-7d61-bc7d-225923c4ef4c/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '[
  {
    "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
    "sortOrder": 1
  }
]'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3f-8347-7d61-bc7d-225923c4ef4c",
  "title": "待删除清单154506",
  "introduction": null,
  "cityId": "01a00b34-a31b-726c-8c15-95d9ccbae26b",
  "sortOrder": 0,
  "merchants": [
    {
      "merchantId": "01a00b3d-47e1-7d97-8d01-dbfb69749d2b",
      "name": "商户M一154239",
      "logo": {
        "id": "bound/logo-test.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896906&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=SyWy5FnShW6hiaYpUVrWVfodYRw%3D"
      },
      "address": "测试路 1 号",
      "online": true,
      "sortOrder": 1
    }
  ],
  "createdAt": "2026-08-16T15:45:06.887273Z",
  "updatedAt": "2026-08-16T15:45:06.887273Z"
}
```

## Step 3: DELETE /api/admin/recommend-lists/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/recommend-lists/01a00b3f-8347-7d61-bc7d-225923c4ef4c" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 

```

## Step 4: GET /api/admin/recommend-lists/{id}（应 400，资源不存在口径）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/recommend-lists/01a00b3f-8347-7d61-bc7d-225923c4ef4c" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400）:

```
HTTP/1.1 400 
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "推荐清单不存在：01a00b3f-8347-7d61-bc7d-225923c4ef4c",
  "path": "/api/admin/recommend-lists/01a00b3f-8347-7d61-bc7d-225923c4ef4c"
}
```

## Step 5: GET /api/admin/merchants/{M1} 商户仍存在

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/merchants/01a00b3d-47e1-7d97-8d01-dbfb69749d2b" -H "Authorization: Bearer $TOKEN"
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
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896907&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zF%2BcMtMAv2U9CFXqG%2BAgU4xPjFk%3D"
  },
  "address": "测试路 1 号",
  "longitude": 114.300000,
  "latitude": 30.590000,
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
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896907&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=iXNY7Pb6AorpN7Kw%2F3cixZd8768%3D"
    }
  ],
  "createdAt": "2026-08-16T15:42:40.609762Z",
  "updatedAt": "2026-08-16T15:42:40.609762Z"
}
```
