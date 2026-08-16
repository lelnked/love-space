# TC-route-IT-004 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建大使（小满、2 条标签）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/64184beb-abed-4baa-a398-e4f47f12d80a.png","name":"小满","tags":["古着","咖啡"]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4314-7fcb-97f0-9a6a80f5cfa3",
  "avatar": {
    "id": "bound/64184beb-abed-4baa-a398-e4f47f12d80a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/64184beb-abed-4baa-a398-e4f47f12d80a.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=n%2F%2BO%2FEZtuSewhDzhQ3NdZKcOGEE%3D"
  },
  "name": "小满",
  "tags": [
    "古着",
    "咖啡"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.356904978Z",
  "updatedAt": "2026-08-16T16:27:26.356904978Z"
}
```

## Step 2: PUT /api/admin/ambassadors/{id} 更新 name/tags/avatar

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a00b66-4314-7fcb-97f0-9a6a80f5cfa3" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/07446555-4ee5-4e29-966d-3fb1c6a2caf6.jpg","name":"小满改","tags":["旅拍"]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4314-7fcb-97f0-9a6a80f5cfa3",
  "avatar": {
    "id": "bound/07446555-4ee5-4e29-966d-3fb1c6a2caf6.jpg",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/07446555-4ee5-4e29-966d-3fb1c6a2caf6.jpg?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5rzdNqvY8WrWw35FC%2BBT09rnf18%3D"
  },
  "name": "小满改",
  "tags": [
    "旅拍"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.356905Z",
  "updatedAt": "2026-08-16T16:27:26.356905Z"
}
```

## Step 3: GET 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-4314-7fcb-97f0-9a6a80f5cfa3" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4314-7fcb-97f0-9a6a80f5cfa3",
  "avatar": {
    "id": "bound/07446555-4ee5-4e29-966d-3fb1c6a2caf6.jpg",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/07446555-4ee5-4e29-966d-3fb1c6a2caf6.jpg?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=5rzdNqvY8WrWw35FC%2BBT09rnf18%3D"
  },
  "name": "小满改",
  "tags": [
    "旅拍"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.356905Z",
  "updatedAt": "2026-08-16T16:27:26.402766Z"
}
```

