# TC-article-IT-008 PUT /api/admin/articles/{id}/online 文章上下线切换 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录；栏目复用 TC-article-IT-005 的栏目 B（id=01a00b98-4c92-70b3-be6d-42e449c9bdfb）

## Step 1: 前置：POST /api/admin/articles 创建 online=true 的文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it008-cover-172204.png","title":"上下线切换文章","subtitle":"x","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b98-4c92-70b3-be6d-42e449c9bdfb"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
  "image": {
    "id": "bound/it008-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=%2FAGv0xIgCtWYs29w6ZGtEBwycSk%3D"
  },
  "title": "上下线切换文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.987105135Z",
  "updatedAt": "2026-08-16T17:22:05.987105135Z"
}
```

## Step 2: PUT /api/admin/articles/{id}/online {"online": false}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a00b98-4e23-72d8-8d7c-fea9e55f868f/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":false}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
  "image": {
    "id": "bound/it008-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y3mmm5flQIjul4DgRV1HgzBWkpQ%3D"
  },
  "title": "上下线切换文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": false,
  "createdAt": "2026-08-16T17:22:05.987105Z",
  "updatedAt": "2026-08-16T17:22:05.987105Z"
}
```

## Step 3: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4e23-72d8-8d7c-fea9e55f868f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
  "image": {
    "id": "bound/it008-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y3mmm5flQIjul4DgRV1HgzBWkpQ%3D"
  },
  "title": "上下线切换文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": false,
  "createdAt": "2026-08-16T17:22:05.987105Z",
  "updatedAt": "2026-08-16T17:22:06.024641Z"
}
```

## Step 4: PUT /api/admin/articles/{id}/online {"online": true}

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a00b98-4e23-72d8-8d7c-fea9e55f868f/online" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
  "image": {
    "id": "bound/it008-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y3mmm5flQIjul4DgRV1HgzBWkpQ%3D"
  },
  "title": "上下线切换文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.987105Z",
  "updatedAt": "2026-08-16T17:22:06.024641Z"
}
```

## Step 5: GET /api/admin/articles/{id} 再查详情

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4e23-72d8-8d7c-fea9e55f868f" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4e23-72d8-8d7c-fea9e55f868f",
  "image": {
    "id": "bound/it008-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it008-cover-172204.png?Expires=1786902726&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=y3mmm5flQIjul4DgRV1HgzBWkpQ%3D"
  },
  "title": "上下线切换文章",
  "subtitle": "x",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4c92-70b3-be6d-42e449c9bdfb"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.987105Z",
  "updatedAt": "2026-08-16T17:22:06.082943Z"
}
```
