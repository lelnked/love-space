# TC-article-IT-007 PUT /api/admin/articles/{id} 更新文章与栏目关联 — 请求/响应存证

执行日期: 2026-08-16 ｜ admin=http://localhost:21423 ｜ app=http://localhost:8081
认证: admin 统一登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: test-api-key
> token 复用本轮统一登录

## Step 1: 前置 1/3：创建栏目 A

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目A-007","icon":"images/it007-a-172204.png","sortOrder":31}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4d86-7f01-9a93-17502d34993b",
  "name": "栏目A-007",
  "icon": {
    "id": "bound/it007-a-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-a-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=8Q%2FbqCrCl7QOK%2FRoBHLUaLb7RMw%3D"
  },
  "sortOrder": 31,
  "createdAt": "2026-08-16T17:22:05.830862242Z",
  "updatedAt": "2026-08-16T17:22:05.830862242Z"
}
```

## Step 2: 前置 2/3：创建栏目 B

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name":"栏目B-007","icon":"images/it007-b-172204.png","sortOrder":32}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4da3-77be-8a25-7118fbe9b7df",
  "name": "栏目B-007",
  "icon": {
    "id": "bound/it007-b-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-b-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=lCMhgVXQtBIEJF1EAB8p1DTBoCs%3D"
  },
  "sortOrder": 32,
  "createdAt": "2026-08-16T17:22:05.859419612Z",
  "updatedAt": "2026-08-16T17:22:05.859419612Z"
}
```

## Step 3: 前置 3/3：创建关联 [A] 的文章

```bash
curl -s -i -X POST "http://localhost:21423/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it007-cover-172204.png","title":"更新前标题","subtitle":"更新前副标题","contentHtml":"<p>正文</p>","sortOrder":1,"categoryIds":["01a00b98-4d86-7f01-9a93-17502d34993b"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4dc1-72a7-9141-7ea6ad0368c6",
  "image": {
    "id": "bound/it007-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WmZx5Pb46UWP9BApeEFOS8lrd%2Fw%3D"
  },
  "title": "更新前标题",
  "subtitle": "更新前副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a00b98-4d86-7f01-9a93-17502d34993b"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.889099544Z",
  "updatedAt": "2026-08-16T17:22:05.889099544Z"
}
```

## Step 4: PUT /api/admin/articles/{id} 改 title/subtitle/sortOrder=9/categoryIds=[B]

```bash
curl -s -i -X PUT "http://localhost:21423/api/admin/articles/01a00b98-4dc1-72a7-9141-7ea6ad0368c6" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"image":"images/it007-cover-172204.png","title":"更新后标题","subtitle":"更新后副标题","contentHtml":"<p>正文</p>","sortOrder":9,"categoryIds":["01a00b98-4da3-77be-8a25-7118fbe9b7df"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4dc1-72a7-9141-7ea6ad0368c6",
  "image": {
    "id": "bound/it007-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WmZx5Pb46UWP9BApeEFOS8lrd%2Fw%3D"
  },
  "title": "更新后标题",
  "subtitle": "更新后副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 9,
  "categoryIds": [
    "01a00b98-4da3-77be-8a25-7118fbe9b7df"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.8891Z",
  "updatedAt": "2026-08-16T17:22:05.8891Z"
}
```

## Step 5: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:21423/api/admin/articles/01a00b98-4dc1-72a7-9141-7ea6ad0368c6" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:

```json
{
  "id": "01a00b98-4dc1-72a7-9141-7ea6ad0368c6",
  "image": {
    "id": "bound/it007-cover-172204.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it007-cover-172204.png?Expires=1786902725&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=WmZx5Pb46UWP9BApeEFOS8lrd%2Fw%3D"
  },
  "title": "更新后标题",
  "subtitle": "更新后副标题",
  "contentHtml": "<p>正文</p>",
  "sortOrder": 9,
  "categoryIds": [
    "01a00b98-4da3-77be-8a25-7118fbe9b7df"
  ],
  "online": true,
  "createdAt": "2026-08-16T17:22:05.8891Z",
  "updatedAt": "2026-08-16T17:22:05.922094Z"
}
```
