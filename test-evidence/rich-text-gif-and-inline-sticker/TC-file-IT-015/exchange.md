# TC-file-IT-015 请求/响应存证

gif 后缀 objectKey 通过绑定校验，svg 后缀仍被拒绝

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/banners imageUrls=[images/...1501.gif]

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "gif绑定-6e6b87", "positionCode": "home-top", "type": "CITY", "imageUrls": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3d-21a9-74db-b9aa-9f0e53821ca7",
  "name": "gif绑定-6e6b87",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif?Expires=1788507363&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VqMAZp%2F23peibDWhUMBFUt1z14w%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:06:03.548187046Z",
  "updatedAt": "2026-09-04T07:06:03.548187046Z"
}
```

## Step 2: GET /api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7

```bash
curl -s -i -X GET http://localhost:21423/api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3d-21a9-74db-b9aa-9f0e53821ca7",
  "name": "gif绑定-6e6b87",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif?Expires=1788507363&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VqMAZp%2F23peibDWhUMBFUt1z14w%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:06:03.548187Z",
  "updatedAt": "2026-09-04T07:06:03.548187Z"
}
```

## Step 3: POST /api/admin/banners imageUrls=[images/...1502.svg]

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "svg绑定-6e6b87", "positionCode": "home-top", "type": "CITY", "imageUrls": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1502.svg"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
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

## Step 4: PUT /api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7 原样回传 bound gif key

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7 -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "gif绑定-6e6b87", "positionCode": "home-top", "type": "CITY", "imageUrls": ["bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3d-21a9-74db-b9aa-9f0e53821ca7",
  "name": "gif绑定-6e6b87",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif?Expires=1788507363&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VqMAZp%2F23peibDWhUMBFUt1z14w%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:06:03.548187Z",
  "updatedAt": "2026-09-04T07:06:03.548187Z"
}
```

## Step 5: GET /api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7 核对 key 不变

```bash
curl -s -i -X GET http://localhost:21423/api/admin/banners/01a06b3d-21a9-74db-b9aa-9f0e53821ca7 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3d-21a9-74db-b9aa-9f0e53821ca7",
  "name": "gif绑定-6e6b87",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif?Expires=1788507363&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=VqMAZp%2F23peibDWhUMBFUt1z14w%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:06:03.548187Z",
  "updatedAt": "2026-09-04T07:06:03.548187Z"
}
```
