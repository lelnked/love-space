# TC-file-IT-004 请求/响应存证

未绑定图片在业务保存时被改写为 bound/ 前缀

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/banners imageUrls=[images/...1001.png]

```bash
curl -s -i -X POST http://localhost:21423/api/admin/banners -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"name": "绑定用例-af4f49", "positionCode": "home-top", "type": "CITY", "imageUrls": ["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png"], "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5", "sortOrder": 0}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-1581-7d40-b6aa-9dfe8be01a00",
  "name": "绑定用例-af4f49",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png?Expires=1788507163&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NssGlHLiXyacNOC1Mo9zdCJek7U%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:02:43.841766147Z",
  "updatedAt": "2026-09-04T07:02:43.841766147Z"
}
```

## Step 2: GET /api/admin/banners/01a06b3a-1581-7d40-b6aa-9dfe8be01a00

```bash
curl -s -i -X GET http://localhost:21423/api/admin/banners/01a06b3a-1581-7d40-b6aa-9dfe8be01a00 -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-1581-7d40-b6aa-9dfe8be01a00",
  "name": "绑定用例-af4f49",
  "positionCode": "home-top",
  "type": "CITY",
  "imageUrls": [
    {
      "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png",
      "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png?Expires=1788507163&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=NssGlHLiXyacNOC1Mo9zdCJek7U%3D"
    }
  ],
  "link": "01a06b34-02a1-7873-8f51-9dd37e4241d5",
  "linkedCityName": "精选城-65b718d3-1c3f-4699-9949-21d459f2011f",
  "online": false,
  "sortOrder": 0,
  "createdAt": "2026-09-04T07:02:43.841766Z",
  "updatedAt": "2026-09-04T07:02:43.841766Z"
}
```
