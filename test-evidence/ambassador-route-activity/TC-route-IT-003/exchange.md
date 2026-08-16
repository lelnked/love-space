# TC-route-IT-003 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: 前置：创建 online=true 的大使

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png","name":"切换大使-162725","tags":["t"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4250-730f-bff3-a56bbc9be7e6",
  "avatar": {
    "id": "bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=007MItpArZkvGdgbnEBcPgJjGdc%3D"
  },
  "name": "切换大使-162725",
  "tags": [
    "t"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:26.160091489Z",
  "updatedAt": "2026-08-16T16:27:26.160091489Z"
}
```

## Step 2: PUT /online 下线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a00b66-4250-730f-bff3-a56bbc9be7e6/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4250-730f-bff3-a56bbc9be7e6",
  "avatar": {
    "id": "bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=007MItpArZkvGdgbnEBcPgJjGdc%3D"
  },
  "name": "切换大使-162725",
  "tags": [
    "t"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.160091Z",
  "updatedAt": "2026-08-16T16:27:26.160091Z"
}
```

## Step 3: GET 详情确认 online=false

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-4250-730f-bff3-a56bbc9be7e6" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4250-730f-bff3-a56bbc9be7e6",
  "avatar": {
    "id": "bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=007MItpArZkvGdgbnEBcPgJjGdc%3D"
  },
  "name": "切换大使-162725",
  "tags": [
    "t"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.160091Z",
  "updatedAt": "2026-08-16T16:27:26.200794Z"
}
```

## Step 4: PUT /online 重新上线

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/ambassadors/01a00b66-4250-730f-bff3-a56bbc9be7e6/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4250-730f-bff3-a56bbc9be7e6",
  "avatar": {
    "id": "bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=007MItpArZkvGdgbnEBcPgJjGdc%3D"
  },
  "name": "切换大使-162725",
  "tags": [
    "t"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:26.160091Z",
  "updatedAt": "2026-08-16T16:27:26.200794Z"
}
```

## Step 5: GET 详情确认 online=true（可往返）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-4250-730f-bff3-a56bbc9be7e6" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4250-730f-bff3-a56bbc9be7e6",
  "avatar": {
    "id": "bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b655f1a4-60ce-4a30-aa53-727bc49a41a6.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=007MItpArZkvGdgbnEBcPgJjGdc%3D"
  },
  "name": "切换大使-162725",
  "tags": [
    "t"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:26.160091Z",
  "updatedAt": "2026-08-16T16:27:26.288385Z"
}
```

