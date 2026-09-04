# TC-article-IT-010 请求/响应存证

POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: POST /api/admin/articles contentHtml 含 2 个 images/ objectKey img

```bash
curl -s -i -X POST http://localhost:21423/api/admin/articles -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"image": "images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png", "title": "富文本文章10-af4f49", "subtitle": "副标题", "contentHtml": "<p>文章段落一</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png\"><p>文章段落二</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1002.png\">", "sortOrder": 0, "categoryIds": ["01a06b34-0262-7574-87b4-259859cf92d1"], "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
  },
  "title": "富文本文章10-af4f49",
  "coverTitle": null,
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>文章段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qGt6xTpCc2p8y0Pqs%2BNJCsSPP1E%3D\"><p>文章段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YGg5ioo4j7WbloefV%2FEY6x9wXQM%3D\">",
  "sortOrder": 0,
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ],
  "online": true,
  "createdAt": "2026-09-04T07:02:44.377297223Z",
  "updatedAt": "2026-09-04T07:02:44.377297223Z"
}
```

## Step 2: GET /api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e

```bash
curl -s -i -X GET http://localhost:21423/api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
  },
  "title": "富文本文章10-af4f49",
  "coverTitle": null,
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>文章段落一</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=qGt6xTpCc2p8y0Pqs%2BNJCsSPP1E%3D\"><p>文章段落二</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=YGg5ioo4j7WbloefV%2FEY6x9wXQM%3D\">",
  "sortOrder": 0,
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ],
  "online": true,
  "createdAt": "2026-09-04T07:02:44.377297Z",
  "updatedAt": "2026-09-04T07:02:44.377297Z"
}
```

## Step 3: PUT /api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e contentHtml 改为纯文本

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"image": "images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png", "title": "富文本文章10-af4f49", "subtitle": "副标题", "contentHtml": "<p>纯文本无图</p>", "sortOrder": 0, "categoryIds": ["01a06b34-0262-7574-87b4-259859cf92d1"], "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
  },
  "title": "富文本文章10-af4f49",
  "coverTitle": null,
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>纯文本无图</p>",
  "sortOrder": 0,
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ],
  "online": true,
  "createdAt": "2026-09-04T07:02:44.377297Z",
  "updatedAt": "2026-09-04T07:02:44.404688649Z"
}
```

## Step 4: GET /api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e

```bash
curl -s -i -X GET http://localhost:21423/api/admin/articles/01a06b3a-179a-7ab4-9d81-b6498a685d3e -H 'Authorization: Bearer $TOKEN'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-179a-7ab4-9d81-b6498a685d3e",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
  },
  "title": "富文本文章10-af4f49",
  "coverTitle": null,
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>纯文本无图</p>",
  "sortOrder": 0,
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ],
  "online": true,
  "createdAt": "2026-09-04T07:02:44.377297Z",
  "updatedAt": "2026-09-04T07:02:44.405302Z"
}
```
