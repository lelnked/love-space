# TC-file-IT-007 请求/响应存证

非白名单后缀与路径穿越的 objectKey 被拒绝

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/banners imageUrls=['images/abc.exe']

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "非法key0-c2c00e", "positionCode": "home-top", "type": "CITY", "imageUrls": ["images/abc.exe"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）",
  "path": "/api/admin/banners"
}
```

## Step 2: POST /api/admin/banners imageUrls=['images/../../etc/passwd.png']

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "非法key1-c2c00e", "positionCode": "home-top", "type": "CITY", "imageUrls": ["images/../../etc/passwd.png"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）",
  "path": "/api/admin/banners"
}
```

## Step 3: POST /api/admin/banners imageUrls=['']

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "非法key2-c2c00e", "positionCode": "home-top", "type": "CITY", "imageUrls": [""], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 400）:

```
HTTP/1.1 400
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）, 图片不能为空",
  "path": "/api/admin/banners"
}
```

## Step 4: GET /api/admin/banners/page?keyword=非法key 核对未创建

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/banners/page?keyword=%E9%9D%9E%E6%B3%95key&page=0&size=20' -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "content": [],
  "page": 1,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```
