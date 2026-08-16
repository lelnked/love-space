# TC-route-IT-001 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: POST /api/admin/ambassadors 创建大使（tags 2 条，顺序 古着→咖啡）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/aaf89b6f-436c-477e-ac82-fde385411f4a.png","name":"小满","tags":["古着","咖啡"],"online":true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4155-7223-9514-748415ba0f0e",
  "avatar": {
    "id": "bound/aaf89b6f-436c-477e-ac82-fde385411f4a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/aaf89b6f-436c-477e-ac82-fde385411f4a.png?Expires=1786899445&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WjI9h%2ByJqRrW1SKc6lRSJaZuf9g%3D"
  },
  "name": "小满",
  "tags": [
    "古着",
    "咖啡"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:25.909044171Z",
  "updatedAt": "2026-08-16T16:27:25.909044171Z"
}
```

## Step 2: GET /api/admin/ambassadors/{id} 详情

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-4155-7223-9514-748415ba0f0e" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-4155-7223-9514-748415ba0f0e",
  "avatar": {
    "id": "bound/aaf89b6f-436c-477e-ac82-fde385411f4a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/aaf89b6f-436c-477e-ac82-fde385411f4a.png?Expires=1786899445&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WjI9h%2ByJqRrW1SKc6lRSJaZuf9g%3D"
  },
  "name": "小满",
  "tags": [
    "古着",
    "咖啡"
  ],
  "online": true,
  "createdAt": "2026-08-16T16:27:25.909044Z",
  "updatedAt": "2026-08-16T16:27:25.909044Z"
}
```

