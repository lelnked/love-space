# TC-article-IT-014 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT014栏目（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT014栏目","icon":"images/it014-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5005-760f-86d0-afaa41e42ed8",
  "name": "IT014栏目",
  "icon": {
    "id": "bound/it014-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-a.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=RL%2FG1%2BJu9a0QhcJ%2BcU%2FVpeMUqrs%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:31.877309617Z",
  "updatedAt": "2026-08-25T09:24:31.877309617Z"
}
```

## 前置: 创建含图片富文本的上线文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it014-cover.png","title":"IT014文章","subtitle":"IT014副标题","contentHtml":"<p>段落甲</p><img src=\"images/it014-p1.png\"><p>段落乙</p>","sortOrder":1,"categoryIds":["01a0383c-5005-760f-86d0-afaa41e42ed8"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5023-7dbb-a99c-f3309b542182",
  "image": {
    "id": "bound/it014-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-cover.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=IPL6YGrQU3whACUNwgxxIBOuzgk%3D"
  },
  "title": "IT014文章",
  "coverTitle": null,
  "subtitle": "IT014副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>段落甲</p><img src=\"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it014-p1.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=1AbsXQHmmJVJ9nT48T7dMsxPHG0%3D\"><p>段落乙</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-5005-760f-86d0-afaa41e42ed8"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:31.907801007Z",
  "updatedAt": "2026-08-25T09:24:31.907801007Z"
}
```

## Step 2: GET /api/app/articles/{id}

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-5023-7dbb-a99c-f3309b542182" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-5023-7dbb-a99c-f3309b542182",
  "image": {
    "id": "bound/it014-cover.png",
    "url": "https://love-space-test-0524.oss-test.example.com/bound/it014-cover.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=DxBeetpySJhRs8TNLO%2FrwHXqkSc%3D"
  },
  "title": "IT014文章",
  "subtitle": "IT014副标题",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>段落甲</p><img src=\"https://love-space-test-0524.oss-test.example.com/bound/it014-p1.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=qJ8G%2F3UoO0zljTVvOdWlStqDhjg%3D\"><p>段落乙</p>",
  "categoryIds": [
    "01a0383c-5005-760f-86d0-afaa41e42ed8"
  ]
}
```
