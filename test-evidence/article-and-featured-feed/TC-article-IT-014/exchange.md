# TC-article-IT-014 GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；栏目复用 TC-article-IT-005 的栏目 B（id=01a00b98-4c92-70b3-be6d-42e449c9bdfb）

## Step 1: 前置：POST /api/admin/articles 创建含 img 富文本的可见文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it014-cover-172204.png","title":"富文本详情文章","subtitle":"富文本副标题","contentHtml":"<p>正文文字十四</p><img src=\"images/it014-a-172204.png\">","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5191-79ce-987b-e52dec39a9d3",
  "image": {
    "id": "bound/it014-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ee1l3sJYWR9gH3JYvSHcp0sfI04%3D"
  },
  "title": "富文本详情文章",
  "subtitle": "富文本副标题",
  "contentHtml": "<p>正文文字十四</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7qHagEjf%2FewbqU312%2B8FK4beBpc%3D\">",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.865542091Z",
  "updatedAt": "2026-08-16T17:22:06.865542091Z"
}
```

## Step 2: GET /api/app/articles/{id}（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a00b98-5191-79ce-987b-e52dec39a9d3" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5191-79ce-987b-e52dec39a9d3",
  "image": {
    "id": "bound/it014-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Ee1l3sJYWR9gH3JYvSHcp0sfI04%3D"
  },
  "title": "富文本详情文章",
  "subtitle": "富文本副标题",
  "contentHtml": "<p>正文文字十四</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=7qHagEjf%2FewbqU312%2B8FK4beBpc%3D\">",
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ]
}
```
