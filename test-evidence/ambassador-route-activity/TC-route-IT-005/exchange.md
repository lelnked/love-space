# TC-route-IT-005 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建未被路线引用的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/5322c76c-fb0b-4451-9305-6765431e9180.png","name":"待删大使-162725"}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4394-7632-be8f-fa8510e0b61a",
  "avatar": {
    "id": "bound/5322c76c-fb0b-4451-9305-6765431e9180.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/5322c76c-fb0b-4451-9305-6765431e9180.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=EskZpK6AMWXDUHZ4aB2MGZSAkAw%3D"
  },
  "name": "待删大使-162725",
  "tags": [],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.484298552Z",
  "updatedAt": "2026-08-16T16:27:26.484298552Z"
}
```

## Step 2: DELETE /api/admin/ambassadors/{id}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/ambassadors/01a00b66-4394-7632-be8f-fa8510e0b61a" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200



```

## Step 3: GET 已删除大使详情（应 400 中文口径）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-4394-7632-be8f-fa8510e0b61a" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "爱女大使不存在：01a00b66-4394-7632-be8f-fa8510e0b61a",
  "path": "/api/admin/ambassadors/01a00b66-4394-7632-be8f-fa8510e0b61a"
}
```

