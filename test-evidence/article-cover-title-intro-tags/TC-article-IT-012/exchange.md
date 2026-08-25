# TC-article-IT-012 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT012栏目（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT012栏目","icon":"images/it012-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4ef3-7439-827c-9b7233a1de86",
  "name": "IT012栏目",
  "icon": {
    "id": "bound/it012-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-a.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=u4L6q%2BoOwAE7sq88lSd5zaGzenM%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:31.603214999Z",
  "updatedAt": "2026-08-25T09:24:31.603214999Z"
}
```

## 前置: 创建上线文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it012-cover.png","title":"IT012文章","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383c-4ef3-7439-827c-9b7233a1de86"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7",
  "image": {
    "id": "bound/it012-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-cover.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jjQh253y8jwRzCMZ4C0wll7prq4%3D"
  },
  "title": "IT012文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-4ef3-7439-827c-9b7233a1de86"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:31.626590694Z",
  "updatedAt": "2026-08-25T09:24:31.626590694Z"
}
```

## Step 1: GET /api/app/articles 确认上线时可见

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a0383c-4ef3-7439-827c-9b7233a1de86" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[
  {
    "id": "01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7",
    "image": {
      "id": "bound/it012-cover.png",
      "url": "https://love-space-test-0524.oss-test.example.com/bound/it012-cover.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=KXRLyuNLykhURbjQe6wR4J%2FIFfg%3D"
    },
    "coverTitle": "IT012文章",
    "title": "IT012文章",
    "subtitle": null,
    "tags": []
  }
]
```

## Step 2: admin PUT /api/admin/articles/{id}/online 下线

```bash
curl -s -i -X PUT "http://localhost:8080/api/admin/articles/01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7/online" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7",
  "image": {
    "id": "bound/it012-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it012-cover.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=jjQh253y8jwRzCMZ4C0wll7prq4%3D"
  },
  "title": "IT012文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-4ef3-7439-827c-9b7233a1de86"
  ],
  "online": false,
  "createdAt": "2026-08-25T09:24:31.626591Z",
  "updatedAt": "2026-08-25T09:24:31.675866169Z"
}
```

## Step 3: GET /api/app/articles 下线后列表

```bash
curl -s -i "http://localhost:8081/api/app/articles?categoryId=01a0383c-4ef3-7439-827c-9b7233a1de86" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
[]
```

## Step 4: GET /api/app/articles/{id} 下线后详情

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 404，Content-Type: application/json）:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "article not found: 01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7",
  "path": "/api/app/articles/01a0383c-4f0a-7a0e-bbc6-6f25ded8ecc7"
}
```
