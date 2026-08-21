# TC-featured-IT-002 POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: POST 缺 banner（cityId 合法）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e49-77e8-b3a5-292f99396d7e", "description": "D", "online": true}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "banner 图片不能为空",
  "path": "/api/admin/featured-items"
}
```

## Step 2: POST cityId 为不存在的 UUID（banner 合法）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "cb6362fc-af74-4987-a090-7026442a0ea3", "banner": "images/586e8320-43db-447d-8372-2c577e3401eb.png", "description": "D"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联城市不存在：cb6362fc-af74-4987-a090-7026442a0ea3",
  "path": "/api/admin/featured-items"
}
```

## Step 3: POST 缺 cityId

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner": "images/5a7746f7-13d6-4a10-93e3-6faf0981a8f6.png", "description": "D"}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联城市不能为空",
  "path": "/api/admin/featured-items"
}
```

## Step 4: 复查条目未创建

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/page?size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
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
    },
    {
      "id": "01a00ba7-8964-7c8a-862b-49d723784db2",
      "cityId": "01a00b9d-71e4-7135-aa5d-4a7b6c907065",
      "banner": {
        "id": "bound/01a00ba7-855b-76fa-9b41-b58211e630de.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a00ba7-855b-76fa-9b41-b58211e630de.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=z%2FIRuiud%2BazdsPopCQP9X4cyghs%3D"
      },
      "description": "WEB002精选说明902189",
      "online": false,
      "createdAt": "2026-08-16T17:38:44.196678Z",
      "updatedAt": "2026-08-16T17:38:44.196678Z"
    },
    {
      "id": "01a00b9d-7209-7e5d-be6b-33f64275ff19",
      "cityId": "01a00b9d-71e4-7135-aa5d-4a7b6c907065",
      "banner": {
        "id": "bound/feat001r-172742.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001r-172742.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=KdbEYn4R%2Bc2qrGpuWacd%2BKtDaSc%3D"
      },
      "description": "地图上新",
      "online": true,
      "createdAt": "2026-08-16T17:27:42.856083Z",
      "updatedAt": "2026-08-16T17:38:40.440761Z"
    },
    {
      "id": "01a00b98-54d2-7d90-9a27-92ccb92422ad",
      "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "banner": {
        "id": "bound/feat006-off-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-off-172204.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=w3ZRWTwY%2BNvj04zL82EYJ9BTd6M%3D"
      },
      "description": "下线条目",
      "online": false,
      "createdAt": "2026-08-16T17:22:07.698775Z",
      "updatedAt": "2026-08-16T17:22:07.698775Z"
    },
    {
      "id": "01a00b98-54b4-7e01-b004-225b720f27a6",
      "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "banner": {
        "id": "bound/feat006-two-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-two-172204.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7XA7tlRn371fkrXRixKLM4KntIQ%3D"
      },
      "description": "信息流条目二",
      "online": true,
      "createdAt": "2026-08-16T17:22:07.668809Z",
      "updatedAt": "2026-08-16T17:22:07.668809Z"
    },
    {
      "id": "01a00b98-5496-7871-9f38-146dde409188",
      "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
      "banner": {
        "id": "bound/feat006-one-172204.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-one-172204.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ds5H28V8nzAhZ977OJ0q8zavtQg%3D"
      },
      "description": "信息流条目一",
      "online": true,
      "createdAt": "2026-08-16T17:22:07.638465Z",
      "updatedAt": "2026-08-16T17:22:07.638465Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 6,
  "totalPages": 1
}
```

