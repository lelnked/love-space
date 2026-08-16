# TC-featured-IT-006 GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；上架城市复用 TC-featured-IT-001 创建的「精选城172204」（id=01a00b98-524e-7a4a-ba99-d12f1e7e3297）

## Step 1: 前置 1/3：创建上线条目 1（先建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b98-524e-7a4a-ba99-d12f1e7e3297","banner":"images/feat006-one-172204.png","description":"信息流条目一","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5496-7871-9f38-146dde409188",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat006-one-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-one-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ag69GqEwOtdYQJD4WFGLwMX2fp4%3D"
  },
  "description": "信息流条目一",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.638465017Z",
  "updatedAt": "2026-08-16T17:22:07.638465017Z"
}
```

## Step 2: 前置 2/3：创建上线条目 2（后建）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b98-524e-7a4a-ba99-d12f1e7e3297","banner":"images/feat006-two-172204.png","description":"信息流条目二","online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-54b4-7e01-b004-225b720f27a6",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat006-two-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-two-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=C8Rhtf0mIRURuL7tVoKSwT7MqtQ%3D"
  },
  "description": "信息流条目二",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.668808804Z",
  "updatedAt": "2026-08-16T17:22:07.668808804Z"
}
```

## Step 3: 前置 3/3：创建下线条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b98-524e-7a4a-ba99-d12f1e7e3297","banner":"images/feat006-off-172204.png","description":"下线条目","online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-54d2-7d90-9a27-92ccb92422ad",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat006-off-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-off-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=QDf7lnicqb2JC3FyhnhezrSa%2BVA%3D"
  },
  "description": "下线条目",
  "online": false,
  "createdAt": "2026-08-16T17:22:07.698775168Z",
  "updatedAt": "2026-08-16T17:22:07.698775168Z"
}
```

## Step 4: GET /api/app/featured-items（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/featured-items" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
[
  {
    "id": "01a00b98-54b4-7e01-b004-225b720f27a6",
    "banner": {
      "id": "bound/feat006-two-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-two-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=C8Rhtf0mIRURuL7tVoKSwT7MqtQ%3D"
    },
    "description": "信息流条目二",
    "city": {
      "id": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "name": "精选城172204"
    }
  },
  {
    "id": "01a00b98-5496-7871-9f38-146dde409188",
    "banner": {
      "id": "bound/feat006-one-172204.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-one-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ag69GqEwOtdYQJD4WFGLwMX2fp4%3D"
    },
    "description": "信息流条目一",
    "city": {
      "id": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "name": "精选城172204"
    }
  }
]
```
