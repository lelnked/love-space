# TC-article-IT-005 — 请求/响应存证

执行日期: 2026-08-25 ｜ admin=http://localhost:8080 ｜ app=http://localhost:8081
认证: admin 登录 POST /api/admin/auth/login（fixture: admin / 8@y2eoRLyStM*UVU，JWT 记为 $TOKEN，shell 中 export TOKEN 后命令可原样执行）；app 侧请求头 X-API-Key: $APP_API_KEY

## Step 1a: 创建栏目 A

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT005栏目A\",\"icon\":\"images/it005-a.png\",\"sortOrder\":1}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d6b8-7523-a3ab-68fffb104604",
  "name": "IT005栏目A",
  "icon": {
    "id": "bound/it005-a.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-a.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=p3kkxrh4OAU57ZUUlPKFr%2BkDK5c%3D"
  },
  "sortOrder": 1,
  "createdAt": "2026-08-25T09:22:55.288250063Z",
  "updatedAt": "2026-08-25T09:22:55.288250063Z"
}
```

## Step 1b: 创建栏目 B

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/article-categories" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"IT005栏目B\",\"icon\":\"images/it005-b.png\",\"sortOrder\":2}"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d6d4-7424-8fc1-93a5af080a9f",
  "name": "IT005栏目B",
  "icon": {
    "id": "bound/it005-b.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-b.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=UswS%2FfnzAL6IX6taxKXBEl5aswI%3D"
  },
  "sortOrder": 2,
  "createdAt": "2026-08-25T09:22:55.316195518Z",
  "updatedAt": "2026-08-25T09:22:55.316195518Z"
}
```

## Step 2: POST /api/admin/articles 创建关联 A、B 的完整文章

```bash
curl -s -i -X POST "http://localhost:8080/api/admin/articles" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"image":"images/it005-cover.png","title":"海岛两日游","subtitle":"附完整行程","contentHtml":"<p>第一天出发</p><p>第二天返程</p>","sortOrder":1,"categoryIds":["01a0383a-d6b8-7523-a3ab-68fffb104604","01a0383a-d6d4-7424-8fc1-93a5af080a9f"],"online":true}'
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d6f4-7472-86af-0a33fb35aaaf",
  "image": {
    "id": "bound/it005-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZFm7IwDAekh%2B6Xt%2BRGPnkvjLOnk%3D"
  },
  "title": "海岛两日游",
  "coverTitle": null,
  "subtitle": "附完整行程",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>第一天出发</p><p>第二天返程</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383a-d6b8-7523-a3ab-68fffb104604",
    "01a0383a-d6d4-7424-8fc1-93a5af080a9f"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:22:55.348223212Z",
  "updatedAt": "2026-08-25T09:22:55.348223212Z"
}
```

## Step 3: GET /api/admin/articles/{id}

```bash
curl -s -i "http://localhost:8080/api/admin/articles/01a0383a-d6f4-7472-86af-0a33fb35aaaf" -H "Authorization: Bearer $TOKEN"
```

实际响应（HTTP 200，Content-Type: application/json）:
```json
{
  "id": "01a0383a-d6f4-7472-86af-0a33fb35aaaf",
  "image": {
    "id": "bound/it005-cover.png",
    "url": "http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/it005-cover.png?Expires=1787651575&OSSAccessKeyId=LTAI5t7p1oaVfmzDSvrF7mQm&Signature=ZFm7IwDAekh%2B6Xt%2BRGPnkvjLOnk%3D"
  },
  "title": "海岛两日游",
  "coverTitle": null,
  "subtitle": "附完整行程",
  "intro": null,
  "tags": [],
  "contentHtml": "<p>第一天出发</p><p>第二天返程</p>",
  "sortOrder": 1,
  "categoryIds": [
    "01a0383a-d6b8-7523-a3ab-68fffb104604",
    "01a0383a-d6d4-7424-8fc1-93a5af080a9f"
  ],
  "online": true,
  "createdAt": "2026-08-25T09:22:55.348223Z",
  "updatedAt": "2026-08-25T09:22:55.348223Z"
}
```
