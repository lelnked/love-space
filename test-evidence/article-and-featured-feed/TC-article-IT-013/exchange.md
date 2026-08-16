# TC-article-IT-013 GET /api/app/articles/{id} 失去所有栏目的文章不可见 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录

## Step 1: 前置 1/2：创建栏目 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"孤栏目-013","icon":"images/it013-a-172204.png","sortOrder":41}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-510a-76c6-9638-b79b0eca6989",
  "name": "孤栏目-013",
  "icon": {
    "id": "bound/it013-a-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-a-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=9BUD%2BL2SDHC9dTFtGnQXAfTPizg%3D"
  },
  "sortOrder": 41,
  "createdAt": "2026-08-16T17:22:06.730354762Z",
  "updatedAt": "2026-08-16T17:22:06.730354762Z"
}
```

## Step 2: 前置 2/2：创建仅关联 A 的上线文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it013-cover-172204.png","title":"失去栏目文章","subtitle":"x","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b98-510a-76c6-9638-b79b0eca6989"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5127-7806-bcb3-341d89a1e48e",
  "image": {
    "id": "bound/it013-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uOddiuguaBQn9k2fJ9Q8JJ5hDYI%3D"
  },
  "title": "失去栏目文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-510a-76c6-9638-b79b0eca6989"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:06.759424201Z",
  "updatedAt": "2026-08-16T17:22:06.759424201Z"
}
```

## Step 3: 前置确认：GET /api/app/articles/{id} 返回 200

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a00b98-5127-7806-bcb3-341d89a1e48e" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-5127-7806-bcb3-341d89a1e48e",
  "image": {
    "id": "bound/it013-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it013-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=uOddiuguaBQn9k2fJ9Q8JJ5hDYI%3D"
  },
  "title": "失去栏目文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "categoryIds": [
    "01a00b98-510a-76c6-9638-b79b0eca6989"
  ]
}
```

## Step 4: DELETE /api/admin/article-categories/{A}

```bash
curl -s -i -X DELETE "http://localhost:21423/api/admin/article-categories/01a00b98-510a-76c6-9638-b79b0eca6989" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，无 Content-Type）:

```json

```

## Step 5: GET /api/app/articles/{id}（X-API-Key）

```bash
curl -s -i "http://localhost:8081/api/app/articles/01a00b98-5127-7806-bcb3-341d89a1e48e" -H 'X-API-Key: test-api-key'
```

实际响应（HTTP 404，Content-Type: application/json）:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "article not found: 01a00b98-5127-7806-bcb3-341d89a1e48e",
  "path": "/api/app/articles/01a00b98-5127-7806-bcb3-341d89a1e48e"
}
```
