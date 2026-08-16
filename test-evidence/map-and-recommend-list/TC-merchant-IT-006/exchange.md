# TC-merchant-IT-006 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指）；app 侧请求头 X-API-Key: test-api-key

> 补跑说明：首轮因 test 实例 OSS 不可用标记未执行；现 admin test 实例已启用 test-profile StubObjectKeyValidator（正则校验后直接返回 bound key），本轮补跑。城市夹具复用首轮：城市 A=01a00b34-a31b-726c-8c15-95d9ccbae26b（上架）、城市 B=01a00b34-a4cb-7575-bafe-8b3fc7e32473（上架）。

## Step 1: 前置：POST /api/admin/merchants 创建上架商户（recommendReason=江景约会首选）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/merchants" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "name": "商户己154239",
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
  "recommendReason": "江景约会首选"
}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4691-7db0-b351-e78a040bce4f",
  "name": "商户己154239",
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
  "recommendReason": "江景约会首选",
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
  "createdAt": "2026-08-16T15:42:40.27377385Z",
  "updatedAt": "2026-08-16T15:42:40.27377385Z"
}
```

## Step 2: GET /api/app/merchants/{id}（X-API-Key）

```bash
curl -s -i -X GET "http://localhost:8081/api/app/merchants/01a00b3d-4691-7db0-b351-e78a040bce4f" -H "X-API-Key: test-api-key"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "01a00b3d-4691-7db0-b351-e78a040bce4f",
  "name": "商户己154239",
  "logo": {
    "id": "bound/logo-test.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/logo-test.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=mS0RxNYD5B5n8Ib6YXh%2BaNRM3X8%3D"
  },
  "images": [
    {
      "id": "bound/img-test-1.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/img-test-1.png?Expires=1786896760&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VhG4xjep69%2FnLiwRS%2FjtpTcxeK8%3D"
    }
  ],
  "address": "测试路 1 号",
  "longitude": 114.300000,
  "latitude": 30.590000,
  "recommendedPeriods": [],
  "tags": [],
  "scores": {
    "safetyEnvironmentPercent": 83,
    "businessRightsPercent": 80,
    "experienceFriendlyPercent": 80,
    "socialContributionPercent": 75
  },
  "loveIndex": {
    "total": 80,
    "level": 8
  },
  "story": null,
  "recommendReason": "江景约会首选"
}
```
