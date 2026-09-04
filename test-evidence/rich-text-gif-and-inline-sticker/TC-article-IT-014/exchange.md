# TC-article-IT-014 请求/响应存证

GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL

执行日期: 2026-09-04 ｜ admin=http://localhost:21423（test profile） ｜ app=http://localhost:8081
认证: admin 侧 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，返回 JWT，下文以 $TOKEN 代指，`export TOKEN=<登录返回 token>` 后命令可直接执行）；app 侧请求头 X-API-Key: test-api-key（测试 fixture，明文入存证）

## Step 1: 前置：POST /api/admin/articles（online=true，contentHtml 含图与文本）

```bash
curl -s -i -X POST http://localhost:21423/api/admin/articles -H 'Content-Type: application/json' -H 'Authorization: Bearer $TOKEN' -d '{"image": "images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png", "title": "app文章14-af4f49", "subtitle": "副标题", "contentHtml": "<p>app文章段落</p><img src=\"images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1401.png\">", "sortOrder": 0, "categoryIds": ["01a06b34-0262-7574-87b4-259859cf92d1"], "online": true}'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-17c7-77c7-ba11-a2e2578b0889",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=zo6YzmZhKxqEWLtwCZOoHASz3QU%3D"
  },
  "title": "app文章14-af4f49",
  "coverTitle": null,
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>app文章段落</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1401.png?Expires=1788507164&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=sfR%2BFeOpcBNTGo1rKWRZlMvQOjw%3D\">",
  "sortOrder": 0,
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ],
  "online": true,
  "createdAt": "2026-09-04T07:02:44.423452232Z",
  "updatedAt": "2026-09-04T07:02:44.423452232Z"
}
```

## Step 2: GET http://localhost:8081/api/app/articles/01a06b3a-17c7-77c7-ba11-a2e2578b0889

```bash
curl -s -i -X GET http://localhost:8081/api/app/articles/01a06b3a-17c7-77c7-ba11-a2e2578b0889 -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200）:

```
HTTP/1.1 200
Content-Type: application/json

{
  "id": "01a06b3a-17c7-77c7-ba11-a2e2578b0889",
  "image": {
    "id": "bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png",
    "url": "https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?Expires=1788507164&OSSAccessKeyId=x&Signature=WcRgxWfZLnzWOC%2BoY%2F%2FQ%2FlzuURc%3D"
  },
  "title": "app文章14-af4f49",
  "subtitle": "副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>app文章段落</p><img src=\"https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1401.png?Expires=1788507164&OSSAccessKeyId=x&Signature=yieGIn3kRs5OINOjh1J5WktlZyo%3D\">",
  "categoryIds": [
    "01a06b34-0262-7574-87b4-259859cf92d1"
  ]
}
```
