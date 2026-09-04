# TC-file-IT-006 请求/响应存证

非白名单前缀的 objectKey 被拒绝

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/banners imageUrls=[other/abc.png]

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "非法前缀-c2c00e", "positionCode": "home-top", "type": "CITY", "imageUrls": ["other/abc.png"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
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

## Step 2: GET /api/admin/banners/page?keyword=<name> 核对未创建

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/banners/page?keyword=%E9%9D%9E%E6%B3%95%E5%89%8D%E7%BC%80-c2c00e&page=0&size=5' -H 'Authorization: Bearer $TOKEN'
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
