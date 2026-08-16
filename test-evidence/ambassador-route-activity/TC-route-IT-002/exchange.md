# TC-route-IT-002 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，shell 中 export TOKEN 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key

## Step 1: POST /api/admin/ambassadors 恰好 3 条标签

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/bfb30d00-7c38-46ec-8506-b076e2edf517.png","name":"三标大使-162725","tags":["a","b","c"]}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-41c1-7109-95e9-bd1abf605de3",
  "avatar": {
    "id": "bound/bfb30d00-7c38-46ec-8506-b076e2edf517.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bfb30d00-7c38-46ec-8506-b076e2edf517.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7WaJPCg1V9f%2BHO%2FlTghnJztGdEM%3D"
  },
  "name": "三标大使-162725",
  "tags": [
    "a",
    "b",
    "c"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.016982351Z",
  "updatedAt": "2026-08-16T16:27:26.016982351Z"
}
```

## Step 2: GET 详情确认 tags 为 3 条

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/01a00b66-41c1-7109-95e9-bd1abf605de3" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a00b66-41c1-7109-95e9-bd1abf605de3",
  "avatar": {
    "id": "bound/bfb30d00-7c38-46ec-8506-b076e2edf517.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/bfb30d00-7c38-46ec-8506-b076e2edf517.png?Expires=1786899446&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7WaJPCg1V9f%2BHO%2FlTghnJztGdEM%3D"
  },
  "name": "三标大使-162725",
  "tags": [
    "a",
    "b",
    "c"
  ],
  "online": false,
  "createdAt": "2026-08-16T16:27:26.016982Z",
  "updatedAt": "2026-08-16T16:27:26.016982Z"
}
```

## Step 3: POST /api/admin/ambassadors 4 条标签（应拒绝）

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/ambassadors" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"avatar":"images/66f24f47-8570-4774-8f1f-16ad71d38e24.png","name":"四标大使-162725","tags":["a","b","c","d"]}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "大使标签最多 3 条",
  "path": "/api/admin/ambassadors"
}
```

## Step 4: GET /api/admin/ambassadors/page 确认四标大使未创建

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/page?page=0&size=200&keyword=四标大使-162725" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: text/html;charset=utf-8

<!doctype html><html lang="en"><head><title>HTTP Status 400 – Bad Request</title><style type="text/css">body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}</style></head><body><h1>HTTP Status 400 – Bad Request</h1></body></html>
```

## Step 5: GET /api/admin/ambassadors/page 确认四标大使未创建（keyword 已 URL 编码；step 4 的 400 为存证脚本未编码中文导致，非后端问题）

```bash
curl -s -i -X GET "http://localhost:21423/api/admin/ambassadors/page?page=0&size=200&keyword=%E5%9B%9B%E6%A0%87%E5%A4%A7%E4%BD%BF-162725" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 30,
  "totalElements": 0,
  "totalPages": 0
}
```

