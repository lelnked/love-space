# TC-featured-IT-003 PUT /api/admin/featured-items/{id}/online 上下线切换 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：创建 online=true 的推荐条目

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b", "banner": "images/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png", "description": "上下线", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
  "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
  "banner": {
    "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=68HA2w1SFIUieaUuh25xnH0Nyeg%3D"
  },
  "description": "上下线",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.947217902Z",
  "updatedAt": "2026-08-20T13:47:15.947217902Z"
}
```

## Step 2: PUT /online 置 false

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a01f6d-0e6b-7455-9f29-f387d57d7d09/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
  "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
  "banner": {
    "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=68HA2w1SFIUieaUuh25xnH0Nyeg%3D"
  },
  "description": "上下线",
  "online": false,
  "createdAt": "2026-08-20T13:47:15.947218Z",
  "updatedAt": "2026-08-20T13:47:15.947218Z"
}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/01a01f6d-0e6b-7455-9f29-f387d57d7d09" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
  "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
  "banner": {
    "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=68HA2w1SFIUieaUuh25xnH0Nyeg%3D"
  },
  "description": "上下线",
  "online": false,
  "createdAt": "2026-08-20T13:47:15.947218Z",
  "updatedAt": "2026-08-20T13:47:15.953766Z"
}
```

## Step 4: PUT /online 置 true

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a01f6d-0e6b-7455-9f29-f387d57d7d09/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
  "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
  "banner": {
    "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=68HA2w1SFIUieaUuh25xnH0Nyeg%3D"
  },
  "description": "上下线",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.947218Z",
  "updatedAt": "2026-08-20T13:47:15.953766Z"
}
```

## Step 5: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/01a01f6d-0e6b-7455-9f29-f387d57d7d09" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e6b-7455-9f29-f387d57d7d09",
  "cityId": "01a01f6d-0e65-7836-a7a1-95053c3c2c7b",
  "banner": {
    "id": "bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/6e9afd2d-084d-4250-bd68-15cbff7d2faa.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=68HA2w1SFIUieaUuh25xnH0Nyeg%3D"
  },
  "description": "上下线",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.947218Z",
  "updatedAt": "2026-08-20T13:47:15.963334Z"
}
```

