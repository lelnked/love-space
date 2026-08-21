# TC-featured-IT-004 PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变 — 请求/响应存证

执行日期: 2026-08-20 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 走 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 $TOKEN；
shell 中 `export TOKEN=<登录返回 token>` 后下列 curl 可原样执行。app 端请求头 `X-API-Key: test-api-key`。

## Step 1: 前置：城市 A 下创建推荐条目（另建城市 B）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/featured-items" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e85-78c3-b42c-17544a8c99e5", "banner": "images/a54950f7-0f5e-497c-8fe4-3d766e646c25.png", "description": "原说明", "online": true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1",
  "cityId": "01a01f6d-0e85-78c3-b42c-17544a8c99e5",
  "banner": {
    "id": "bound/a54950f7-0f5e-497c-8fe4-3d766e646c25.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a54950f7-0f5e-497c-8fe4-3d766e646c25.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=nQZBgfvydBC66BACHgKrOOLyQhw%3D"
  },
  "description": "原说明",
  "online": true,
  "createdAt": "2026-08-20T13:47:15.983624801Z",
  "updatedAt": "2026-08-20T13:47:15.983624801Z"
}
```

## Step 2: PUT 改 description/banner，并把 cityId 传成城市 B

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/featured-items/01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"cityId": "01a01f6d-0e8a-7df8-b54e-bb926775cc47", "banner": "images/baaf11a1-0478-48c3-85d8-625a9838d2e0.png", "description": "改写后的说明"}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1",
  "cityId": "01a01f6d-0e85-78c3-b42c-17544a8c99e5",
  "banner": {
    "id": "bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yVHzp8kySYkFhB9WEYB1LgBbcho%3D"
  },
  "description": "改写后的说明",
  "online": false,
  "createdAt": "2026-08-20T13:47:15.983625Z",
  "updatedAt": "2026-08-20T13:47:15.983625Z"
}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/featured-items/01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a01f6d-0e8f-7ad2-a2f2-2075f1f239a1",
  "cityId": "01a01f6d-0e85-78c3-b42c-17544a8c99e5",
  "banner": {
    "id": "bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/baaf11a1-0478-48c3-85d8-625a9838d2e0.png?Expires=1787235435&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=yVHzp8kySYkFhB9WEYB1LgBbcho%3D"
  },
  "description": "改写后的说明",
  "online": false,
  "createdAt": "2026-08-20T13:47:15.983625Z",
  "updatedAt": "2026-08-20T13:47:15.989359Z"
}
```

