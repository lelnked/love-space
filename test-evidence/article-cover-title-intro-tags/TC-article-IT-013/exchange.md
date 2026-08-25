# TC-article-IT-013 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## 前置: 创建栏目 IT013栏目A（sortOrder=1）

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"name":"IT013栏目A","icon":"images/it013-a.png","sortOrder":1}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4f81-74cf-bb0e-3c135a8d4d32",
  "name": "IT013栏目A",
  "icon": {
    "id": "bound/it013-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-a.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=k6itfaZnSK3BJeDH8JclaVgN3wo%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:24:31.745247451Z",
  "updatedAt": "2026-08-25T09:24:31.745247451Z"
}
```

## 前置: 创建仅关联栏目A的上线文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it013-cover.png","title":"IT013文章","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a0383c-4f81-74cf-bb0e-3c135a8d4d32"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4f9a-71a1-9f37-4c556a73be9a",
  "image": {
    "id": "bound/it013-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-cover.png?Expires=1787651671&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=Wrs4zBtFhw2rEq4bxdUiLLChHNA%3D"
  },
  "title": "IT013文章",
  "coverTitle": null,
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383c-4f81-74cf-bb0e-3c135a8d4d32"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:24:31.770049245Z",
  "updatedAt": "2026-08-25T09:24:31.770049245Z"
}
```

## Step 1: GET /api/app/articles/{id} 删除栏目前

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-4f9a-71a1-9f37-4c556a73be9a" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383c-4f9a-71a1-9f37-4c556a73be9a",
  "image": {
    "id": "bound/it013-cover.png",
    "url": "https://love-space-test-0524.oss-test.example.com/bound/it013-cover.png?Expires=1787651671&OSSAccessKeyId=test-ak&Signature=Gk2oWDAzpzHyFjB4I%2BjE8MmOwcA%3D"
  },
  "title": "IT013文章",
  "subtitle": null,
  "intro": null,
  "tags": [],
  "contentHtml": "<p>正文</p>",
  "categoryIds": [
    "01a0383c-4f81-74cf-bb0e-3c135a8d4d32"
  ]
}
```

## Step 2: admin DELETE /api/admin/article-categories/{A}

```bash
curl -s -i -X DELETE "http://localhost:8080/api/admin/article-categories/01a0383c-4f81-74cf-bb0e-3c135a8d4d32" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，）:
```json
```

## Step 3: GET /api/app/articles/{id} 删除栏目后

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a0383c-4f9a-71a1-9f37-4c556a73be9a" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 404，Content-Type: application/json）:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "article not found: 01a0383c-4f9a-71a1-9f37-4c556a73be9a",
  "path": "/api/app/articles/01a0383c-4f9a-71a1-9f37-4c556a73be9a"
}
```
