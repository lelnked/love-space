# TC-featured-IT-002 POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；合法城市取 TC-featured-IT-001 创建的（id=01a00b98-524e-7a4a-ba99-d12f1e7e3297）

## Step 1: 前置采样：GET /api/admin/featured-items/page 记录当前条目数

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/page?page=0&size=1" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
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
      "updatedAt": "2026-08-16T17:22:07.091985Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

## Step 2: POST /api/admin/featured-items 缺 banner

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"01a00b98-524e-7a4a-ba99-d12f1e7e3297","description":"缺banner","online":true}'
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

## Step 3: POST /api/admin/featured-items cityId 为不存在的 UUID

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId":"00000000-0000-0000-0000-000000000999","banner":"images/feat002-172204.png","description":"城市不存在","online":true}'
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "关联城市不存在：00000000-0000-0000-0000-000000000999",
  "path": "/api/admin/featured-items"
}
```

## Step 4: POST /api/admin/featured-items 缺 cityId

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"banner":"images/feat002b-172204.png","description":"缺cityId","online":true}'
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

## Step 5: GET /api/admin/featured-items/page 确认条目均未创建

```bash
curl -s -i "http://localhost:21423/api/admin/featured-items/page?page=0&size=1" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
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
      "updatedAt": "2026-08-16T17:22:07.091985Z"
    }
  ],
  "page": 1,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```
