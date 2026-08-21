# TC-featured-IT-005 DELETE /api/admin/featured-items/{id} 物理删除 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建一个推荐条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e9d-7f27-a412-d8a87561e488", "banner": "images/afec042d-b169-44b3-bb00-63b3fd46997d.png", "description": "待删除", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0ea3-70d4-91db-0f95ce15c4b9",
  "cityId": "01a01f6d-0e9d-7f27-a412-d8a87561e488",
  "banner": {
    "id": "bound/afec042d-b169-44b3-bb00-63b3fd46997d.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/afec042d-b169-44b3-bb00-63b3fd46997d.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=47AEIaa%2BosjXerMoyn5OQcSOEI8%3D"
  },
  "description": "待删除",
  "online": true,
  "createdAt": "2026-08-20T13:47:16.002997842Z",
  "updatedAt": "2026-08-20T13:47:16.002997842Z"
}
```

## Step 2: DELETE 条目

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/featured-items/01a01f6d-0ea3-70d4-91db-0f95ce15c4b9" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: ）:

```json
null
```

## Step 3: GET 已删除条目详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/01a01f6d-0ea3-70d4-91db-0f95ce15c4b9" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400，Content-Type: application/json）:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "精选推荐不存在：01a01f6d-0ea3-70d4-91db-0f95ce15c4b9",
  "path": "/api/admin/featured-items/01a01f6d-0ea3-70d4-91db-0f95ce15c4b9"
}
```

## Step 4: GET 分页列表确认不含该条目

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/page?size=100" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "content": [
    {
      "id": "01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1",
      "cityId": "01a01f6d-0e85-78c3-b42c-17544a8c99e5",
      "banner": {
        "id": "bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=wFTKyzOesKmPVxo5IOi3kAM5c%2FI%3D"
      },
      "description": "改写后的说明",
      "online": false,
      "createdAt": "2026-08-20T13:47:15.983625Z",
      "updatedAt": "2026-08-20T13:47:15.989359Z"
    },
    {
      "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
      "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
      "banner": {
        "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=rZfzzeo%2B%2F2jILMT0ln85LeOAqK0%3D"
      },
      "description": "上下线",
      "online": true,
      "createdAt": "2026-08-20T13:47:15.947218Z",
      "updatedAt": "2026-08-20T13:47:15.963334Z"
    },
    {
      "id": "01a01f6d-0e3c-75a3-9cd2-eb2beb0cb050",
      "cityId": "01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1",
      "banner": {
        "id": "bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png",
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/c2c2405d-d892-4238-b0bd-31535d2b83e8.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nLzo4sv3XI7v1q%2BvN126eAAfJPM%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/01a00ba7-855b-76fa-9b41-b58211e630de.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=pfB5A9T66jd5uJBVt92czPuyz7Y%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat001r-172742.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WWe0H8fRacucETAfE%2BotB%2FDFgNE%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-off-172204.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=TYPWWDrG21syeFae6OKCAOJdWtk%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-two-172204.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=tRcM7D5nJnspXgKU0s%2F%2B0saw2TE%3D"
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
        "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/feat006-one-172204.png?Expires=1787235436&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=DteElQl754aKWqIK7coVIywykMA%3D"
      },
      "description": "信息流条目一",
      "online": true,
      "createdAt": "2026-08-16T17:22:07.638465Z",
      "updatedAt": "2026-08-16T17:22:07.638465Z"
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 8,
  "totalPages": 1
}
```

