# TC-file-IT-002 请求/响应存证

非图片 contentType 返回 400

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/files/upload-credentials contentType=application/pdf

```bash
curl -s -i -X POST http://localhost:21423/api/admin/files/upload-credentials -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"contentType": "application/pdf"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "仅支持 png/jpeg/webp/gif 图片",
  "path": "/api/admin/files/upload-credentials"
}
```

## Step 2: POST /api/admin/files/upload-credentials contentType=image/svg+xml

```bash
curl -s -i -X POST http://localhost:21423/api/admin/files/upload-credentials -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"contentType": "image/svg+xml"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "仅支持 png/jpeg/webp/gif 图片",
  "path": "/api/admin/files/upload-credentials"
}
```

## Step 3: POST /api/admin/files/upload-credentials contentType=image/bmp

```bash
curl -s -i -X POST http://localhost:21423/api/admin/files/upload-credentials -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"contentType": "image/bmp"}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "仅支持 png/jpeg/webp/gif 图片",
  "path": "/api/admin/files/upload-credentials"
}
```
