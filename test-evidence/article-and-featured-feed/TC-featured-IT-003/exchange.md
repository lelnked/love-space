# TC-featured-IT-003 PUT /api/admin/featured-items/{id}/online 上下线切换 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> 前置：复用 TC-featured-IT-001 创建的 online=true 条目（id=01a00b98-5274-719c-8619-76dc30c748df）；token 复用本轮统一登录

## Step 1: PUT /api/admin/featured-items/{id}/online {"online": false}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat001-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=c803ZZuGu0btLom6w2Fj2uIVWgs%3D"
  },
  "description": "地图上新",
  "online": false,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.091985Z"
}
```

## Step 2: GET /api/admin/featured-items/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat001-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=c803ZZuGu0btLom6w2Fj2uIVWgs%3D"
  },
  "description": "地图上新",
  "online": false,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.313634Z"
}
```

## Step 3: PUT /api/admin/featured-items/{id}/online {"online": true}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat001-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=c803ZZuGu0btLom6w2Fj2uIVWgs%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.313634Z"
}
```

## Step 4: GET /api/admin/featured-items/{id} 再查详情

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/01a00b98-5274-719c-8619-76dc30c748df" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5274-719c-8619-76dc30c748df",
  "cityId": "01a00b98-524e-7a4a-ba99-d12f1e7e3297",
  "banner": {
    "id": "bound/feat001-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001-172204.png?Expires=1786902727&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=c803ZZuGu0btLom6w2Fj2uIVWgs%3D"
  },
  "description": "地图上新",
  "online": true,
  "createdAt": "2026-08-16T17:22:07.091985Z",
  "updatedAt": "2026-08-16T17:22:07.372746Z"
}
```
